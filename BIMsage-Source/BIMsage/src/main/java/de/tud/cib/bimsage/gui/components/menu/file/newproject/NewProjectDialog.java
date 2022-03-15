package de.tud.cib.bimsage.gui.components.menu.file.newproject;

import com.apstex.gui.ifc.controller.IfcLoadManager;
import com.apstex.loader3d.core.NotSupportedException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;
import de.tud.cib.bimsage.icdd.activedata.treemodel.ICDDTreeCreator;
import de.tud.cib.bimsage.icdd.configuration.ICDDBuilder;
import de.tud.cib.bimsage.gui.MainGUI;
import de.tud.cib.bimsage.gui.components.ComponentController;
import de.tud.cib.bimsage.gui.components.ifc.IfcStatus;
import de.tud.cib.bimsage.gui.utility.FileChooserUtility;
import de.tud.cib.bimsage.gui.utility.messages.MessageFactory;
import icdd.beans.Container;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class NewProjectDialog extends JDialog {

    private final String appTitle = "New Project";
    private final int posX = 250;
    private final int posY = 250;

    private JPanel contentPane;
    private JButton buttonCreate;
    private JButton buttonCancel;
    private JTextField icddNameField;
    private JTextField icddIFCPath;
    private JButton ifcBrowse;
    private JTextField icddNamespaceField;
    private JTextField icddSavePath;
    private JButton saveBrowse;

    IfcLoadManager loadManager = IfcLoadManager.getInstance();

    public NewProjectDialog() {

        /*
        ActionListeners
         */

        ifcBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserUtility.browseFileForTextField(icddIFCPath);
            }
        });

        saveBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserUtility.browseFolderForTextField(icddSavePath);
            }
        });

        buttonCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCreate();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        /*
        Window-Visualization
         */
        this.panelVisualization();
    }

    private void panelVisualization() {
        this.setTitle(appTitle);
        this.setContentPane(contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(buttonCreate);
        this.pack();
        this.setLocation(posX, posY);
        this.setVisible(true);
    }

    /*
    Events
     */

    //TODO: Fehlerbehandlung der Input-Felder durchführen
    //TODO: Code entschlacken und neu strukturieren
    private void onCreate() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        ICDDBuilder icddBuilder = new ICDDBuilder(icddSavePath.getText());
        if (!icddNameField.getText().isEmpty())
            icddBuilder.setName(icddNameField.getText());
        if (!icddNamespaceField.getText().isEmpty())
            icddBuilder.setNamespace(icddNamespaceField.getText());
        icddBuilder.setIfc(Paths.get(icddIFCPath.getText()));
        try {
            Container icdd = icddBuilder.build();
            activeICDD.setIcdd(icdd);
            activeICDD.setSavePath(icddBuilder.getSavePath());
        } catch (IOException e) {
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.IFC_FILE_ERROR);
            e.printStackTrace();
        }
        try {
            activeICDD.saveICDD();
        } catch (IOException e) {
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.ICDD_SAVE_ERROR);
            e.printStackTrace();
        }
        try {
            File ifc = new File(icddIFCPath.getText());
            loadManager.loadFile(ifc);
            IfcStatus ifcStatus = IfcStatus.getInstance();
            ifcStatus.setIfcFile(ifc.toPath());
            ifcStatus.setIfcModelName(ifc.getName());
            ifcStatus.initIfcEntityListener();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            MessageFactory.showSimpleMessage(MessageFactory.MessageType.IFC_FILE_ERROR);
            e.printStackTrace();
        }
        MainGUI.treeModel.setRoot(ICDDTreeCreator.createTreeModel(activeICDD.getIcdd()));
        ComponentController componentController = ComponentController.getInstance();
        componentController.enableICDDButtons(true);
        componentController.setLinkTable();
        componentController.setDamageList();
        this.dispose();
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.ICDD_COMPLETE);
    }

    private void onCancel() {
        this.dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setAlignmentX(0.5f);
        contentPane.setMinimumSize(new Dimension(600, 200));
        contentPane.setPreferredSize(new Dimension(600, 200));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCreate = new JButton();
        buttonCreate.setText("Create");
        panel2.add(buttonCreate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        icddNameField = new JTextField();
        panel3.add(icddNameField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Custom ICDD-Name ");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label2 = new JLabel();
        label2.setText("IFC-File ");
        panel3.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        icddIFCPath = new JTextField();
        panel3.add(icddIFCPath, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 1, false));
        ifcBrowse = new JButton();
        ifcBrowse.setText("Browse");
        panel3.add(ifcBrowse, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 20), new Dimension(80, 20), new Dimension(80, 20), 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Save File Location");
        panel3.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        icddSavePath = new JTextField();
        panel3.add(icddSavePath, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 1, false));
        saveBrowse = new JButton();
        saveBrowse.setText("Browse");
        panel3.add(saveBrowse, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 20), new Dimension(80, 20), new Dimension(80, 20), 0, false));
        icddNamespaceField = new JTextField();
        panel3.add(icddNamespaceField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Custom ICDD-Namespace");
        panel3.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
