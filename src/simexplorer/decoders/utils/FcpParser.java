package simexplorer.decoders.utils;

import simexplorer.files.AccessConditions;
import simexplorer.files.AccessConditionsMap;
import simexplorer.files.EF;
import simexplorer.files.IncreaseCommandAllowed;
import simexplorer.files.TypeOfFile;

import java.util.ArrayList;
import java.util.List;

public final class FcpParser {

    private FcpParser() {}

    public static final class Result {
        public int fileSize;
        public EF.StructureOfFile structureOfFile = EF.StructureOfFile.Invalid;
        public TypeOfFile typeOfFile = TypeOfFile.INVALID;

        public IncreaseCommandAllowed increaseCommandAllowed = IncreaseCommandAllowed.NOT_APPLY;

        public AccessConditions updateAccessConditions;
        public AccessConditions readSeekAccessConditions;
        public AccessConditions increaseAccessConditions;
        public AccessConditions invalidateAccessConditions;
        public AccessConditions rehabilitateAccessConditions;

        public boolean invalidated = false;
        public boolean readableAndUpdatableWhenInvalidated = false;

        public int recordLength = 0;
        public int numRecords = 0;

        public byte[] rawFileDescriptor;
        public byte[] rawCompactSecurityAttributes;
        public Integer lifeCycleStatusInteger;
        public byte[] rawFileIdentifier;

        public boolean isCHV1Blocked;
        public boolean isCHV1Enabled;
        public boolean isUnblockCHV1Blocked;
        public boolean isCHV2Blocked;
        public boolean isUnblockCHV2Blocked;

        public byte[] rawPinStatusTemplate;
        public Byte rawFileCharacteristics;
    }

    public static Result parseSelectResponse(byte[] selectResponse) {
        if (selectResponse == null || selectResponse.length < 4) {
            throw new IllegalArgumentException("SELECT response is null/too short");
        }

        int dataLen = selectResponse.length - 2;
        List<BerTLV> level1 = decodeTopLevelFcp(selectResponse, dataLen);

        byte[] fileDescriptor = null;
        byte[] compactSecurity = null;
        byte[] fileId = null;
        Integer fileSize = null;
        Integer lifeCycle = null;

        byte[] pinStatusTemplate = null;
        Byte fileCharacteristics = null;

        TypeOfFile typeOfFile = TypeOfFile.INVALID;

        for (BerTLV t : level1) {
            int tag = t.getTag() & 0xFF;

            if (tag == 0x80 || tag == 0x81) {
                fileSize = parseBigEndianInt(HexUtil.hexStringToByteArray(t.getData()));
            } else if (tag == 0x82) {
                fileDescriptor = HexUtil.hexStringToByteArray(t.getData());
                if (typeOfFile == TypeOfFile.INVALID) {
                    TypeOfFile fromFd = parseTypeFromFileDescriptor(fileDescriptor);
                    if (fromFd != null) typeOfFile = fromFd;
                }
            } else if (tag == 0x8C) {
                compactSecurity = HexUtil.hexStringToByteArray(t.getData());
            } else if (tag == 0x8A) {
                byte[] v = HexUtil.hexStringToByteArray(t.getData());
                if (v.length > 0) lifeCycle = (v[0] & 0xFF);
            } else if (tag == 0x83) {
                fileId = HexUtil.hexStringToByteArray(t.getData());
                if (typeOfFile == TypeOfFile.INVALID) {
                    TypeOfFile fromFid = parseTypeFromFileId(fileId);
                    if (fromFid != null) typeOfFile = fromFid;
                }
            } else if (tag == 0x8B) {
                byte[] v = HexUtil.hexStringToByteArray(t.getData());
                if (v.length > 0) fileCharacteristics = v[0];
            } else if (tag == 0xC6) {
                pinStatusTemplate = HexUtil.hexStringToByteArray(t.getData());
            } else if (tag == 0xA5) {
                if (compactSecurity == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x8C);
                    if (fromA5 != null) compactSecurity = fromA5;
                }

                if (lifeCycle == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x8A);
                    if (fromA5 != null && fromA5.length > 0) lifeCycle = (fromA5[0] & 0xFF);
                }

                if (fileId == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x83);
                    if (fromA5 != null) fileId = fromA5;
                }

                if (fileDescriptor == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x82);
                    if (fromA5 != null) fileDescriptor = fromA5;
                }

                if (fileSize == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x80);
                    if (fromA5 != null) fileSize = parseBigEndianInt(fromA5);
                }
                if (fileSize == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x81);
                    if (fromA5 != null) fileSize = parseBigEndianInt(fromA5);
                }

                if (pinStatusTemplate == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0xC6);
                    if (fromA5 != null) pinStatusTemplate = fromA5;
                }

                if (fileCharacteristics == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x8B);
                    if (fromA5 != null && fromA5.length > 0) fileCharacteristics = fromA5[0];
                }

                if (typeOfFile == TypeOfFile.INVALID) {
                    TypeOfFile fromFid = parseTypeFromFileId(fileId);
                    if (fromFid != null) typeOfFile = fromFid;
                }

                if (typeOfFile == TypeOfFile.INVALID) {
                    TypeOfFile fromFd = parseTypeFromFileDescriptor(fileDescriptor);
                    if (fromFd != null) typeOfFile = fromFd;
                }
            }
        }

        if (fileSize == null) {
            throw new IllegalArgumentException("File size (0x80/0x81) not found in FCP");
        }

        Result r = new Result();
        r.fileSize = fileSize;

        r.rawFileDescriptor = fileDescriptor;
        r.rawCompactSecurityAttributes = compactSecurity;
        r.lifeCycleStatusInteger = lifeCycle;
        r.rawFileIdentifier = fileId;

        r.rawPinStatusTemplate = pinStatusTemplate;
        r.rawFileCharacteristics = fileCharacteristics;

        r.typeOfFile = typeOfFile;

        applyStructureAndRecords(r, fileDescriptor);
        applyIncreaseAllowed(r, fileDescriptor);
        applyAccessConditions(r, compactSecurity);
        applyLifeCycle(r, lifeCycle);

        if (r.typeOfFile == TypeOfFile.DF) {
            applyDfPinStuff(r, pinStatusTemplate, fileCharacteristics);
        }

        return r;
    }

    private static void applyDfPinStuff(Result r, byte[] pinStatusTemplate, Byte fileCharacteristics) {
        if (fileCharacteristics != null) {
            r.isCHV1Enabled = ((fileCharacteristics.byteValue() & 0x80) != (byte) 0x80);
        }

        if (pinStatusTemplate == null) return;

        if (pinStatusTemplate.length > 0) {
            byte chv1Status = pinStatusTemplate[0];
            r.isCHV1Blocked = ((chv1Status & 0x0F) == 0);
        }
        if (pinStatusTemplate.length > 1) {
            byte puk1Status = pinStatusTemplate[1];
            r.isUnblockCHV1Blocked = ((puk1Status & 0x0F) == 0);
        }
        if (pinStatusTemplate.length > 2) {
            byte chv2Status = pinStatusTemplate[2];
            r.isCHV2Blocked = ((chv2Status & 0x0F) == 0);
        }
        if (pinStatusTemplate.length > 3) {
            byte puk2Status = pinStatusTemplate[3];
            r.isUnblockCHV2Blocked = ((puk2Status & 0x0F) == 0);
        }
    }

    private static TypeOfFile parseTypeFromFileId(byte[] fileId) {
        if (fileId == null || fileId.length != 2) return null;
        int fid = ((fileId[0] & 0xFF) << 8) | (fileId[1] & 0xFF);
        if (fid == 0x3F00) return TypeOfFile.MF;
        return null;
    }

    private static TypeOfFile parseTypeFromFileDescriptor(byte[] fd) {
        if (fd == null || fd.length < 1) return null;

        int b1 = fd[0] & 0xFF;
        int fileType = (b1 >> 3) & 0x07;
        int efStruct = b1 & 0x07;

        if (fileType == 0x00 || fileType == 0x01) return TypeOfFile.EF;
        if (fileType == 0x07) return TypeOfFile.DF;

        if (efStruct == 0x01 || efStruct == 0x02 || efStruct == 0x04 || efStruct == 0x06) return TypeOfFile.EF;

        return null;
    }

    private static List<BerTLV> decodeTopLevelFcp(byte[] selectResponse, int dataLen) {
        int start = indexOfTag(selectResponse, dataLen, 0x62);
        if (start < 0) {
            throw new IllegalArgumentException("FCP template (0x62) not found");
        }

        byte[] fcpBytes = slice(selectResponse, start, dataLen);
        BerTLV top = TlvUtil.decodeBerTLV(HexUtil.byteArrayToHexString(fcpBytes));
        if ((top.getTag() & 0xFF) != 0x62) {
            throw new IllegalArgumentException("Top TLV is not 0x62");
        }

        return safeDecodeAll(top.getData());
    }

    private static void applyStructureAndRecords(Result r, byte[] fileDescriptor) {
        r.structureOfFile = EF.StructureOfFile.Invalid;
        r.recordLength = 0;
        r.numRecords = 0;

        if (fileDescriptor == null || fileDescriptor.length < 1) return;

        int b1 = fileDescriptor[0] & 0xFF;
        int structureBits = b1 & 0x07;

        if (structureBits == 0x01) r.structureOfFile = EF.StructureOfFile.Transparent;
        else if (structureBits == 0x02) r.structureOfFile = EF.StructureOfFile.LinearFixed;
        else if (structureBits == 0x04 || structureBits == 0x06) r.structureOfFile = EF.StructureOfFile.Cyclic;
        else r.structureOfFile = EF.StructureOfFile.Invalid;

        if (r.structureOfFile == EF.StructureOfFile.LinearFixed || r.structureOfFile == EF.StructureOfFile.Cyclic) {
            if (fileDescriptor.length >= 3) {
                r.recordLength = fileDescriptor[2] & 0xFF;
            }
            if (fileDescriptor.length >= 5) {
                int nrec = ((fileDescriptor[3] & 0xFF) << 8) | (fileDescriptor[4] & 0xFF);
                if (nrec > 0) r.numRecords = nrec;
            }
            if (r.numRecords <= 0 && r.recordLength > 0) {
                r.numRecords = r.fileSize / r.recordLength;
            }
        }
    }

    private static void applyIncreaseAllowed(Result r, byte[] fileDescriptor) {
        if (r.structureOfFile != EF.StructureOfFile.Cyclic) {
            r.increaseCommandAllowed = IncreaseCommandAllowed.NOT_APPLY;
            return;
        }

        boolean allowed = false;
        if (fileDescriptor != null && fileDescriptor.length >= 2) {
            allowed = (fileDescriptor[1] & 0x40) == 0x40;
        }
        r.increaseCommandAllowed = allowed ? IncreaseCommandAllowed.TRUE : IncreaseCommandAllowed.FALSE;
    }

    private static void applyAccessConditions(Result r, byte[] compactSecurity) {
        if (compactSecurity == null || compactSecurity.length < 3) return;

        byte b0 = compactSecurity[0];
        byte b1 = compactSecurity[1];
        byte b2 = compactSecurity[2];

        byte update = (byte) (b0 & 0x0F);
        byte readSeek = (byte) ((b0 >> 4) & 0x0F);
        byte increase = (byte) ((b1 >> 4) & 0x0F);
        byte invalidate = (byte) (b2 & 0x0F);
        byte rehabilitate = (byte) ((b2 >> 4) & 0x0F);

        r.updateAccessConditions = AccessConditionsMap.getAcessConditions(update);
        r.readSeekAccessConditions = AccessConditionsMap.getAcessConditions(readSeek);
        r.increaseAccessConditions = AccessConditionsMap.getAcessConditions(increase);
        r.invalidateAccessConditions = AccessConditionsMap.getAcessConditions(invalidate);
        r.rehabilitateAccessConditions = AccessConditionsMap.getAcessConditions(rehabilitate);
    }

    private static void applyLifeCycle(Result r, Integer lifeCycle) {
        r.invalidated = false;
        r.readableAndUpdatableWhenInvalidated = false;

        if (lifeCycle == null) return;

        int lc = lifeCycle & 0xFF;
        if (lc == 0x07 || lc == 0x0F) {
            r.invalidated = true;
        }
    }

    private static int indexOfTag(byte[] data, int limitExclusive, int tag) {
        int t = tag & 0xFF;
        for (int i = 0; i < limitExclusive; i++) {
            if ((data[i] & 0xFF) == t) return i;
        }
        return -1;
    }

    private static byte[] slice(byte[] data, int fromInclusive, int toExclusive) {
        int n = Math.max(0, toExclusive - fromInclusive);
        byte[] out = new byte[n];
        System.arraycopy(data, fromInclusive, out, 0, n);
        return out;
    }

    private static int parseBigEndianInt(byte[] v) {
        int r = 0;
        for (byte b : v) r = (r << 8) | (b & 0xFF);
        return r;
    }

    private static List<BerTLV> safeDecodeAll(String hexData) {
        List<BerTLV> out = TlvUtil.decodeAll(hexData);
        return out != null ? out : new ArrayList<>();
    }

    private static byte[] findTagValueInConstructed(BerTLV constructed, int wantedTag) {
        if (constructed == null) return null;

        List<BerTLV> inner = safeDecodeAll(constructed.getData());
        for (BerTLV t : inner) {
            if ((t.getTag() & 0xFF) == (wantedTag & 0xFF)) {
                return HexUtil.hexStringToByteArray(t.getData());
            }
            if ((t.getTag() & 0xFF) == 0xA5) {
                byte[] nested = findTagValueInConstructed(t, wantedTag);
                if (nested != null) return nested;
            }
        }
        return null;
    }
}
