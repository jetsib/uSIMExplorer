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
