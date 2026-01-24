package simexplorer.utils;

import java.util.ArrayList;
import java.util.List;

public class TlvUtil {

    public static String decodeRawTLV(String strTLV) {
        BerTLV berTLV = new BerTLV();

        int skip = 0;
        berTLV.setTag(0);
        skip += 2; // skip TAG

        if (HexUtil.atoi(strTLV.substring(skip, skip + 2)) < 0x80) {
            berTLV.setLen(HexUtil.atoi(strTLV.substring(skip, skip + 2)));
            skip += 2; // skip 1 byte len
        } else {
            byte first = (byte) HexUtil.atoi(strTLV.substring(skip, skip + 2));
            int octets = (first ^ (byte) 0x80);
            berTLV.setLen(getEncodedLength(HexUtil.hexStringToByteArray(strTLV.substring(skip, skip + octets * 2))));
            skip += (octets + 1) * 2; // skip (first+octets)*2
        }

        berTLV.setData(strTLV.substring(skip, skip + 2 * berTLV.getLen()));

        return berTLV.getData();
    }

    public static BerTLV decodeBerTLV(String strTLV) {
        BerTLV berTLV = new BerTLV();

        int skip = 0;
        berTLV.setTag(HexUtil.atoi(strTLV.substring(skip, skip + 2)));
        skip += 2; // skip TAG

        if (HexUtil.atoi(strTLV.substring(skip, skip + 2)) < 0x80) {
            berTLV.setLen(HexUtil.atoi(strTLV.substring(skip, skip + 2)));
            skip += 2; // skip 1 byte len
        } else {
            byte first = (byte) HexUtil.atoi(strTLV.substring(skip, skip + 2));
            int len = (first ^ (byte) 0x80) + 1;
            berTLV.setLen(getEncodedLength(HexUtil.hexStringToByteArray(strTLV.substring(skip, skip + len * 2))));
            skip += len * 2; // skip (first+octets)*2
        }

        berTLV.setData(strTLV.substring(skip, skip + 2 * berTLV.getLen()));

        return berTLV;
    }

    private static int getEncodedLength(byte byteArray[]) {
        byte first = byteArray[0];
        if ((first & 0x80) == 0x00) {
            return first;
        }
        int octets = (first ^ (byte) 0x80);
        if (octets > 3) {
            throw new IllegalArgumentException("Encoded length has too many octets");
        }
        int result = 0;
        for (int i = 1; i <= octets; i++) {
            byte next = byteArray[i];
            result |= (next & 0xff) << ((octets - i) * 8);
        }
        return result;
    }

    public static int berTLVBytesLen(BerTLV berTLV) {
        int TAG_LEN = 1;
        int LENGTH_LEN = 1;

        if (berTLV.getLen() >= 128 && berTLV.getLen() <= 255) {
            LENGTH_LEN = 2;
        }
        if (berTLV.getLen() >= 256 && berTLV.getLen() <= 65535) {
            LENGTH_LEN = 3;
        }
        if (berTLV.getLen() >= 65536 && berTLV.getLen() <= 16777215) {
            LENGTH_LEN = 4;
        }

        return (TAG_LEN + LENGTH_LEN + berTLV.getLen()) * 2;
    }

    public static int berTLVMetaBytesLen(BerTLV berTLV) {
        int TAG_LEN = 1;
        int LENGTH_LEN = 1;

        if (berTLV.getLen() >= 128 && berTLV.getLen() <= 255) {
            LENGTH_LEN = 2;
        }
        if (berTLV.getLen() >= 256 && berTLV.getLen() <= 65535) {
            LENGTH_LEN = 3;
        }
        if (berTLV.getLen() >= 65536 && berTLV.getLen() <= 16777215) {
            LENGTH_LEN = 4;
        }

        return (TAG_LEN + LENGTH_LEN) * 2;
    }

    public static String toBerTLV(String tag, String data) {
        return toComprehensionTLV(tag,data);
    }

    public static String encodeLength(int length){
        String lenStr=HexUtil.itoa(length,1);
        if (length >0xFFFF)
            return "83"+lenStr;
        else if (length > 0xFF)
            return "82"+lenStr;
        else if (length > 0x7F)
            return "81"+lenStr;
        return lenStr;
    }
    public static String toComprehensionTLV(String tag, String data) {
        return tag+encodeLength(data.length()/2)+data;
    }

    public static List<BerTLV> decodeAll(String strTLV) {
        List<BerTLV> list = new ArrayList<>();
        int offset = 0;
        while (offset < strTLV.length()) {
            BerTLV tlv = decodeBerTLV(strTLV.substring(offset));
            list.add(tlv);
            offset += berTLVBytesLen(tlv);
        }
        return list;
    }
}
