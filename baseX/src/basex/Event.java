package basex;

import java.util.ArrayList;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

public class Event {

    private int id;
    private String name;
    private String place;
    private XMLGregorianCalendar date;
    private XMLGregorianCalendar to;
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
        return date;
    }

    public void setDate(XMLGregorianCalendar date) {
        this.date = date;
    }

    public void setDateTo(XMLGregorianCalendar date) {
        this.to = date;
    }

    /*public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
*/
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
        return id + ": date: " + date.toString() + " to: " + to.toString() + " name: " + name + " place: " + place + " note: " + note + " tag: " + tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
