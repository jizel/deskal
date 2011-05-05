package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Event;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Drak
 */
public class EventListModel extends AbstractListModel {
    private List<Event> events = new ArrayList<Event>();

    public int getSize() {
        return events.size();
    }

    public Object getElementAt(int index) {
        return events.get(index).getName();
    }

    public Event getEventAt(int index) {
        return events.get(index);
    }

    public void setEvents(List<Event> events){
        this.events = events;
        fireContentsChanged(this, 0, events.size());
    }

    public void cleanList() {
        int size = events.size();
        events = new ArrayList<Event>();
        fireContentsChanged(this, 0, size);
    }

    public void remove(int index){
        events.remove(index);
        fireContentsChanged(this, 0, events.size() + 1);
    }

    public void update(){
        fireContentsChanged(this, 0, events.size());
    }
}
