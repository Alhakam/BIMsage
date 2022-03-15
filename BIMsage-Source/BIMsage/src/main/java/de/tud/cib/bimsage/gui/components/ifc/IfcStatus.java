package de.tud.cib.bimsage.gui.components.ifc;

import com.apstex.ifctoolbox.ifc.IfcRoot;
import de.tud.cib.bimsage.icdd.configuration.input.ModelElement;

import java.nio.file.Path;
import java.util.LinkedHashSet;

/**
 * Singleton for storing information about the active IFC model in the platform
 */
public class IfcStatus {

    private Path ifcFile;
    private IfcEntityListener ifcEntityListener;
    private String ifcModelName;

    private static IfcStatus ourInstance = new IfcStatus();

    public static IfcStatus getInstance() {
        return ourInstance;
    }

    private IfcStatus() {
    }

    public void initIfcEntityListener() {
        ifcEntityListener = new IfcEntityListener();
    }

    public LinkedHashSet<ModelElement> getIfcModelElements() {
        LinkedHashSet<ModelElement> modelElementSet = new LinkedHashSet<>();
        LinkedHashSet<IfcRoot> ifcEntitySet = ifcEntityListener.getIfcEntities();
        for (IfcRoot ifcEntity :
                ifcEntitySet) {
            modelElementSet.add(new ModelElement(ifcModelName, ifcEntity.getGlobalId().getDecodedValue()));
        }
        return modelElementSet;
    }


    /*
    Setter
     */

    public void setIfcFile(Path ifcFile) {
        this.ifcFile = ifcFile;
    }

    public void setIfcModelName(String ifcModelName) {
        this.ifcModelName = ifcModelName;
    }

    /*
    Getter
     */

    public Path getIfcFile() {
        return ifcFile;
    }

    public String getIfcModelName() {
        return ifcModelName;
    }
}
