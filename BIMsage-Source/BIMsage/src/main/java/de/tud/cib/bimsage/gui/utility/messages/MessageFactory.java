package de.tud.cib.bimsage.gui.utility.messages;

import javax.swing.*;

/**
 * Provides functions for generating various MessageDialog Objects
 */
public class MessageFactory {

    private MessageFactory() {}

    //TODO:Dokumentation um weitere Message-Typen ergänzen
    //TODO:Überlegen eine überladene Methode für die Messages zu erstellen

    /**
     * Shows a simple MessageDialog based on a given MessageType
     * Possible Message Types:
     *  - IFC_FILE_ERROR: IFC File could not be loaded.
     *  - ICDD_SAVE_ERROR: Saving ICDD failed.
     *  - ICDD_COMPLETE: ICDD has been successfully created.
     * @param messageType the given MessageType Enum
     */
    public static void showSimpleMessage(MessageType messageType) {

        switch(messageType) {

            case IFC_FILE_ERROR:
                JOptionPane.showMessageDialog(null,
                        "IFC File could not be loaded.",
                        "IFC-File Error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case ICDD_SAVE_ERROR:
                JOptionPane.showMessageDialog(null,
                        "Saving ICDD failed.",
                        "ICDD Save Error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case ICDD_COMPLETE:
                JOptionPane.showMessageDialog(null,
                        "ICDD has been successfully created.",
                        "ICDD Creation finished",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case LINK_DOCUMENTS:
                JOptionPane.showMessageDialog(null,
                        "Documents have been successfully linked",
                        "Documents linked",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case ANNOTATION_COMPLETE:
                JOptionPane.showMessageDialog(null,
                        "Annotation has been successfully applied on the selected elements",
                        "Element Annotation complete",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case CONSTRUCTION_ONTOLOGY_COMPLETE:
                JOptionPane.showMessageDialog(null,
                        "Construction Ontology has been generated and linked with the active IFC model",
                        "Construction Ontology complete",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case ONTOLOGY_UPDATED:
                JOptionPane.showMessageDialog(null,
                        "The Ontology has been successfully updated",
                        "Ontology updated",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case REASONING_FINISHED:
                JOptionPane.showMessageDialog(null,
                        "The Ontology has been successfully reasoned against the selected rulesets",
                        "Ontology reasoned",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case VALIDATION_SUCCESSFUL:
                JOptionPane.showMessageDialog(null,
                        "The validation of the ontology was successful. No validation errors found.",
                        "Ontology validation successful",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case VALIDATION_FAILED:
                JOptionPane.showMessageDialog(null,
                        "The validation of the ontology has been processed. Validation errors occured ",
                        "Ontology validation failed",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case ERROR_NO_DAMAGE_ELEMENTS:
                JOptionPane.showMessageDialog(null,
                        "There exists no damage elements for assignment",
                        "No damage elements existent",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case ERROR_NO_DAMAGES:
                JOptionPane.showMessageDialog(null,
                        "No damages have been defined",
                        "No Damages available",
                        JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void showMessageWithCustomString(MessageType messageType, String customText) {

        switch(messageType) {

            case ICDD_ADDEDRESOURCE:
                JOptionPane.showMessageDialog(null,
                        customText + "has been added to the current ICDD.",
                        "Resource added",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case DAMAGE_CREATED:
                JOptionPane.showMessageDialog(null,
                        customText + " has been created and added to the ontology.",
                        "Damage created",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case DAMAGE_MODIFIED:
                JOptionPane.showMessageDialog(null,
                        customText + " has been modified.",
                        "Damage modified",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case DAMAGE_ASSIGNED:
                JOptionPane.showMessageDialog(null,
                        "Damages have been assigned to " + customText,
                        "Damages assigned",
                        JOptionPane.INFORMATION_MESSAGE);
                break;

            case DAMAGE_DELETED:
                JOptionPane.showMessageDialog(null,
                        customText + " has been deleted",
                        "Damages deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    public enum MessageType {
        IFC_FILE_ERROR ,
        ICDD_SAVE_ERROR ,
        ICDD_COMPLETE ,
        ICDD_ADDEDRESOURCE ,
        LINK_DOCUMENTS ,
        ANNOTATION_COMPLETE ,
        CONSTRUCTION_ONTOLOGY_COMPLETE ,
        ONTOLOGY_UPDATED ,
        DAMAGE_CREATED ,
        DAMAGE_MODIFIED ,
        DAMAGE_ASSIGNED ,
        DAMAGE_DELETED ,
        REASONING_FINISHED ,
        VALIDATION_SUCCESSFUL ,
        VALIDATION_FAILED ,
        ERROR_NO_DAMAGE_ELEMENTS ,
        ERROR_NO_DAMAGES
    }

}
