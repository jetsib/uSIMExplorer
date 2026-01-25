package simexplorer.decoders;

public class DecodePLMN implements DecodeEF {

    @Override
    public String decode(byte[] bytes) {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i + 2 < bytes.length; i += 3) {
            int b1 = bytes[i] & 0xFF;
            int b2 = bytes[i + 1] & 0xFF;
            int b3 = bytes[i + 2] & 0xFF;

            if (b1 == 0xFF && b2 == 0xFF && b3 == 0xFF) continue;

            int mcc1 = b1 & 0x0F;
            int mcc2 = (b1 >> 4) & 0x0F;
            int mcc3 = b2 & 0x0F;

            int mnc3 = (b2 >> 4) & 0x0F;
            int mnc1 = b3 & 0x0F;
            int mnc2 = (b3 >> 4) & 0x0F;

            String mcc = "" + mcc1 + mcc2 + mcc3;
            String mnc;

            if (mnc3 == 0x0F) {
                mnc = "" + mnc1 + mnc2;
            } else {
                mnc = "" + mnc1 + mnc2 + mnc3;
            }

            out.append(mcc).append("-").append(mnc).append("\n");
        }

        return out.toString().trim();
    }
}
