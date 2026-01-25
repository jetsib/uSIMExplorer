/*
 * The MIT License
 *
 * Copyright 2018 Gustavo Vieira Rocha Rabelo <gustavo.vrr@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package simexplorer.decoders;

public class DecodeIMSI implements DecodeEF {

    @Override
    public String decode(byte[] bytes) {
        if (bytes == null || bytes.length < 2) return "Invalid IMSI";

        int size = bytes[0] & 0xFF;
        if (size <= 0) return "Invalid IMSI";

        if (bytes.length < size + 1) return "Invalid IMSI";

        StringBuilder imsi = new StringBuilder();

        imsi.append(String.format("%01X", (bytes[1] >> 4) & 0x0F));

        for (int i = 1; i < size; i++) {
            imsi.append(String.format("%01X", bytes[i + 1] & 0x0F));
            imsi.append(String.format("%01X", (bytes[i + 1] >> 4) & 0x0F));
        }

        String imsiStr = imsi.toString();

        if (imsiStr.length() < 5) return imsiStr;

        String mcc = imsiStr.substring(0, 3);
        String mnc2 = imsiStr.substring(3, 5);
        String mnc3 = imsiStr.length() >= 6 ? imsiStr.substring(3, 6) : null;

        if (mnc3 != null) {
            return "IMSI: " + imsiStr + " (MCC=" + mcc + ", MNC=" + mnc2 + "/" + mnc3 + ")";
        }
        return "IMSI: " + imsiStr + " (MCC=" + mcc + ", MNC=" + mnc2 + ")";
    }
}

