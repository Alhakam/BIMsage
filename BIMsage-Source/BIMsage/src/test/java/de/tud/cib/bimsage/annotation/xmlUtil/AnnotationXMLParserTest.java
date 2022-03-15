package de.tud.cib.bimsage.annotation.xmlUtil;

import de.tud.cib.bimsage.annotation.model.AnnotationData;
import de.tud.cib.bimsage.annotation.model.entry.AnnotationEntry;
import de.tud.cib.bimsage.annotation.model.entry.ClassAnnotation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class AnnotationXMLParserTest {

    AnnotationData annotationData;
    ClassAnnotation abutmentAnnotation;
    ClassAnnotation reinforcementAnnotation;
    String output = "src/test/resources/annotation/output/testOutput.xml";
    String xsdPath = "src/main/resources/annotationXSD/annotationSchema.xsd";
    String testXMLPath = "src/test/resources/annotation/testAnnotation.xml";

    @Before
    public void initAnnotationData() {
        annotationData = new AnnotationData();
        abutmentAnnotation = new ClassAnnotation("123", "testModel.ifc", "IfcAbutment");
        reinforcementAnnotation = new ClassAnnotation("456", "testModel.ifc", "IfcReinforcingBar");
        annotationData.addAnnotationEntry(abutmentAnnotation);
        annotationData.addAnnotationEntry(reinforcementAnnotation);
    }

    @Test
    public void testCreateDoc() {
        Element testElementAbutment = null;
        Element testElementReinforcement = null;
        Document xmlDoc = AnnotationXMLParser.createXMLDoc(annotationData);
        List<Element> xmlElementList = xmlDoc.getRootElement().getChildren("IfcClass");
        for (Element xmlElement :
                xmlElementList) {
            if (xmlElement.getAttributeValue("id").equals("0"))
                testElementAbutment = xmlElement;
            else
                testElementReinforcement = xmlElement;
        }
        assertEquals(abutmentAnnotation.getClassName(), testElementAbutment.getAttributeValue("name"));
        assertEquals(reinforcementAnnotation.getClassName(), testElementReinforcement.getAttributeValue("name"));
    }

    @Test
    public void testWriteXML() throws JDOMException, IOException {
        AnnotationXMLParser.writeXMLDoc(annotationData, output);
        Path outputFile = Paths.get(output);
        Path xsdSchema = Paths.get(xsdPath);
        assertTrue(validateXML(outputFile, xsdSchema));
    }

    private boolean validateXML(Path xml, Path xsd) throws JDOMException, IOException {
        try {
            XMLReaderJDOMFactory xmlReaderJDOMFactory = new XMLReaderXSDFactory(xsd.toFile());
            new SAXBuilder(xmlReaderJDOMFactory).build(xml.toFile());
            return true;
        } catch (JDOMParseException e) {
            return false;
        }
    }

    @Test
    public void testParseXML() throws JDOMException, IOException {
        AnnotationData annotationData = AnnotationXMLParser.parseXML(Paths.get(testXMLPath));
        HashMap<String, AnnotationEntry> annotationEntryMap = annotationData.getAnnotationEntries();
        MatcherAssert.assertThat(annotationEntryMap.get("0"), Matchers.<AnnotationEntry>samePropertyValuesAs(
                new ClassAnnotation("1ZJKF$F_f84ww7__q1FDPX", "testModel.ifc", "IfcAbutment")));
        MatcherAssert.assertThat(annotationEntryMap.get("1"), Matchers.<AnnotationEntry>samePropertyValuesAs(
                new ClassAnnotation("2M6f_UD1nEkvEACW0qZrgl", "testModel.ifc", "IfcReinforcement")));
    }

    @Test
    public void testValidateXML() throws JDOMException, IOException {
        assertTrue(AnnotationXMLParser.validateXML(Paths.get(testXMLPath)));
    }

}
