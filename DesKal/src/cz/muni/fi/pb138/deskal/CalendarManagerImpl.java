package cz.muni.fi.pb138.deskal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CalendarManagerImpl implements CalendarManager {

    private org.basex.core.Context context;
    private String calendarXml;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    private EventManager eventManager;
    private DatatypeFactory df;

    public CalendarManagerImpl(Context context, EventManager eventManager) {
        this.context = context;
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        calendarXml = userDir + separator + "DesKal" + separator + "calendar.xml";
        docFactory = DocumentBuilderFactory.newInstance();
        this.eventManager = eventManager;
        try {
            df = DatatypeFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("Datatype factory init error", ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Unable to init document builder", ex);
        }
    }

    public List<String> getAllTags() {
        List<String> labels = new ArrayList<String>();

        String queryForLabels = "<labels> "
                + "{ "
                + "let $doc := doc('" + calendarXml + "') "
                + "for $x in $doc//label "
                + "return <label> { $x/@name/string() } </label>"
                + "} "
                + "</labels>";

        String labelsParseXML = null;
        try {
            labelsParseXML = new XQuery(queryForLabels).execute(context);
        } catch (BaseXException ex) {
            throw new RuntimeException("Query execution error", ex);
        }

        Document doc = null;

        InputSource iS = new InputSource();
        iS.setCharacterStream(new StringReader(labelsParseXML));

        try {
            doc = docBuilder.parse(iS);
        } catch (Exception ex) {
            throw new RuntimeException("Error while parsing", ex);
        }

        NodeList nodes = doc.getElementsByTagName("label");

        for (int i = 0; i < nodes.getLength(); i++) {
            labels.add(nodes.item(i).getTextContent());
        }

        return labels;
    }

    public List<Day> getDaysInMonthWithTag(int year, int month, String tag) {
        List<Day> days = createDaysForMonth(year, month);
        List<Event> events = eventManager.getEventsForMonth(year, month);

        for (Event event : events) {
            XMLGregorianCalendar eventStartDate = (XMLGregorianCalendar) event.getDate().clone();
            Duration eventDuration = event.getDuration();
            int lastsDays = eventDuration.getDays();
            //duration zasahuje do dalsiho dne
            if (eventDuration.getHours() > 0 || eventDuration.getMinutes() > 0) {
                lastsDays++;
            } else {//cas zacatku zasahuje do dalsiho dne
                if (eventStartDate.getHour() > 0 || eventStartDate.getMinute() > 0) {
                    lastsDays++;
                }
            }//preteceni hodin a minut pres pulnoc
            int hours = eventStartDate.getHour() + eventDuration.getHours();
            int minutes = eventStartDate.getMinute() + eventDuration.getMinutes();
            if (minutes >= 60) {
                hours++;
            }
            if (hours >= 24) {
                lastsDays++;
            }

            eventStartDate.setTime(0, 0, 0); //kvuli compare 
            //udalost zacina prvniho
            if (eventStartDate.compare(days.get(0).getDate()) == DatatypeConstants.EQUAL) {
                if (eventDuration.getYears() > 0 || eventDuration.getMonths() > 0
                        || lastsDays >= days.size()) {
                    for (Day day : days) {
                        day.addEvent(event);
                    }
                } else {
                    for (int i = 0; i < lastsDays; i++) {
                        days.get(i).addEvent(event);
                    }
                }
            }
            //udalost zacina po prvnim dni
            if (eventStartDate.compare(days.get(0).getDate()) == DatatypeConstants.GREATER) {
                if (eventDuration.getYears() > 0 || eventDuration.getMonths() > 0
                        || lastsDays > days.size() - event.getDate().getDay()) {
                    for (int i = eventStartDate.getDay(); i <= days.size(); i++) {
                        days.get(i - 1).addEvent(event);
                    }
                } else {
                    int startDay = eventStartDate.getDay();
                    for (int i = startDay; i < startDay + lastsDays; i++) {
                        days.get(i - 1).addEvent(event);
                    }
                }
            }
            //udalost zacala drive
            if (eventStartDate.compare(days.get(0).getDate()) == DatatypeConstants.LESSER) {
                XMLGregorianCalendar endDate = (XMLGregorianCalendar) event.getDate().clone();
                endDate.add(eventDuration);
                for (Day day : days) {
                    if (day.getDate().compare(endDate) == DatatypeConstants.LESSER
                            || day.getDate().compare(endDate) == DatatypeConstants.EQUAL) {
                        day.addEvent(event);
                    } else {
                        break;
                    }
                }
            }
        }
        return days;
    }

    public void addFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private List<Day> createDaysForMonth(int year, int month) {
        List<Day> days = new ArrayList<Day>();
        GregorianCalendar date = new GregorianCalendar();
        date.set(year, month - 1, 1);
        int lastDay = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        String monthStr = null;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = Integer.toString(month);
        }
        String actDate = year + "-" + monthStr + "-";
        //create days for given month
        for (int i = 1; i <= lastDay; i++) {
            String xmlDate = null;
            if (i > 9) {
                xmlDate = actDate + i + "Z";
            } else {
                xmlDate = actDate + "0" + i + "Z";
            }
            Day day = new Day();
            XMLGregorianCalendar dayDate = df.newXMLGregorianCalendar(xmlDate);
            dayDate.setTime(0, 0, 0);
            day.setDate(dayDate);
            days.add(day);
        }
        return days;
    }
}
