package simexplorer;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import simexplorer.decoders.SIMFileNotFoundException;
import simexplorer.files.DF;
import simexplorer.files.EF;
import simexplorer.files.File;

class FileEditorController {
    private final SIMExplorer owner;
    private final JTextArea edtDadosBrutos;
    private final JTextArea edtDadosDecodificados;
    private File lastFile = null;

    FileEditorController(SIMExplorer owner, JTextArea edtDadosBrutos, JTextArea edtDadosDecodificados) {
        this.owner = owner;
        this.edtDadosBrutos = edtDadosBrutos;
        this.edtDadosDecodificados = edtDadosDecodificados;
    }

    void updateEf(String nome, String[] pais) {
        try {
            EF ef = new EF(owner, nome, pais);
            lastFile = ef;
            edtDadosBrutos.setText(ef.toString());
            edtDadosDecodificados.setText(ef.dadosDecodificados());
        } catch (SIMFileNotFoundException ex) {
            edtDadosBrutos.setText(ex.getMessage());
            edtDadosDecodificados.setText(ex.getMessage());
            lastFile = null;
        } finally {
            edtDadosBrutos.setSelectionStart(0);
            edtDadosBrutos.setSelectionEnd(0);
            edtDadosDecodificados.setSelectionStart(0);
            edtDadosDecodificados.setSelectionEnd(0);
        }
    }

    void updateDf(String nome, String[] pais) {
        try {
            DF df = new DF(owner, nome, pais);
            lastFile = df;
            edtDadosBrutos.setText(df.toString());
        } catch (SIMFileNotFoundException ex) {
            edtDadosBrutos.setText(ex.getMessage());
            lastFile = null;
        } finally {
            edtDadosBrutos.setSelectionStart(0);
            edtDadosBrutos.setSelectionEnd(0);
            edtDadosDecodificados.setSelectionStart(0);
            edtDadosDecodificados.setSelectionEnd(0);
        }
    }

    boolean updateForSelection(DefaultMutableTreeNode node, boolean isRootVisible) {
        edtDadosBrutos.setText("");
        edtDadosDecodificados.setText("");

        if (node == null) {
            return false;
        }

        String[] pais = buildFilePath(node, isRootVisible);
        if (node.getChildCount() == 0) {
            updateEf(node.toString(), pais);
        } else {
            updateDf(node.toString(), pais);
        }
        edtDadosBrutos.setSelectionStart(0);
        edtDadosBrutos.setSelectionEnd(0);
        edtDadosDecodificados.setSelectionStart(0);
        edtDadosDecodificados.setSelectionEnd(0);
        return true;
    }

    private String[] buildFilePath(DefaultMutableTreeNode node, boolean isRootVisible) {
        String[] pais = new String[isRootVisible ? node.getLevel() : node.getLevel() - 1];
        for (int i = 0; i < pais.length; i++) {
            pais[i] = node.getPath()[isRootVisible ? i : i + 1].toString();
        }
        return pais;
    }

    void editLastFile() {
        if (lastFile.getClass() == EF.class) {
            EF ef = (EF) lastFile;
            if (ef.getStructureOfFile() == EF.StructureOfFile.Transparent) {
                DialogEditar dlgEditar = new DialogEditar(owner, true);
                dlgEditar.txtEdicao.setText(ef.getContentsTransparentAsString());
                dlgEditar.setTitle("Edit " + ef.getNome());
                dlgEditar.setVisible(true);
                if (dlgEditar.getPressionouOK()) {
                    try {
                        ef.updateBinary(dlgEditar.txtEdicao.getText());
                        updateEf(lastFile.getNome(), lastFile.getPais());
                    } catch (EF.UpdateBinaryException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            } else if (ef.getStructureOfFile() == EF.StructureOfFile.LinearFixed) {
                DialogNumReg dlgNumReg = new DialogNumReg(owner, true, ef.getNumRegs());
                dlgNumReg.setVisible(true);
                if (dlgNumReg.getPressionouOK()) {
                    DialogEditar dlgEditar = new DialogEditar(owner, true);
                    dlgEditar.txtEdicao.setText(ef.getContentsLinearCyclicAsString(dlgNumReg.getNumReg() - 1));
                    dlgEditar.setTitle("Edit " + ef.getNome() + "; register " + dlgNumReg.getNumReg());
                    dlgEditar.setVisible(true);
                    if (dlgEditar.getPressionouOK()) {
                        try {
                            ef.updateRecord(dlgEditar.txtEdicao.getText(), dlgNumReg.getNumReg());
                            updateEf(lastFile.getNome(), lastFile.getPais());
                        } catch (EF.UpdateRecordException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                }
            } else if (ef.getStructureOfFile() == EF.StructureOfFile.Cyclic) {
                DialogEditar dlgEditar = new DialogEditar(owner, true);
                dlgEditar.txtEdicao.setText(ef.getContentsLinearCyclicAsString(0));
                dlgEditar.setTitle("New register: " + ef.getNome());
                dlgEditar.setVisible(true);
                if (dlgEditar.getPressionouOK()) {
                    try {
                        ef.updateRecord(dlgEditar.txtEdicao.getText(), 0);
                        updateEf(lastFile.getNome(), lastFile.getPais());
                    } catch (EF.UpdateRecordException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        }
    }
}
