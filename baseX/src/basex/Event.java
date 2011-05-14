package basex;

import java.sql.Time;
import java.util.ArrayList;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public class Event {

    private int id;
    private String name;
    private String place;
    private XMLGregorianCalendar dateSince;
    private XMLGregorianCalendar dateTo;
    private Duration duration;
    private Time timeSince;
    private Time timeTo;
    private String note;
    private String tag;
    private ArrayList<String> tags;



    public Event(int id) {
        this.id = id;
        tag = "";
    }

    public Event(){

    }

    public boolean isCorrect() throws DatatypeConfigurationException{
        //TODO: overeni korektni inicializace tridy Event, ie. ma nastaveny korektni id, date, duration a title
        return true;
    }

    public XMLGregorianCalendar getDate() {
        return dateSince;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.dateSince = date;
    }

    public void setDateTo(XMLGregorianCalendar date) {
        this.dateTo = date;
    }

    public void setTime(Time time){
        this.timeSince = time;
    }

    public void setTimeTo(Time time){
        this.timeTo = time;
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
        return id + ": date: " + dateSince.toString() + " to: " + dateTo.toString() + " name: " + name + " place: " + place + " note: " + note + " tag: " + tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
