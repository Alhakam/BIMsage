package de.tud.cib.bimsage.gui.components.menu.file.newproject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewProjectButton extends JMenuItem {

    public NewProjectButton (String name) {
        super(name);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewProjectDialog();
            }
        });
    }

}
