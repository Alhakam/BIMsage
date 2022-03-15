package de.tud.cib.bimsage.annotation.model;

import de.tud.cib.bimsage.annotation.model.entry.AnnotationEntry;

import java.util.HashMap;

/**
 * Java representation of the annotation data
 */
public class AnnotationData {

    //Hashmap besser, weil Einträge getrackt werden sollten.
    /*
    GUID als annotationID für zukünftige Annotationen nicht sinnvoll
    (Annotationen auf gleiche GUID sonst nicht möglich)
     */
    private int annotationID;
    private HashMap<String, AnnotationEntry> annotationEntries;

    public AnnotationData() {
        annotationID = 0;
        annotationEntries = new HashMap<String, AnnotationEntry>();
    }

    /**
     * Adds an annotation entry to the annotation data. Generates an UUID as key for the entry.
     * @param annotationEntry The annotation entry that will be added
     */
    public void addAnnotationEntry(AnnotationEntry annotationEntry) {
        annotationEntries.put(Integer.toString(annotationID), annotationEntry);
        annotationID++;
    }

    /*
    Getter
     */

    public HashMap<String, AnnotationEntry> getAnnotationEntries() {
        return annotationEntries;
    }

    /*
    Setter
     */

    public void setAnnotationEntries(HashMap<String, AnnotationEntry> annotationEntries) {
        this.annotationEntries = annotationEntries;
    }
}
