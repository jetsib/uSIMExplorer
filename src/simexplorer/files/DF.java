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

package simexplorer.files;

import simexplorer.apdusender.APDUSender;
import simexplorer.decoders.SIMFileNotFoundException;
import simexplorer.decoders.utils.FcpParser;

/**
 *
 * @author Gustavo Vieira Rocha Rabelo <gustavo.vrr@gmail.com>
 */
public class DF extends File{

    private final boolean isCHV1Enabled;

    public boolean isCHV1Enabled() {
        return isCHV1Enabled;
    }

    public boolean isCHV1Blocked() {
        return isCHV1Blocked;
    }

    public boolean isUnblockCHVIBlocked() {
        return isUnblockCHVIBlocked;
    }

    public boolean isCHV2Blocked() {
        return isCHV2Blocked;
    }

    public boolean isUnblockCHV2Blocked() {
        return isUnblockCHV2Blocked;
    }
    private final boolean isCHV1Blocked;
    private final boolean isUnblockCHVIBlocked;
    private final boolean isCHV2Blocked;
    private final boolean isUnblockCHV2Blocked;

    private final FcpParser.Result fcp;

    public DF(APDUSender apduSender, String nome, String[] pais) throws SIMFileNotFoundException {
        super(apduSender, nome, pais);

        this.fcp = FcpParser.parseSelectResponse(resposta);
        if (this.fcp!=null) {
            typeOfFile = fcp.typeOfFile;

            isCHV1Blocked = fcp.isCHV1Blocked;
            isCHV1Enabled = fcp.isCHV1Enabled;
            isUnblockCHVIBlocked = fcp.isUnblockCHV1Blocked;
            isCHV2Blocked = fcp.isCHV2Blocked;
            isUnblockCHV2Blocked = fcp.isUnblockCHV2Blocked;
        }else{
            typeOfFile = TypeOfFile.INVALID;

            isCHV1Blocked = false;
            isCHV1Enabled = false;
            isUnblockCHVIBlocked = false;
            isCHV2Blocked = false;
            isUnblockCHV2Blocked = false;
        }
    }

    public String getCHVInfo() {
        StringBuilder sb = new StringBuilder();

        if (isCHV1Enabled) sb.append("CHV1 enabled");
        else sb.append("CHV1 disabled");

        if (fcp == null || fcp.rawPinStatusTemplate == null) {
            sb.append("\nPIN status template not available in FCP");
            return sb.toString();
        }

        byte[] pst = fcp.rawPinStatusTemplate;

        appendChvLine(sb, "CHV1", pst, 0);
        appendChvLine(sb, "UNBLOCK CHV1", pst, 1);
        appendChvLine(sb, "CHV2", pst, 2);
        appendChvLine(sb, "UNBLOCK CHV2", pst, 3);

        return sb.toString();
    }

    private void appendChvLine(StringBuilder sb, String label, byte[] pst, int idx) {
        if (pst.length <= idx) {
            sb.append("\n").append(label).append(" status-> not available");
            return;
        }

        byte v = pst[idx];
        sb.append("\n").append(label).append(" status-> Number of false presentations remaining ('0' means blocked): ").append(v & 0x0F);
        sb.append("\n").append(label).append(" status-> ").append(((v & (byte) 0x80) == (byte) 0x80) ? "secret code initialised" : "secret code not initialised");
    }

    @Override
    public String toString()
    {
        StringBuilder sb ;
        sb = new StringBuilder();
        
        sb.append("File ID: ");
        sb.append(String.format("%02X", fileID[0]));
        sb.append(" ");
        sb.append(String.format("%02X", fileID[1])) ;

        if (fcp == null) {
            sb.append("\nDF not found");
            return sb.toString();
        }

        sb.append("\nType of file: " + typeOfFile);
        
        sb.append("\n").append(this.getCHVInfo());
        
        return sb.toString();
        
    }
    
}
