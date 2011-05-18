package cz.muni.fi.pb138.deskal;

import java.text.MessageFormat;
import java.util.Locale;
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
    private MessageFormat dateCzFormat;

    public Event() {
        Locale czech = new Locale("cs", "CZ");
        String sablonaTextu = "{0,choice,0#|1#{0} rok|2#{0} roky|3#{0} roky|4#{0} roky|4<{0} let}"
                + "  {1,choice,0#|1#{1} měsíc|2#{1} měsíce|3#{1} měsíce|4#{1} měsíce|4<{1} měsíců}"
                + "  {2,choice,0#|1#{2} den|2#{2} dny|3#{2} dny|4#{2} dny|4<{2} dní}"
                + "  {3,choice,0#|1#{3} hodina|2#{3} hodiny|3#{3} hodiny|4#{3} hodiny|4<{3} hodin}"
                + "  {4,choice,0#|1#{4} minuta|2#{4} minuty|3#{4} minuty|4#{4} minuty|4<{4} minut}";
        dateCzFormat = new MessageFormat(sablonaTextu, czech);
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

    public String getDurationString() {
        int years = duration.getYears();
        int months = duration.getMonths();
        int days = duration.getDays();
        int hours = duration.getHours();
        int minutes = duration.getMinutes();
        return dateCzFormat.format(new Object[]{ years , months, days, hours , minutes});
    }

    public int[] getTime() {
        int[] time = new int[2];
        time[0] = date.getHour();
        time[1] = date.getMinute();
        return time;
    }

    public void setTime(int hour, int minute) {
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
//        equals by nemelo byt na id kvuli porovnavani eventu pri importu
//        if (this.id != other.id) {
//            return false;
//        }
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
//        hash = 29 * hash + this.id;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.place != null ? this.place.hashCode() : 0);
        hash = 29 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 29 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        hash = 29 * hash + (this.note != null ? this.note.hashCode() : 0);
        hash = 29 * hash + (this.tag != null ? this.tag.hashCode() : 0);
        return hash;
    }
}
