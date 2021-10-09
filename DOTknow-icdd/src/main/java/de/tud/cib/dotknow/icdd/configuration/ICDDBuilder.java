package de.tud.cib.dotknow.icdd.configuration;

import icdd.beans.Container;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Builder for creating an ICDD Container
 *
 * Provides various functions for configuring the ICDD, usually revolving around a central IFC model
 */
public class ICDDBuilder {

    private String name;
    private String namespace;
    private Path ifc;
    private String savePath;

    private final String defaultName = "ICDD";
    private final String defaultNameSpace = "https://tu-dresden.de/bu/bauingenieurwesen/cib/icdd";

    public ICDDBuilder(String savePath) {
        this.savePath = savePath;
        this.name = defaultName;
        this.namespace = defaultNameSpace;
    }

    public ICDDBuilder(String savePath, Path ifc) {
        this(savePath);
        this.ifc = ifc;
    }

    public ICDDBuilder(String savePath, String name, String namespace, Path ifc) {
        this(savePath, ifc);
        this.name = name;
        this.namespace = namespace;
    }

    /**
     * Builds the ICDD container, which contains the given IFC model
     * @return The ICDD container
     */
    public Container build() throws IOException {
        Container icdd = new Container(this.name, this.namespace);
        icdd = addIFCToICDD(icdd, this.ifc);
        return icdd;
    }

    private Container addIFCToICDD(Container icdd, Path ifc) {
        File ifcFile = ifc.toFile();
        icdd.addInternalDocument(ifcFile.getName(), ifcFile.getName(), ".ifc", ifcFile);
        return icdd;
    }

    /*
    Setter
     */

    public void setIfc(Path ifc) {
        this.ifc = ifc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /*
    Getter
     */

    public String getSavePath() {
        return savePath;
    }
}
