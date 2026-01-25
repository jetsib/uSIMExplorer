package simexplorer.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.IntConsumer;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import simexplorer.smartcard.SmartCardController;

public class TerminalMenuController {
    private final SmartCardController smartCardController;

    public TerminalMenuController(SmartCardController smartCardController) {
        this.smartCardController = smartCardController;
    }

    public void populateMenu(JMenu menu, IntConsumer onTerminalSelected) {
        List<CardTerminal> terminals = null;
        try {
            terminals = smartCardController.listTerminals();
        } catch (CardException ex) {
            JOptionPane.showMessageDialog(null, ex, null, 0);
        }
        System.out.println("List : " + terminals);

        for (int i = 0; i < smartCardController.getTerminalCount(); i++) {
            CardTerminal terminal = smartCardController.getTerminal(i);
            TerminalMenuItem item = new TerminalMenuItem(terminal.getName(), i);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    onTerminalSelected.accept(item.getTerminalNumber());
                }
            });
            menu.add(item);
        }
    }

    private static class TerminalMenuItem extends JMenuItem {
        private final int terminalNumber;

        private TerminalMenuItem(String name, int terminalNumber) {
            super(name);
            this.terminalNumber = terminalNumber;
        }

        private int getTerminalNumber() {
            return terminalNumber;
        }
    }
}
