package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Filter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Drak
 */
public class FiltersComboBoxModel extends DefaultComboBoxModel{
    private List<Filter> filters = new ArrayList<Filter>();

    public Object getElementAt(int index) {
        return filters.get(index).getName();
    }

    @Override
    public int getSize(){
        return filters.size();
    }

    public void setFilters(List<Filter> filters){
        this.filters = filters;
        fireContentsChanged(this, 0, filters.size());
    }

    public void update(){
        fireContentsChanged(this, 0, filters.size());
    }
}
