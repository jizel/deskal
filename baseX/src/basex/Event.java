package basex;

import java.util.ArrayList;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public class Event {

    private int id;
    private String name;
    private String place;
    private XMLGregorianCalendar date;
    private Duration duration;
    private String note;
    private String tag;
    private ArrayList<String> tags;

    public Event(String name, XMLGregorianCalendar date, Duration duration) {
        this.name = name;
        this.date = date;
        this.duration = duration;
    }

    public Event(int id) {
        this.id = id;
    }

    public Event(){

    }

    public XMLGregorianCalendar getDate() {
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTag() {
        return tag;
    }

    public String toStringAll(){
        return id + ": " + date.toString() + " " + duration.toString() + " " + name + " " + place + " " + note;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
