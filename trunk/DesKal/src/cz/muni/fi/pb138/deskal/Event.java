/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.deskal;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Drak
 */
public class Event {

    private int id;
    private String name;
    private String place;
    private XMLGregorianCalendar date;
    private Duration duration;
    private String note;
    private String tag;

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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStartString() {
        int minute = date.getMinute();
        int hour = date.getHour();
        String dateString = date.getDay() +"."+date.getMonth()+"."+date.getYear();
        String time;
        if (minute < 10) {
            time = Integer.toString(hour) + ":0" + Integer.toString(minute);
        } else {
            time = Integer.toString(hour) + ":" + Integer.toString(minute);
        }
        return  dateString + "    " + time;
    }

    public String getDurationString() {
        int years = duration.getYears();
        int months = duration.getMonths();
        int days = duration.getDays();
        int hours = duration.getHours();
        int minutes = duration.getMinutes();

        //todo: lokalizace
        String yearsString = years == 0 ? "" : (Integer.toString(years) + (years == 1 ? " Year   " : " Years   "));
        String monthsString = months == 0 ? "" : (Integer.toString(months) + (months == 1 ? " Month   " : " Months   "));
        String daysString = days == 0 ? "" : (Integer.toString(days) + (days == 1 ? " Day   " : " Days   "));
        String hoursString = hours == 0 ? "" : (Integer.toString(hours) + (hours == 1 ? " Hour   " : " Hours   "));
        String minutesString = minutes == 0 ? "" : (Integer.toString(minutes) + (minutes == 1 ? " Minute   " : " Minutes   "));

        String duration =  yearsString + monthsString + daysString + hoursString + minutesString;
        return duration;
    }

    public int[] getTime(){
        int[] time = new int[2];
        time[0] = date.getHour();
        time[1] = date.getMinute();
        return time;
    }

    public void setTime(int hour,int minute){
        this.date.setTime(hour, minute, 0);
    }

    //equals and hashcode on all attributes
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.place == null) ? (other.place != null) : !this.place.equals(other.place)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.duration != other.duration && (this.duration == null || !this.duration.equals(other.duration))) {
            return false;
        }
        if ((this.note == null) ? (other.note != null) : !this.note.equals(other.note)) {
            return false;
        }
        if ((this.tag == null) ? (other.tag != null) : !this.tag.equals(other.tag)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.place != null ? this.place.hashCode() : 0);
        hash = 29 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 29 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        hash = 29 * hash + (this.note != null ? this.note.hashCode() : 0);
        hash = 29 * hash + (this.tag != null ? this.tag.hashCode() : 0);
        return hash;
    }

    
}
