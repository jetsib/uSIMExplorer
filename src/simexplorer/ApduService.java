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

package simexplorer;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import simexplorer.simcardcloner.SIMCardType;

class ApduService {
    private final HistoryLogger historyLogger;
    private byte currentCla = (byte) 0xA0;
    private CardChannel cardChannel;
    private CommandAPDU commandAPDU;
    private ResponseAPDU responseAPDU;

    ApduService(HistoryLogger historyLogger) {
        this.historyLogger = historyLogger;
    }

    void setCardChannel(CardChannel cardChannel) {
        this.cardChannel = cardChannel;
    }

    void setCurrentCla(byte currentCla) {
        this.currentCla = currentCla;
    }

    byte getCurrentCla() {
        return currentCla;
    }

    byte[] enviarAPDU(byte[] c) {
        return enviarAPDU(c, currentCla);
    }

    byte[] enviarAPDU(byte[] c, byte currentCla) {
        this.currentCla = currentCla;
        c[0] = currentCla;
        commandAPDU = new CommandAPDU(c);
        historyLogger.addEntry("S:" + HistoryLogger.formatBuffer(c));
        try {
            responseAPDU = cardChannel.transmit(commandAPDU);
        } catch (CardException ex) {
            historyLogger.addEntry("Err: " + ex.getMessage());
        }
        historyLogger.addEntry("R:" + HistoryLogger.formatBuffer(responseAPDU.getBytes()));
        return responseAPDU.getBytes();
    }

    boolean supportsUsim() {
        setCurrentCla((byte) 0x00);
        byte[] r = enviarAPDU(new byte[]{
            currentCla, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07,
            (byte) 0xA0, 0x00, 0x00, 0x00, (byte) 0x87, 0x10, 0x02
        });

        if (r.length < 2) {
            return false;
        }
        byte sw1 = r[r.length - 2];
        return sw1 == (byte) 0x90 || sw1 == (byte) 0x61 || sw1 == (byte) 0x9F;
    }

    SIMCardType detectSimCardType() {
        if (supportsUsim()) {
            return SIMCardType.USIM;
        }
        setCurrentCla((byte) 0xA0);
        byte[] response = enviarAPDU(new byte[]{
            currentCla, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x7F, (byte) 0x4D
        });

        if (response.length == 2) {
            if (response[0] == (byte) 0x9F) {
                return SIMCardType.MagicSIM;
            }
            return SIMCardType.Regular;
        }
        return SIMCardType.Regular;
    }
}
