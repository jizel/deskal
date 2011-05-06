package cz.muni.fi.pb138.deskal;

import java.util.ArrayList;
import javax.xml.datatype.DatatypeConstants;
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

    public String toStringAll() {
        return id + ": " + date.toString() + " " + duration.toString() + " " + name + " " + place + " " + note;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTimeString() {
        int minute = date.getMinute();
        int hour = date.getHour();
        String time;
        if (minute != DatatypeConstants.FIELD_UNDEFINED && hour != DatatypeConstants.FIELD_UNDEFINED) {
            if (minute < 10) {
                time = Integer.toString(hour) + " : 0" + Integer.toString(minute);
            } else {
                time = Integer.toString(hour) + " : " + Integer.toString(minute);
            }
            return time;
        } else {
            throw new IllegalArgumentException(this.getName() + "Duration missing");
        }
    }

    public String getDurationString() {
        if (this.duration == null) {
            return null;
        }
        String duration = Integer.toString(this.duration.getHours()) + " hours";
        return duration;
    }

    public int[] getTime() {
        int[] time = new int[2];
        time[0] = date.getHour();
        time[1] = date.getMinute();
        return time;
    }

    public void setTime(int hour, int minute) {
        this.date.setHour(hour);
        this.date.setMinute(minute);
    }
}
