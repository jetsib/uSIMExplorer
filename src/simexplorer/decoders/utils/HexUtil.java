package simexplorer.decoders.utils;

import java.io.ByteArrayOutputStream;

public class HexUtil {

    private static final char HEX_CHARS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    //

    public static String toHexString(final byte b) {
        return new String(new char[]{'0', 'x', HEX_CHARS[(b & 0xf0) >> 4], HEX_CHARS[b & 0x0f]});
    }

    public static String toHexString(String num) {
        String hex = Integer.toHexString(Integer.valueOf(num));

        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }

        return hex.toUpperCase();
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);

        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }

        return sb.toString().toUpperCase();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static char[] hexStringToCharArray(String s) {
        int len = s.length();
        char[] data = new char[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (char) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static int atoi(String s) {
        return Integer.valueOf(s, 16);
    }

    public static String itoa(int i, int num) {
        String target = Integer.toHexString(i);

        if (target.length() > num * 2 || i < 0) {
            // throw new Exception("");
        }

        if (target.length() % 2 == 1) {
            target = "0" + target;
        }

        while (target.length() < num * 2) {
            for (int j = 0; j < num * 2 - target.length(); j++) {
                target = "0" + target;
            }
        }

        return target.toUpperCase();
    }

    public static String secureOtput(String input){
        boolean secure_output=false;
        if (secure_output) {
            int len = input.length();
            if (len == 0)
                return "";
            if (len <= 10)
                return input.substring(0, len / 3) + "...." + input.substring(len / 3 * 2);
            else
                return input.substring(0, 4) + "..." + input.substring(len / 4, len / 4 * 2) + "...." + input.substring(len / 4 * 3);
        }else
            return input;
    }
    //import from m2m smsr tls server
    public static byte[] Hex2ByteToHex1Byte(byte[] hex, boolean nullmapstoempty) {
        if (hex == null) return nullmapstoempty ? new byte[0] : null;

        int len = hex.length / 2;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (int i = 0; i < len; i++) {
            int x1 = hex[2 * i] & 0xFF;
            int x2 = hex[2 * i + 1] & 0xFF;
            String s = (char) x1 + Character.toString((char) x2);
            int x = Integer.parseInt(s, 16);
            os.write(x);

        }
        return os.toByteArray();
    }
    public static byte[] Hex2ByteToHex1ByteRevrse(byte[] hex, boolean nullmapstoempty) {
        if (hex == null) return nullmapstoempty ? new byte[0] : null;

        int len = hex.length / 2;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (int i = 0; i < len; i++) {
            int x1 = hex[2 * i] & 0xFF;
            int x2 = hex[2 * i + 1] & 0xFF;
            String s = (char) x2 + Character.toString((char) x1);
            int x = Integer.parseInt(s, 16);
            os.write(x);

        }
        return os.toByteArray();
    }

    public static String decodeICCID(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String byteHex = hex.substring(i, i + 2);
            int b = Integer.parseInt(byteHex, 16);
            sb.append(b & 0x0F);
            int high = (b >> 4) & 0x0F;
            if (high != 0x0F) sb.append(high); // skip 'F'
        }
        return sb.toString().substring(0,sb.length()-1);
    }
}
