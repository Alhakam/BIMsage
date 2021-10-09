package de.tud.cib.dotknow.gui.components.anamnesis.construction;

import de.tud.cib.dotknow.annotation.model.ActiveAnnotationData;
import de.tud.cib.dotknow.annotation.model.entry.AnnotationEntry;
import de.tud.cib.dotknow.gui.components.ComponentController;
import de.tud.cib.dotknow.gui.components.ifc.IfcStatus;
import de.tud.cib.dotknow.gui.namespaces.icdd.ICDDNameSpaces;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.configuration.ICDDModifier;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import de.tud.dotknow.ontology.construction.conversion.AnnotationConverter;
import de.tud.dotknow.ontology.construction.conversion.IFCToOWLConverter;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.iterator.ExtendedIterator;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.common.io.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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
}
