package de.tud.cib.bimsage.icdd.activedata;

import de.tud.cib.bimsage.icdd.utility.filter.ICDDFilter;
import de.tud.cib.bimsage.icdd.configuration.ICDDModifier;
import icdd.beans.Container;
import icdd.beans.ICDDDocument;
import icdd.beans.ICDDInternalDocument;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Singleton used for storing the active ICDD Container used in the platform
 * Provides functionality for saving the active ICDD in its save path
 * Provides functionality for retrieving basic information about the ICDD
 */
public class ActiveICDD {

    private Container icdd;
    private String savePath;

    private ActiveICDD() {}

    private static ActiveICDD instance = new ActiveICDD();

    public static ActiveICDD getInstance() {
        return instance;
    }

    /**
     * Writes the active ICDD Container in its given savePath
     */
    public void saveICDD() throws IOException {
        icdd.writeICDDContainer(savePath);
    }

    /**
     * Writes or updates the given ontology to the ICDD container
     * @param ontology The OntModel representation of the ontology that will be written in the ICDD file
     */
    public void saveOntologyinICDD(OntModel ontology) throws IOException {
        ICDDInternalDocument ontologyDoc = (ICDDInternalDocument) ICDDFilter.filterDocument("Construction.ttl", icdd);
        File ontologyFile = ontologyDoc.getFile();
        RDFDataMgr.write(new FileOutputStream(ontologyFile), ontology, RDFFormat.TTL);
        ICDDModifier.addResourceToICDD(ontologyFile.toPath(), ActiveICDD.getInstance().getIcdd());
    }

    /**
     * Returns all document names of the current active ICDD
     * @return the document names stored in a Vector<String> object
     */
    public Vector<String> listDocuments() {
        Vector<String> documentList = new Vector<>();
        Iterator<ICDDDocument> documentIterator = icdd.getContainerDescription().listDocuments();
        while (documentIterator.hasNext())
            documentList.add(documentIterator.next().getName());
        return documentList;
    }

    /*
    Setter
     */

    public void setIcdd(Container icdd) {
        this.icdd = icdd;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /*
    Getter
     */

    public Container getIcdd() {
        return icdd;
    }

    public String getSavePath() {
        return savePath;
    }
}
