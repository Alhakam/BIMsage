package de.tud.cib.bimsage.annotation.xmlUtil;

import de.tud.cib.bimsage.annotation.model.AnnotationData;
import de.tud.cib.bimsage.annotation.model.entry.AnnotationEntry;
import de.tud.cib.bimsage.annotation.model.entry.ClassAnnotation;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Provides functions for reading, writing and validating annotation XML files
 * Can transform data from an annotation XML file into an AnnotationData object
 */
public class AnnotationXMLParser {

    private final static String xsdPath = "src/main/resources/annotationXSD/annotationSchema.xsd";

    private AnnotationXMLParser() {}

    /**
     * Creates a JDOM Document object, which consists XML data, transformed from given annotation data
     * @param annotationData The annotation data from which a XML document should be generated
     * @return The JDOM Document containing the generated XML data
     */
    public static Document createXMLDoc(AnnotationData annotationData) {
        HashMap<String, AnnotationEntry> annotationEntryHashMap = annotationData.getAnnotationEntries();
        Document xmlDocument = new Document();
        Element root = new Element("annotation");
        for (int i = 0; i < annotationEntryHashMap.size(); i++){
            AnnotationEntry annotationEntry = annotationEntryHashMap.get(Integer.toString(i));
            if (annotationEntry.getClass() == ClassAnnotation.class)
                root.addContent(parseClassAnnotation((ClassAnnotation) annotationEntry, i));
        }
        xmlDocument.setRootElement(root);
        return xmlDocument;
    }

    /**
     * Writes a XML file through parsing given annotation data
     * @param annotationData The annotation data from which a XML file should be generated
     * @param outputPath The file path to which the XML file should be exported
     */
    public static void writeXMLDoc(AnnotationData annotationData, String outputPath) throws IOException {
        Document xmlDocument = createXMLDoc(annotationData);
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(xmlDocument, new FileOutputStream(outputPath));
    }

    /**
     * Parses a XML Document object and generates Annotation Data from it
     * @param xmlFile The xml file that needs to be parsed
     * @return AnnotationData object
     */
    public static AnnotationData parseXML(Path xmlFile) throws JDOMException, IOException {
        AnnotationData annotationData = new AnnotationData();
        SAXBuilder saxBuilder = new SAXBuilder();
        Document xmlDocument = saxBuilder.build(xmlFile.toFile());
        List<Element> classAnnotationElementList = xmlDocument.getRootElement().getChildren("IfcClass");
        for (Element classAnnotationElement :
                classAnnotationElementList) {
            String guid = classAnnotationElement.getAttributeValue("ifcGUID");
            String modelname = classAnnotationElement.getAttributeValue("model");
            String ifcClass = classAnnotationElement.getAttributeValue("name");
            ClassAnnotation classAnnotationEntry = new ClassAnnotation(guid, modelname, ifcClass);
            annotationData.addAnnotationEntry(classAnnotationEntry);
        }
        return annotationData;
    }

    /**
     * Checks a given XML file against the annotation XSD.
     * @param xmlFile The XML file that needs to be checked
     * @return returns true if the XML is valid against the XSD. Otherwise returns false.
     */
    public static boolean validateXML(Path xmlFile) throws JDOMException, IOException {
        Path xsd = Paths.get(xsdPath);
        try {
            XMLReaderJDOMFactory xmlReaderJDOMFactory = new XMLReaderXSDFactory(xsd.toFile());
            new SAXBuilder(xmlReaderJDOMFactory).build(xmlFile.toFile());
            return true;
        } catch (JDOMParseException e) {
            return false;
        }
    }

    private static Element parseClassAnnotation(ClassAnnotation classAnnotation, int id) {
        Element annotationElement = new Element("IfcClass");
        annotationElement.setAttribute("id", Integer.toString(id));
        annotationElement.setAttribute("ifcGUID", classAnnotation.getAnnotatedEntityGUID());
        annotationElement.setAttribute("model", classAnnotation.getModelName());
        annotationElement.setAttribute("name", classAnnotation.getClassName());
        return annotationElement;
    }

}
