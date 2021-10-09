package de.tud.cib.dotknow.annotation.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Singleton used for representing the active annotation model used in the platform
 */
public class ActiveAnnotationData extends AnnotationData{

    private Path tempFile;

    private static ActiveAnnotationData ourInstance;

    static {
        try {
            ourInstance = new ActiveAnnotationData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ActiveAnnotationData() throws IOException {
        tempFile = Files.createTempFile("IFCAnnotation", ".xml");
    }

    public static ActiveAnnotationData getInstance() {
        return ourInstance;
    }

    /*
    Getter
     */

    public Path getTempFile() {
        return tempFile;
    }
}
