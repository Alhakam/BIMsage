package de.tud.cib.bimsage.gui.components.ifc;

import com.apstex.gui.core.kernel.Kernel;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelNode;
import com.apstex.gui.core.model.applicationmodel.ApplicationModelRootListener;
import com.apstex.gui.core.model.selectionmodel.SelectionModelListener;
import com.apstex.ifctoolbox.ifc.IfcRoot;
import com.apstex.step.core.ClassInterface;
import de.tud.cib.bimsage.gui.components.ComponentController;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Object Listener that stores selected IFC entites of the active IFC model in the platform in a LinkedHashSet
 */
public class IfcEntityListener implements SelectionModelListener, ApplicationModelRootListener {

    private LinkedHashSet<IfcRoot> ifcEntities;

    public IfcEntityListener() {
        Kernel.getApplicationModelRoot().addListener(this);
    }

    @Override
    public void nodesAdded(Collection<ApplicationModelNode> nodes)
    {
        for (ApplicationModelNode node : nodes)
        {
            node.getSelectionModel().addSelectionModelListener(ClassInterface.class, this);
        }
    }

    @Override
    public void nodesRemoved(Collection<ApplicationModelNode> nodes)
    {
        for (ApplicationModelNode node : nodes)
        {
            node.getSelectionModel().removeSelectionModelListener(ClassInterface.class, this);
        }
    }

    @Override
    public void objectsSelected(Collection<Object> objects, boolean isSelected) {
        ifcEntities = new LinkedHashSet<>();
        for (Object object :
                objects) {
            ifcEntities.add((IfcRoot) object);
        }

        //Queries for linked elements and updates the Link table
        ComponentController.getInstance().updateLinkTable(ifcEntities);
    }

    public LinkedHashSet<IfcRoot> getIfcEntities() {
        return ifcEntities;
    }
}
