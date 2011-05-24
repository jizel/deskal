package cz.muni.fi.pb138.deskal;

import java.io.IOException;
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
import org.basex.core.cmd.Export;
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
        List<Event> events = null;
        if (tag == null || tag.equals("bez filtru")) {
            events = eventManager.getEventsForMonth(year, month);
        } else {
            events = eventManager.getEventsForMonth(year, month, tag);
        }

        for (Event event : events) {
            XMLGregorianCalendar eventStartDate = event.getDate();
            Duration eventDuration = event.getDuration();
            XMLGregorianCalendar endDate = (XMLGregorianCalendar) event.getDate().clone();
            endDate.add(eventDuration);
            //udalost zacala drive
            if (eventStartDate.compare(days.get(0).getDate()) == DatatypeConstants.LESSER) {
                for (Day day : days) {
                    int compare = day.getDate().compare(endDate);
                    if (compare == DatatypeConstants.LESSER
                            || compare == DatatypeConstants.EQUAL) {
                        day.addEvent(event);
                    } else {
                        break;
                    }
                }
            } //udalost zacina dany mesic
            else {
                int i = eventStartDate.getDay() - 1;
                while (i < days.size()
                        && (endDate.compare(days.get(i).getDate()) == DatatypeConstants.GREATER
                        || endDate.compare(days.get(i).getDate()) == DatatypeConstants.EQUAL)) {
                    days.get(i).addEvent(event);
                    i++;
                }
            }
        }
        return days;
    }

    public void addFilter(Filter filter) {

        String xquery = "insert node (<label name='" + filter.getName() 
                + "'/>) into /calendar/labels";

        XQuery xQuery = new XQuery(xquery);
        try {
            xQuery.execute(context);
        } catch (BaseXException ex) {
            throw new RuntimeException("Error while adding label to calendar.xml", ex);
        }
        try {
            Export.export(context, context.data);
        } catch (IOException ex) {
            throw new RuntimeException("Error while adding label to calendar.xml", ex);
        }
    }

    public void removeFilter(Filter filter) {

        String xquery = "delete node /calendar/labels/label[@name='" + filter.getName() + "']";
        String xquery2 = "delete node //tag[@tagref='" + filter.getName() + "']";
        XQuery xQuery = new XQuery(xquery);
        XQuery xQuery2 = new XQuery(xquery2);
        try {
            xQuery.execute(context);
            xQuery2.execute(context);
        } catch (BaseXException ex) {
            throw new RuntimeException("Error while adding label to calendar.xml", ex);
        }
        try {
            Export.export(context, context.data);
        } catch (IOException ex) {
            throw new RuntimeException("Error while adding label to calendar.xml", ex);
        }
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
