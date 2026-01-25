package simexplorer.decoders;

public class DecodePLMNwAcT implements DecodeEF {

    @Override
    public String decode(byte[] bytes) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i + 4 < bytes.length; i += 5) {
            int b1 = bytes[i] & 0xFF;
            int b2 = bytes[i + 1] & 0xFF;
            int b3 = bytes[i + 2] & 0xFF;

            int act1 = bytes[i + 3] & 0xFF;
            int act2 = bytes[i + 4] & 0xFF;

            if (b1 == 0xFF && b2 == 0xFF && b3 == 0xFF) continue;

            int mcc1 = b1 & 0x0F;
            int mcc2 = (b1 >> 4) & 0x0F;
            int mcc3 = b2 & 0x0F;

            int mnc3 = (b2 >> 4) & 0x0F;
            int mnc1 = b3 & 0x0F;
            int mnc2 = (b3 >> 4) & 0x0F;

            String mcc = "" + mcc1 + mcc2 + mcc3;
            String mnc = (mnc3 == 0x0F) ? ("" + mnc1 + mnc2) : ("" + mnc1 + mnc2 + mnc3);

            StringBuilder tech = new StringBuilder();

            if ((act1 & 0x80) != 0) appendTech(tech, "UTRAN");
            if ((act1 & 0x40) != 0) appendTech(tech, "E-UTRAN");
            if ((act1 & 0x08) != 0) appendTech(tech, "NG-RAN");

            boolean satLte = (act1 & 0x03) != 0;
            boolean satNr = (act1 & 0x04) != 0;
            if (satLte) appendTech(tech, "Sat LTE");
            if (satNr) appendTech(tech, "Sat NR");

            boolean gsmFamily = (act2 & 0x80) != 0;
            boolean ecGsmIot = gsmFamily && ((act2 & 0x10) != 0 || (act2 & 0x08) != 0);
            boolean gsmCompact = (act2 & 0x40) != 0;

            if (ecGsmIot) appendTech(tech, "EC-GSM-IoT");
            else if (gsmFamily) appendTech(tech, "GSM");

            if (gsmCompact) appendTech(tech, "GSM COMPACT");

            if ((act2 & 0x20) != 0) appendTech(tech, "CDMA2000 1xRTT");
            if ((act2 & 0x40) != 0 && !gsmCompact) appendTech(tech, "CDMA2000 HRPD");

            out.append(mcc).append("-").append(mnc);
            if (tech.length() > 0) out.append(" ").append(tech);
            out.append("\n");
        }

        return out.toString().trim();
    }

    private static void appendTech(StringBuilder sb, String s) {
        if (sb.length() > 0) sb.append(",");
        sb.append(s);
    }
}
