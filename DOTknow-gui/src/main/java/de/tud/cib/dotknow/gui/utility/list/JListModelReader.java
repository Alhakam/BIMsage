package de.tud.cib.dotknow.gui.utility.list;

import javax.swing.*;
import java.util.LinkedHashSet;

/**
 * Reads the content of a JList model and returns it in an appropriate format
 */
public class JListModelReader {

    private JListModelReader() {}

    /**
     * Reads a table model and returns the values of all rows of a specific column
     * @param listModel the table model that should be read (instantiated as DefaultTableModel)
     * @return the LinkedHashSet<String> that contains all row values
     */
    public static LinkedHashSet<String> read(ListModel listModel) {
        LinkedHashSet<String> listContent = new LinkedHashSet<>();
        for (int i=0; i < listModel.getSize(); i++) {
            listContent.add(listModel.getElementAt(i).toString());
        }
        return listContent;
    }

}
