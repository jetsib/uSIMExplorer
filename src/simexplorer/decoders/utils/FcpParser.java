package simexplorer.decoders.utils;

import simexplorer.files.AccessConditions;
import simexplorer.files.AccessConditionsMap;
import simexplorer.files.EF;
import simexplorer.files.IncreaseCommandAllowed;

import java.util.ArrayList;
import java.util.List;

public final class FcpParser {

    private FcpParser() {}

    public static final class Result {
        public int fileSize;
        public EF.StructureOfFile structureOfFile = EF.StructureOfFile.Invalid;

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
    }

    public static Result parseSelectResponse(byte[] selectResponse) {
        if (selectResponse == null || selectResponse.length < 4) {
            throw new IllegalArgumentException("SELECT response is null/too short");
        }

        int dataLen = selectResponse.length - 2;

        int start = indexOfTag(selectResponse, dataLen, 0x62);
        if (start < 0) {
            throw new IllegalArgumentException("FCP template (0x62) not found");
        }

        byte[] fcpBytes = slice(selectResponse, start, dataLen);
        BerTLV top = TlvUtil.decodeBerTLV(HexUtil.byteArrayToHexString(fcpBytes));
        if ((top.getTag() & 0xFF) != 0x62) {
            throw new IllegalArgumentException("Top TLV is not 0x62");
        }

        List<BerTLV> level1 = safeDecodeAll(top.getData());

        byte[] fileDescriptor = null;
        byte[] compactSecurity = null;
        Integer fileSize = null;
        Integer lifeCycle = null;

        for (BerTLV t : level1) {
            int tag = t.getTag() & 0xFF;

            if (tag == 0x80 || tag == 0x81) {
                fileSize = parseBigEndianInt(HexUtil.hexStringToByteArray(t.getData()));
            } else if (tag == 0x82) {
                fileDescriptor = HexUtil.hexStringToByteArray(t.getData());
            } else if (tag == 0x8C) {
                compactSecurity = HexUtil.hexStringToByteArray(t.getData());
            } else if (tag == 0x8A) {
                byte[] v = HexUtil.hexStringToByteArray(t.getData());
                if (v.length > 0) lifeCycle = (v[0] & 0xFF);
            } else if (tag == 0xA5) {
                if (compactSecurity == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x8C);
                    if (fromA5 != null) compactSecurity = fromA5;
                }
                if (lifeCycle == null) {
                    byte[] fromA5 = findTagValueInConstructed(t, 0x8A);
                    if (fromA5 != null && fromA5.length > 0) lifeCycle = (fromA5[0] & 0xFF);
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

        applyStructureAndRecords(r, fileDescriptor);
        applyIncreaseAllowed(r, fileDescriptor);
        applyAccessConditions(r, compactSecurity);
        applyLifeCycle(r, lifeCycle);

        return r;
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
