package simexplorer;

import javax.swing.DefaultListModel;

class HistoryLogger {
    private final DefaultListModel<String> listModel;

    HistoryLogger(DefaultListModel<String> listModel) {
        this.listModel = listModel;
    }

    DefaultListModel<String> getListModel() {
        return listModel;
    }

    void addEntry(String text) {
        listModel.addElement(text);
    }

    static String formatBuffer(byte[] buffer) {
        StringBuilder strBuff = new StringBuilder("");
        for (int i = 0; i < buffer.length; i++) {
            strBuff.append(String.format("%02X ", buffer[i]));
        }
        return strBuff.toString();
    }
}
