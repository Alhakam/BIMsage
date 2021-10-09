package de.tud.cib.dotknow.gui.utility;

import javax.swing.*;
import java.io.File;

/**
 * Provides FileChooser objects with various functionality
 */
public class FileChooserUtility extends JFileChooser{

    private FileChooserUtility(){}

    /**
     * Preloads a JFileChooser object for faster access during runtime
     * Method should be used at starting process of the platform
     */
    public static void preLoad() {
        JFileChooser fileChooser = new JFileChooser();
    }

    /**
     * Opens a dialog for file choice.
     * @return The File object, resulting from the file choice
     */
    public static File browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        return null;
    }

    /**
     * Opens a dialog for file choice, of which the chosen file is transferred as String to a given JTextField
     * @param textField JTextField, of which its content is overwritten with the chosen File Path
     */
    public static void browseFileForTextField(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        changeTextField(fileChooser, textField);
    }

    /**
     * Opens a dialog for folder choice, of which the chosen folder is transferred as String to a given JTextField
     * @param textField JTextField, of which its content is overwritten with the chosen Folder Path
     */
    public static void browseFolderForTextField(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        changeTextField(fileChooser, textField);
    }

    private static void changeTextField (JFileChooser fileChooser, JTextField textField) {
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

}
