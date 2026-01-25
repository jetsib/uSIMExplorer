package simexplorer;

import java.util.List;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

class SmartCardController {
    private final TerminalFactory factory;
    private List<CardTerminal> terminals;
    private CardTerminal terminal;
    private Card card;
    private ATR cardATR;
    private CardChannel cardChannel;

    SmartCardController() {
        factory = TerminalFactory.getDefault();
    }

    static class ConnectionResult {
        private final CardTerminal terminal;
        private final Card card;
        private final ATR atr;
        private final CardChannel cardChannel;

        ConnectionResult(CardTerminal terminal, Card card, ATR atr, CardChannel cardChannel) {
            this.terminal = terminal;
            this.card = card;
            this.atr = atr;
            this.cardChannel = cardChannel;
        }

        CardTerminal getTerminal() {
            return terminal;
        }

        Card getCard() {
            return card;
        }

        ATR getAtr() {
            return atr;
        }

        CardChannel getCardChannel() {
            return cardChannel;
        }
    }

    List<CardTerminal> listTerminals() throws CardException {
        terminals = factory.terminals().list();
        return terminals;
    }

    int getTerminalCount() {
        return terminals == null ? 0 : terminals.size();
    }

    CardTerminal getTerminal(int index) {
        if (terminals == null) {
            return null;
        }
        return terminals.get(index);
    }

    void selectTerminal(int index) {
        terminal = getTerminal(index);
    }

    ConnectionResult connectToTerminal(int index) throws CardException {
        selectTerminal(index);
        connect();
        return new ConnectionResult(terminal, card, cardATR, cardChannel);
    }

    Thread startCardPresenceMonitor(Runnable onCardAbsent, Runnable onCardPresent) {
        Thread monitorThread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                while (true) {
                    try {
                        terminal.waitForCardAbsent(0);
                        onCardAbsent.run();
                        terminal.waitForCardPresent(0);
                        onCardPresent.run();
                    } catch (CardException ex) {
                    }
                }
            }
        });
        monitorThread.start();
        return monitorThread;
    }

    void connect() throws CardException {
        card = terminal.connect("T=0");
        cardATR = card.getATR();
        cardChannel = card.getBasicChannel();
    }

    void disconnect() throws CardException {
        card.disconnect(true);
    }

    boolean isCardPresent() throws CardException {
        return terminal.isCardPresent();
    }

    CardTerminal getSelectedTerminal() {
        return terminal;
    }

    ATR getCardAtr() {
        return cardATR;
    }

    CardChannel getCardChannel() {
        return cardChannel;
    }

    Card getCard() {
        return card;
    }
}
