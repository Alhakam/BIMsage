package de.tud.cib.bimsage.gui.components.menu.file;

import de.tud.cib.bimsage.icdd.activedata.ActiveICDD;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SaveButton extends JMenuItem {

    public SaveButton(String name) {
        super(name);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ActiveICDD.getInstance().saveICDD();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
