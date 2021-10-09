package de.tud.cib.dotknow.gui.components.icdd;

import com.google.common.io.Files;
import de.tud.cib.dotknow.gui.utility.messages.MessageFactory;
import de.tud.cib.dotknow.icdd.activedata.ActiveICDD;
import de.tud.cib.dotknow.icdd.utility.filter.ICDDFilter;
import de.tud.dotknow.ontology.representation.ActiveOntology;
import icdd.beans.Container;
import icdd.beans.ICDDDocument;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

//TODO: Überlegen ob die ActiveFile Klassen alle in den FileManager integriert werden.
/*
Manages the files of the active ICDD and updates changes made to files
 */
public class ICDDFileManager {

    private Container activeICDD;

    private static ICDDFileManager ourInstance = new ICDDFileManager();

    public static ICDDFileManager getInstance() {
        return ourInstance;
    }

    private ICDDFileManager() {
        activeICDD = ActiveICDD.getInstance().getIcdd();
    }

    /*
    File Update functions
     */

    public void updateOntology() throws IOException {
        File tempfile = new File(Files.createTempDir(), "Construction.ttl");
        RDFDataMgr.write(new FileOutputStream(tempfile), ActiveOntology.getInstance().getOntology(), RDFFormat.TTL);
        Container activeICDD = ActiveICDD.getInstance().getIcdd();
        Vector<String> documentList = ActiveICDD.getInstance().listDocuments();
        for (String documentName :
                documentList) {
            if (documentName.equals(tempfile.getName())) {
                ICDDDocument oldDocument = ICDDFilter.filterDocument(documentName, activeICDD);
                activeICDD.removeDocument(oldDocument);
                activeICDD.addInternalDocument(tempfile);
                ICDDDocument newDocument = ICDDFilter.filterDocument(tempfile.getName(), activeICDD);
                newDocument.setName(documentName);
            }
        }
        MessageFactory.showSimpleMessage(MessageFactory.MessageType.ONTOLOGY_UPDATED);
    }

}
