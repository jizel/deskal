package cz.muni.fi.pb138.deskal;

import java.util.List;

public interface EventManager {

    public void addEvent(Event e);

    public void editEvent(Event e);

    public void removeEvent(Event e);

    public List<Event> getAllEvents();

//    public Event getEventById(int id);
    
    public List<Event> getEventsForMonth(int year, int month);

    public List<Event> getEventsForMonth(int year, int month, String tag);
}
