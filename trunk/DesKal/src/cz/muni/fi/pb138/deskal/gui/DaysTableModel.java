package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Day;
import cz.muni.fi.pb138.deskal.Event;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Drak
 */
public class DaysTableModel extends AbstractTableModel {

    private List<Day> days = new ArrayList<Day>();
    private int firstDayOfMonth; //from 0 - Mon to 6 - Sun

    @Override
    public int getRowCount() {
        return 6;
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int myIndex = rowIndex * 7 + columnIndex;
        if (myIndex < firstDayOfMonth) {
            return null;
        } else {
            if (myIndex < days.size() + firstDayOfMonth) {
                Day day = days.get(myIndex - firstDayOfMonth);
                return day.getDate().getDay();
            } else {
                return null;
            }
        }
    }

    public List<Event> getEventsAt(int rowIndex, int columnIndex) {
        int myIndex = rowIndex * 7 + columnIndex;
        if (myIndex < firstDayOfMonth) {
            return null;
        } else {
            if (myIndex < days.size() + firstDayOfMonth) {
                Day day = days.get(myIndex - firstDayOfMonth);
                return day.getEvents();
            } else {
                return null;
            }
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Mon";
            case 1:
                return "Tue";
            case 2:
                return "Wed";
            case 3:
                return "Thu";
            case 4:
                return "Fri";
            case 5:
                return "Sat";
            case 6:
                return "Sun";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public int getFirstDay() {
        return firstDayOfMonth;
    }

    public void setMonth(List<Day> days) {
        if (days.isEmpty()) {
            throw new IllegalArgumentException("Empty list");
        }
        this.days = days;
        GregorianCalendar date = this.days.get(0).getDate().toGregorianCalendar();
        firstDayOfMonth = date.get(Calendar.DAY_OF_WEEK);
        firstDayOfMonth -= 2;
        switch (firstDayOfMonth) {
            case -1:
                firstDayOfMonth = 5;
            case -2:
                firstDayOfMonth = 6;
        }
        fireTableDataChanged();
    }

    public void addEventAt(int rowIndex, int columnIndex, Event event) {
        int myIndex = rowIndex * 7 + columnIndex;
        Day day = days.get(myIndex - firstDayOfMonth);
        day.getEvents().add(event);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public boolean isDayAt(int rowIndex, int columnIndex) {
        int myIndex = rowIndex * 7 + columnIndex;
        if (myIndex < firstDayOfMonth) {
            return false;
        } else {
            if (myIndex < days.size() + firstDayOfMonth) {
                return true;
            } else {
                return false;
            }
        }
    }

    public XMLGregorianCalendar getDateAt(int rowIndex, int columnIndex) {
        int myIndex = rowIndex * 7 + columnIndex;
        Day day = days.get(myIndex - firstDayOfMonth);
        return day.getDate();
    }
}
