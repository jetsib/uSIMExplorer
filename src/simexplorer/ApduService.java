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

class ApduService {
    private final HistoryLogger historyLogger;
    private CardChannel cardChannel;
    private CommandAPDU commandAPDU;
    private ResponseAPDU responseAPDU;

    ApduService(HistoryLogger historyLogger) {
        this.historyLogger = historyLogger;
    }

    void setCardChannel(CardChannel cardChannel) {
        this.cardChannel = cardChannel;
    }

    byte[] enviarAPDU(byte[] c, byte currentCla) {
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
}
