package cz.muni.fi.pb138.deskal;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

public class Day {

    private XMLGregorianCalendar date;
    private List<Event> events;

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
