package simexplorer.decoders;

public class DecodeHPPLMN implements DecodeEF {

    private static final int N_MINUTES = 6;

    @Override
    public String decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";

        int v = bytes[0] & 0xFF;

        if (v == 0x00) return "No higher priority PLMN search";

        int minutes = v * N_MINUTES;

        return "Search interval: " + minutes + " minutes";
    }
}