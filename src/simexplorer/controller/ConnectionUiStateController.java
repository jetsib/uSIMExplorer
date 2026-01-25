package simexplorer.controller;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import simexplorer.ui.dialog.DialogCopia;
import simexplorer.SIMExplorer;
import simexplorer.service.ApduService;
import simexplorer.simcardcloner.SIMCardType;

public class ConnectionUiStateController {
    private final JTree treeFiles;
    private final JMenu mnuConectar;
    private final JMenuItem mnuDesconectar;
    private final JMenuItem mnuCriarRelatorio;
    private final JMenuItem mnuEnviarAPDU;
    private final JMenuItem mnuGerenciarCHV;
    private final JMenu mnuCopia;
    private final JTextArea edtDadosBrutos;
    private final JTextArea edtDadosDecodificados;

    public ConnectionUiStateController(
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

    public SIMCardType onConnected(ApduService apduService, FileTreeController fileTreeController) {
        treeFiles.setEnabled(true);
        mnuConectar.setVisible(false);
        mnuDesconectar.setVisible(true);
        mnuCriarRelatorio.setEnabled(true);
        mnuEnviarAPDU.setEnabled(true);
        mnuGerenciarCHV.setEnabled(true);
        mnuCopia.setEnabled(true);
        SIMCardType detectedType = apduService.detectSimCardType();
        if (detectedType == SIMCardType.MagicSIM) {
            fileTreeController.addMagicSimFiles();
            JOptionPane.showMessageDialog(null, "MagicSim detected!");
        }
        return detectedType;
    }

    public void onDisconnected(FileTreeController fileTreeController, SIMCardType simCardType) {
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
        if (simCardType == SIMCardType.MagicSIM) {
            fileTreeController.removeMagicSimFiles();
        }
    }

    public void startCopyFromSimCard(SIMExplorer owner, SIMCardType simCardType) {
        new Thread(new Runnable() {
            public void run() {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(owner);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (fc.getSelectedFile().exists()) {
                        int r = JOptionPane.showConfirmDialog(null, "File already exists. Replace?", "", JOptionPane.YES_NO_OPTION);
                        if (r == JOptionPane.OK_OPTION) {
                            if (fc.getSelectedFile().delete()) {
                                DialogCopia dialog = new DialogCopia(null, fc.getSelectedFile(), owner, true, simCardType);
                                dialog.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(null, "Can not delete file.");
                            }
                        }

                    } else {
                        DialogCopia dialog = new DialogCopia(null, fc.getSelectedFile(), owner, true, simCardType);
                        dialog.setVisible(true);
                    }
                }
            }
        }).start();
    }

    public void startCopyToSimCard(SIMExplorer owner, SIMCardType simCardType) {
        new Thread(new Runnable() {
            public void run() {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(owner);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    if (fc.getSelectedFile().exists()) {
                        DialogCopia dialog = new DialogCopia(null, fc.getSelectedFile(), owner, false, simCardType);
                        dialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "File doesn't exist.");
                    }
                }
            }
        }).start();
    }
}
