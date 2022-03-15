package de.tud.cib.bimsage.gui.components.anamnesis.construction;

import com.google.common.io.Files;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;
import de.tud.cib.bimsage.icdd.configuration.ICDDModifier;
import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;
import de.tud.cib.bimsage.annotation.model.ActiveAnnotationData;
import de.tud.cib.bimsage.gui.components.ComponentController;
import de.tud.cib.bimsage.gui.components.ifc.IfcStatus;
import de.tud.cib.bimsage.gui.namespaces.icdd.ICDDNameSpaces;
import de.tud.cib.bimsage.gui.utility.messages.MessageFactory;
import de.tud.cib.bimsage.ontology.construction.conversion.AnnotationConverter;
import de.tud.cib.bimsage.ontology.construction.conversion.IFCToOWLConverter;
import de.tud.cib.bimsage.ontology.representation.ActiveOntology;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;

public class ConstructionOntologyDialog extends JDialog {

    private final String appTitle = "Construction Ontology Creation";
    private final int posX = 250;
    private final int posY = 250;

    private JPanel contentPane;
    private JButton buttonCreate;
    private JButton buttonCancel;
    private JTextField constructionURITextField;
    private JTextField constructionNSTextField;
    private JTextField constructionFilenameTextField;

    public ConstructionOntologyDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCreate);

        buttonCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onCreate();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
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

    //TODO: Methode entschlacken
    private void onCreate() throws IOException {
        IFCToOWLConverter ifcToOWLConverter = new IFCToOWLConverter(
                constructionURITextField.getText(), constructionNSTextField.getText());
        OntModel constructionOntology = ifcToOWLConverter.convert(IfcStatus.getInstance().getIfcFile());
        constructionOntology = new AnnotationConverter(constructionOntology).
                convertAnnotations(ActiveAnnotationData.getInstance());
        ActiveOntology activeOntology = ActiveOntology.getInstance();
        activeOntology.setOntology(constructionOntology);
        activeOntology.setOntologyURI(constructionURITextField.getText());
        activeOntology.setOntologyNS(constructionNSTextField.getText());
        Path tempfile = new File(Files.createTempDir(), "Construction.ttl").toPath();
        RDFDataMgr.write(new FileOutputStream(tempfile.toFile()), constructionOntology, RDFFormat.TTL);
        ICDDModifier.addResourceToICDD(tempfile, ActiveICDD.getInstance().getIcdd());
        this.linkIndividuals(constructionOntology, tempfile.getFileName().toString());
        ComponentController componentController = ComponentController.getInstance();
        componentController.updateICDDTree();
        componentController.setConstructionInformationTable();
        componentController.enableConstructionOntology(true);
        dispose();
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.CONSTRUCTION_ONTOLOGY_COMPLETE);
    }

    private void linkIndividuals(OntModel ontology, String ontologyFileName) {
        ontology.listIndividuals().forEachRemaining(individual -> {
            LinkedHashSet<ModelElement> linkElements = new LinkedHashSet<>();
            String individualURI = individual.getURI();
            linkElements.add(new ModelElement(ontologyFileName, individualURI));
            linkElements.add(new ModelElement(IfcStatus.getInstance().getIfcModelName(), StringUtils.substringAfter(individualURI, "#")));
            ICDDModifier.linkElements(linkElements, ICDDNameSpaces.CONSTRUCTION_ONTOLOGY_LINKSET_NAME,
                    ICDDNameSpaces.CONSTRUCTION_ONTOLOGY_LINKSET_NS, ActiveICDD.getInstance().getIcdd());
        });
    }

    private void onCancel() {
        dispose();
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCreate = new JButton();
        buttonCreate.setText("Create Ontology");
        panel2.add(buttonCreate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Construction Ontology URI");
        panel3.add(label1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(3, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        constructionURITextField = new JTextField();
        constructionURITextField.setText("https://dotknow.com/construction#");
        panel3.add(constructionURITextField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Construction Ontology Namespace");
        panel3.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        constructionNSTextField = new JTextField();
        constructionNSTextField.setText("construction");
        panel3.add(constructionNSTextField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Filename");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        constructionFilenameTextField = new JTextField();
        constructionFilenameTextField.setText("Construction");
        panel3.add(constructionFilenameTextField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
