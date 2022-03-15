package de.tud.cib.bimsage.annotation.model.entry;

import java.io.Serializable;

/**
 * AnnotationData entry that could be added to an AnnotationData object
 */
public abstract class AnnotationEntry implements Serializable {

    private String annotatedEntityGUID;
    private String modelName;

    public AnnotationEntry() {}

    public AnnotationEntry(String annotatedEntityGUID, String modelName) {
        this.annotatedEntityGUID = annotatedEntityGUID;
        this.modelName = modelName;
    }

    /*
    Getter
     */

    public String getAnnotatedEntityGUID() {
        return annotatedEntityGUID;
    }

    public String getModelName() {
        return modelName;
    }

    /*
    Setter
     */

    public void setAnnotatedEntityGUID(String annotatedEntityGUID) {
        this.annotatedEntityGUID = annotatedEntityGUID;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
