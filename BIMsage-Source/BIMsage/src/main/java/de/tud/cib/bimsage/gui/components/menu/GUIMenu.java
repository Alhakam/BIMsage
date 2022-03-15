package de.tud.cib.bimsage.gui.components.menu;

import de.tud.cib.bimsage.gui.components.menu.file.ExitButton;
import de.tud.cib.bimsage.gui.components.menu.file.SaveButton;
import de.tud.cib.bimsage.gui.components.menu.file.newproject.NewProjectButton;

import javax.swing.*;

public class GUIMenu extends JMenuBar {

    private final JMenu mFile = new JMenu("File");
    private final NewProjectButton miNewProject = new NewProjectButton("New Project");
    private JMenuItem miSaveProject = new SaveButton("Save Project");
    private JMenuItem miSaveProjectAs = new JMenuItem("Save Project as");
    private final JMenuItem miLoadProject = new JMenuItem("Load Project");
    private final ExitButton miExit = new ExitButton("Exit");

    public GUIMenu() {
        mFile.add(miNewProject);
        mFile.add(miSaveProject);
        mFile.add(miSaveProjectAs);
        mFile.add(miLoadProject);
        mFile.add(miExit);
        this.add(mFile);
        miSaveProject.setEnabled(false);
    }


    /*
    Getter
     */

    public JMenuItem getMiSaveProject() {
        return miSaveProject;
    }

    public JMenuItem getMiSaveProjectAs() {
        return miSaveProjectAs;
    }
}
