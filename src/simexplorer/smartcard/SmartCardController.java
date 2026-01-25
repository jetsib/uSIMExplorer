package simexplorer.smartcard;

import java.util.List;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class SmartCardController {
    private final TerminalFactory factory;
    private List<CardTerminal> terminals;
    private CardTerminal terminal;
    private Card card;
    private ATR cardATR;
    private CardChannel cardChannel;

    public SmartCardController() {
        factory = TerminalFactory.getDefault();
    }

    public static class ConnectionResult {
        private final CardTerminal terminal;
        private final Card card;
        private final ATR atr;
        private final CardChannel cardChannel;

        public ConnectionResult(CardTerminal terminal, Card card, ATR atr, CardChannel cardChannel) {
            this.terminal = terminal;
            this.card = card;
            this.atr = atr;
            this.cardChannel = cardChannel;
        }

        public CardTerminal getTerminal() {
            return terminal;
        }

        public Card getCard() {
            return card;
        }

        public ATR getAtr() {
            return atr;
        }

        public CardChannel getCardChannel() {
            return cardChannel;
        }
    }

    public List<CardTerminal> listTerminals() throws CardException {
        terminals = factory.terminals().list();
        return terminals;
    }

    public int getTerminalCount() {
        return terminals == null ? 0 : terminals.size();
    }

    public CardTerminal getTerminal(int index) {
        if (terminals == null) {
            return null;
        }
        return terminals.get(index);
    }

    public void selectTerminal(int index) {
        terminal = getTerminal(index);
    }

    public ConnectionResult connectToTerminal(int index) throws CardException {
        selectTerminal(index);
        connect();
        return new ConnectionResult(terminal, card, cardATR, cardChannel);
    }

    public Thread startCardPresenceMonitor(Runnable onCardAbsent, Runnable onCardPresent) {
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

    public void connect() throws CardException {
        card = terminal.connect("T=0");
        cardATR = card.getATR();
        cardChannel = card.getBasicChannel();
    }

    public void disconnect() throws CardException {
        card.disconnect(true);
    }

    public boolean isCardPresent() throws CardException {
        return terminal.isCardPresent();
    }

    public CardTerminal getSelectedTerminal() {
        return terminal;
    }

    public ATR getCardAtr() {
        return cardATR;
    }

    public CardChannel getCardChannel() {
        return cardChannel;
    }

    public Card getCard() {
        return card;
    }
}
