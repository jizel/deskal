package cz.muni.fi.pb138.deskal;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Drak
 */
public class Event {

    private String id;
    private String name;
    private String place;
    private XMLGregorianCalendar date;
    private Duration duration;
    private String note;
    private String tag;


    public Event() {

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void setTag(String tag) {
        this.tag = tag;
    }
    //metoda vraci retezec popisujici zacatek udalosti
    public String getStartString() {
        int minute = date.getMinute();
        int hour = date.getHour();
        String dateString = date.getDay() + "." + date.getMonth() + "." + date.getYear();
        String time;
        if (minute < 10) {
            time = Integer.toString(hour) + ":0" + Integer.toString(minute);
        } else {
            time = Integer.toString(hour) + ":" + Integer.toString(minute);
        }
        return dateString + "   " + time;
    }
    //metoda vraci cas zacatku - volano z GUI
    public int[] getTime() {
        int[] time = new int[2];
        time[0] = date.getHour();
        time[1] = date.getMinute();
        return time;
    }

    public void setTime(int hour, int minute) {
        this.date.setTime(hour, minute, 0);
    }

    @Override //equals pouze na atributech Name, Date a Duration - kvuli importu!!
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.duration != other.duration && (this.duration == null || !this.duration.equals(other.duration))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 13 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        return hash;
    }

}
