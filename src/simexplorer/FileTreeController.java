package simexplorer;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import simexplorer.efTools.FileTreeDef;

class FileTreeController {
    private final JTree treeFiles;
    private final JPopupMenu mnuTreeFiles;
    private DefaultMutableTreeNode treeDF_ADMIN = null;

    FileTreeController(JTree treeFiles, JPopupMenu mnuTreeFiles) {
        this.treeFiles = treeFiles;
        this.mnuTreeFiles = mnuTreeFiles;
    }

    void initTreeFiles() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultMutableTreeNode mf = new DefaultMutableTreeNode(FileTreeDef.MF);
        root.add(mf);

        mf.add(new DefaultMutableTreeNode(FileTreeDef.ICCID));

        DefaultMutableTreeNode telecom = new DefaultMutableTreeNode(FileTreeDef.TELECOM);
        for (Object ef : FileTreeDef.TELECOM_EF) {
            telecom.add(new DefaultMutableTreeNode(ef));
        }
        mf.add(telecom);

        DefaultMutableTreeNode gsm = new DefaultMutableTreeNode(FileTreeDef.DF_GSM);
        for (Object ef : FileTreeDef.GSM_EF) {
            gsm.add(new DefaultMutableTreeNode(ef));
        }
        mf.add(gsm);

        DefaultMutableTreeNode usim = new DefaultMutableTreeNode(FileTreeDef.ADF_USIM);
        for (Object ef : FileTreeDef.USIM_EF) {
            usim.add(new DefaultMutableTreeNode(ef));
        }
        mf.add(usim);

        treeFiles.setModel(new DefaultTreeModel(root));
        treeFiles.setComponentPopupMenu(mnuTreeFiles);
        treeFiles.setEnabled(false);
        treeFiles.setRootVisible(false);
    }

    void addMagicSimFiles() {
        DefaultMutableTreeNode treeRoot;
        DefaultMutableTreeNode treeNode3;
        treeDF_ADMIN = new DefaultMutableTreeNode("DF.ADMIN");
        treeNode3 = new DefaultMutableTreeNode("EF.OPN");
        treeDF_ADMIN.add(treeNode3);
        treeNode3 = new DefaultMutableTreeNode("EF 8f 0d");
        treeDF_ADMIN.add(treeNode3);
        treeNode3 = new DefaultMutableTreeNode("EF 8f 0e");
        treeDF_ADMIN.add(treeNode3);
        treeRoot = ((DefaultMutableTreeNode) treeFiles.getModel().getRoot());
        treeRoot.add(treeDF_ADMIN);
        treeFiles.setModel(new DefaultTreeModel(treeRoot));
    }

    void removeMagicSimFiles() {
        DefaultMutableTreeNode treeRoot = ((DefaultMutableTreeNode) treeFiles.getModel().getRoot());
        try {
            treeRoot.remove(treeDF_ADMIN);
        } catch (Exception e) {
        }
        treeFiles.setModel(new DefaultTreeModel(treeRoot));
    }
}
