package simexplorer.history;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class HistoryLogger {
    private final DefaultListModel<String> listModel;

    public HistoryLogger(DefaultListModel<String> listModel) {
        this.listModel = listModel;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    public void addEntry(String text) {
        listModel.addElement(text);
    }

    public void copySelectionToClipboard(JList historyList) {
        Object selectedValue = historyList.getSelectedValue();
        if (selectedValue == null) {
            return;
        }
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(selectedValue.toString());
        cb.setContents(ss, null);
    }

    public static String formatBuffer(byte[] buffer) {
        StringBuilder strBuff = new StringBuilder("");
        for (int i = 0; i < buffer.length; i++) {
            strBuff.append(String.format("%02X ", buffer[i]));
        }
        return strBuff.toString();
    }
}
