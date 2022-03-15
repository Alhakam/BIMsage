package de.tud.cib.bimsage.annotation.model.entry;

/**
 * AnnotationData entry for defining an additional class for a specific IFC entity
 */
public class ClassAnnotation extends AnnotationEntry{

    private String className;

    public ClassAnnotation(String annotatedEntityGUID, String modelName, String className) {
        super(annotatedEntityGUID, modelName);
        this.className = className;
    }

    /*
    Getter
     */

    public String getClassName() {
        return className;
    }

    /*
    Setter
     */

    public void setClassName(String className) {
        this.className = className;
    }
}
