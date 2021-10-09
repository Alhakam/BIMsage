package de.tud.cib.dotknow.gui.controller.annotation;

import com.google.common.io.Files;
import de.tud.cib.dotknow.annotation.model.ActiveAnnotationData;
import de.tud.cib.dotknow.annotation.model.entry.AnnotationEntry;
import de.tud.cib.dotknow.annotation.model.entry.ClassAnnotation;
import de.tud.cib.dotknow.annotation.xmlUtil.AnnotationXMLParser;
import de.tud.cib.dotknow.gui.components.ComponentController;
import de.tud.cib.dotknow.gui.namespaces.icdd.ICDDNameSpaces;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.configuration.ICDDModifier;
import de.tud.cib.dotknow.icdd.configuration.input.ModelElement;
import icdd.beans.Container;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Configures annotation files and manages the linking process of them
 */
public class AnnotationController {

    private AnnotationController() {}

    /**
     * Creates an annotation file or updates the existing one based on a given annotation and a list of selected model elements
     * @param annotation the annotation which should be  assigned to a set of elements
     * @param selectedElementList a LinkedHashSet of Model Element objects that should be annotated
     * @throws IOException
     */
    public static void createAnnotationFile(String annotation, LinkedHashSet<ModelElement> selectedElementList) throws IOException {
        if(!selectedElementList.isEmpty()) {
            ActiveAnnotationData annotationData = ActiveAnnotationData.getInstance();
            for (ModelElement selectedElement :
                    selectedElementList) {
                ClassAnnotation classAnnotation = new ClassAnnotation(selectedElement.getElementID(), selectedElement.getDocumentName(), annotation);
                annotationData.addAnnotationEntry(classAnnotation);
            }
            Path tempFile = new File(Files.createTempDir(), "IFCAnnotation.xml").toPath();
            AnnotationXMLParser.writeXMLDoc(annotationData, tempFile.toAbsolutePath().toString());
            Container activeICDD = ActiveICDD.getInstance().getIcdd();
            activeICDD = ICDDModifier.addResourceToICDD(tempFile, activeICDD);
            ActiveICDD.getInstance().setIcdd(activeICDD);
            ComponentController.getInstance().updateICDDTree();
        }
    }

    //TODO:ActiveICDD direkt als Subklasse von Container definieren
    /**
     * Creates a link model that links the active annotation file with its related IFC model
     */
    public static void linkAnnotationFile() {
        LinkedHashSet<ModelElement> modelElementSet = new LinkedHashSet<>();
        ActiveICDD activeICDD = ActiveICDD.getInstance();
        ActiveAnnotationData activeAnnotationData = ActiveAnnotationData.getInstance();
        Container icdd = activeICDD.getIcdd();
        HashMap<String, AnnotationEntry> annotationEntries = activeAnnotationData.getAnnotationEntries();
        for (int i=0; i<annotationEntries.size(); i++) {
            String key = Integer.toString(i);
            AnnotationEntry annotationEntry = annotationEntries.get(key);
            modelElementSet.add(new ModelElement(activeAnnotationData.getTempFile().getFileName().toString(), key));
            modelElementSet.add(new ModelElement(annotationEntry.getModelName(), annotationEntry.getAnnotatedEntityGUID()));
        }
        ICDDModifier.linkElements(modelElementSet, ICDDNameSpaces.ANNOTATION_LINKSET_NAME, ICDDNameSpaces.ANNOTATION_LINKSET_NS, icdd);
        activeICDD.setIcdd(icdd);
        ComponentController.getInstance().updateICDDTree();
    }

}
