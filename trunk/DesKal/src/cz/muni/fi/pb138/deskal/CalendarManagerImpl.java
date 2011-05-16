package cz.muni.fi.pb138.deskal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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

    public List<Day> getDaysInMonthWithTag(int year, int month) {
        List<Day> days = new ArrayList<Day>();
        List<Event> events = new ArrayList<Event>();




        //ZMENIT NA GET EVENTS FOR MONTH AZ BUDE HOTOVA!!!
        events = eventManager.getAllEvents();
        GregorianCalendar date = new GregorianCalendar();
        date.set(year, month - 1 , 1);
        int lastDay = date.getActualMaximum(Calendar.DAY_OF_MONTH);
        String monthStr = null;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = Integer.toString(month);
        }
        String actDate = year + "-" + monthStr + "-";

        for (int i = 1; i <= lastDay; i++) {
            String xmlDate = null;
            if (i > 9) {
                xmlDate = actDate + i;
            } else {
                xmlDate = actDate + "0" + i;
            }
            Day day = new Day();
            XMLGregorianCalendar dayDate = df.newXMLGregorianCalendar(xmlDate);
            day.setDate(dayDate);
            Iterator it = events.iterator();
            while (it.hasNext()) {
                Event event = (Event) it.next();
                if (event.getDate().getDay() == i) {
                    day.addEvent(event);
                    //events.remove(event); THREAD CONCURRENT EXCEPTION WTF??
                }
            }
            days.add(day);
        }
        return days;
    }

    public void addFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
