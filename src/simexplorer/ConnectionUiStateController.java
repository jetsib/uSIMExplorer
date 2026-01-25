package simexplorer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTree;

class ConnectionUiStateController {
    private final JTree treeFiles;
    private final JMenu mnuConectar;
    private final JMenuItem mnuDesconectar;
    private final JMenuItem mnuCriarRelatorio;
    private final JMenuItem mnuEnviarAPDU;
    private final JMenuItem mnuGerenciarCHV;
    private final JMenu mnuCopia;
    private final JTextArea edtDadosBrutos;
    private final JTextArea edtDadosDecodificados;

    ConnectionUiStateController(
            JTree treeFiles,
            JMenu mnuConectar,
            JMenuItem mnuDesconectar,
            JMenuItem mnuCriarRelatorio,
            JMenuItem mnuEnviarAPDU,
            JMenuItem mnuGerenciarCHV,
            JMenu mnuCopia,
            JTextArea edtDadosBrutos,
            JTextArea edtDadosDecodificados
    ) {
        this.treeFiles = treeFiles;
        this.mnuConectar = mnuConectar;
        this.mnuDesconectar = mnuDesconectar;
        this.mnuCriarRelatorio = mnuCriarRelatorio;
        this.mnuEnviarAPDU = mnuEnviarAPDU;
        this.mnuGerenciarCHV = mnuGerenciarCHV;
        this.mnuCopia = mnuCopia;
        this.edtDadosBrutos = edtDadosBrutos;
        this.edtDadosDecodificados = edtDadosDecodificados;
    }

    void onConnected() {
        treeFiles.setEnabled(true);
        mnuConectar.setVisible(false);
        mnuDesconectar.setVisible(true);
        mnuCriarRelatorio.setEnabled(true);
        mnuEnviarAPDU.setEnabled(true);
        mnuGerenciarCHV.setEnabled(true);
        mnuCopia.setEnabled(true);
    }

    void onDisconnected() {
        mnuDesconectar.setVisible(false);
        mnuConectar.setVisible(true);
        treeFiles.setEnabled(false);
        mnuCriarRelatorio.setEnabled(false);
        edtDadosBrutos.setText("");
        edtDadosDecodificados.setText("");
        treeFiles.setSelectionPath(null);
        mnuEnviarAPDU.setEnabled(false);
        mnuGerenciarCHV.setEnabled(false);
        mnuCopia.setEnabled(false);
    }
}
