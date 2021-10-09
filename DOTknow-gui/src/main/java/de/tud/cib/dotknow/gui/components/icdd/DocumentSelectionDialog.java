package de.tud.cib.dotknow.gui.components.icdd;

import de.tud.cib.dotknow.gui.components.ComponentController;
import de.tud.cib.dotknow.gui.namespaces.icdd.ICDDNameSpaces;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.configuration.ICDDModifier;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;

import javax.swing.*;
import java.awt.event.*;
import java.util.LinkedHashSet;
import java.util.Vector;

public class DocumentSelectionDialog extends JDialog {

    private LinkedHashSet<ModelElement> ifcElementSet;

    private static final int posX = 250;
    private static final int posY = 250;

    private JList docList;
    private JPanel contentPane;
    private JButton linkDocButton;
    private JButton cancelButton;

    public DocumentSelectionDialog(Vector<String> documentList, LinkedHashSet<ModelElement> ifcElementSet) {
        this.docList.setListData(documentList);
        this.ifcElementSet = ifcElementSet;

        /*
        ActionListeners
         */

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        linkDocButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLinkDocuments();
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
        this.setContentPane(contentPane);
        this.setModal(true);
        this.pack();
        this.setLocation(posX, posY);
        this.setVisible(true);
    }

    /*
    Events
     */

    private void onLinkDocuments() {
        if(ifcElementSet == null)
            this.processDocumentLinking();
        else
            this.processIfcEntityLinking();
        ComponentController.getInstance().updateICDDTree();
        this.dispose();
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.LINK_DOCUMENTS);
    }

    private void onCancel() {
        this.dispose();
    }

    /*
    Internal Functions
     */

    private void processDocumentLinking() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        activeICDD.setIcdd(ICDDModifier.linkDocuments(docList.getSelectedValuesList(), ICDDNameSpaces.DOCUMENT_LINKSET_NAME,
                ICDDNameSpaces.DOCUMENT_LINKSET_NS, activeICDD.getIcdd()));
    }

    private void processIfcEntityLinking() {
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        for (ModelElement ifcElement :
                ifcElementSet) {
            activeICDD.setIcdd(ICDDModifier.linkDocumentsToElement(docList.getSelectedValuesList(), ifcElement,
                    ICDDNameSpaces.DOCUMENT_LINKSET_NAME, ICDDNameSpaces.DOCUMENT_LINKSET_NS, activeICDD.getIcdd()));
        }

    }
}