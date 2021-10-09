package de.tud.cib.dotknow.gui.components.menu.file.newproject;

import com.apstex.gui.ifc.controller.IfcLoadManager;
import com.apstex.loader3d.core.NotSupportedException;
import de.tud.cib.dotknow.gui.MainGUI;
import de.tud.cib.dotknow.gui.components.ComponentController;
import de.tud.cib.dotknow.gui.components.ifc.IfcStatus;
import de.tud.cib.dotknow.gui.utility.FileChooserUtility;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.activedata.treemodel.ICDDTreeCreator;
import de.tud.cib.dotknow.icdd.configuration.ICDDBuilder;
import icdd.beans.Container;

import javax.swing.*;
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
}
