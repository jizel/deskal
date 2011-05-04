package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Filter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Drak
 */
public class FiltersListModel extends AbstractListModel {

    private List<Filter> filters = new ArrayList();

    public int getSize() {
        return filters.size();
    }

    public Object getElementAt(int index) {
        return filters.get(index).getName();
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
        fireContentsChanged(this, 0, filters.size());
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
        fireContentsChanged(this, filters.size() - 1, filters.size());
    }

    public void removeFilter(Filter filter) {
        filters.remove(filter);
        fireContentsChanged(this, filters.size(), filters.size() + 1);
    }
}
