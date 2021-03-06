package de.tud.cib.bimsage.gui.utility.list.checkboxlist;

import javax.swing.*;

public class JCheckboxWithObject extends JCheckBox{
    private Object object;

    public JCheckboxWithObject (Object object){
        this.object = object;
        this.setText(object.toString());
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
        this.setText(object.toString());
    }
}
