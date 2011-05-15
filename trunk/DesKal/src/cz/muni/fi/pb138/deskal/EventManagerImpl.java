package cz.muni.fi.pb138.deskal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class EventManagerImpl implements EventManager {

    private org.basex.core.Context context;
    private String calendarXml;
    private DatatypeFactory df;

    public EventManagerImpl(Context context) {
        this.context = context;
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        calendarXml = userDir + separator + "DesKal" + separator + "calendar.xml";
    }

    public void addEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void editEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Event getEventById(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Event> getAllEvents() {

        String queryForEvents = "<events>"
                + "{ "
                + "let $doc := doc('" + calendarXml + "') "
                + "for $x in $doc//event "
                + "return <event id = \"{$x/@id/string()}\"> "
                + "<dateSince>{$x/dateSince/string()}</dateSince>"
                + "<dateTo>{$x/dateTo/string()}</dateTo>"
                + "<timeFrom>{$x/timeFrom/string()}</timeFrom>"
                + "<timeTo>{$x/timeTo/string()}</timeTo>"
                + "<title>{$x/title/string()}</title>"
                + "<place>{$x/place/string()}</place>"
                + "<note>{$x/note/string()}</note>"
                + "<tag>{$x/tags/tag/@tagref/string()}</tag>"
                + "</event>"
                + "} "
                + "</events>";

        Document doc = getDocumentFromQuery(queryForEvents);

        return parseDocument(doc);

    }

    public List<Event> getEventsForMonth(int year, int month) {

        GregorianCalendar sinceHelp = new GregorianCalendar();
        sinceHelp.set(year, month, Calendar.DAY_OF_MONTH);
        int lastDay = sinceHelp.getActualMaximum(Calendar.DAY_OF_MONTH);

        XMLGregorianCalendar since = null;
        XMLGregorianCalendar to = null;
        try {
            since = DatatypeFactory.newInstance().newXMLGregorianCalendar(sinceHelp);
            to = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            to.setYear(year);
            to.setMonth(month);
            to.setDay(lastDay);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(EventManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }


        String sSince = since.toXMLFormat();
        String sTo = to.toXMLFormat();

        String queryForEvents2 = "<events> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "return $doc//event[ "
                + "(dateSince/text() >= '" + sSince + "' and dateTo/text() <= '" + sTo + "') " //cely event je mezi daty
                + " or (dateTo/text() >= '" + sSince + "' and dateTo/text() < '" + sTo + "') "
                + " or (dateSince/text() >= '" + sSince + "' and dateSince/text() <= '" + sTo + "') "
                + " or (dateSince/text() < '" + sSince + "' and dateTo/text() > '" + sTo + "')" //event zacina pred pocatecnim datem a konci po koncovym
                + " ] "
                + "} "
                + "</events>";


        Document doc = getDocumentFromQuery(queryForEvents2);

        return parseDocument(doc);
    }

//help methods
    public Document getDocumentFromQuery(String query) {
        String labelsParseXML = null;
        try {
            labelsParseXML = new XQuery(query).execute(context);
        } catch (BaseXException ex) {
        }

        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
        }

        InputSource iS = new InputSource();
        iS.setCharacterStream(new StringReader(labelsParseXML));

        try {
            doc = builder.parse(iS);
        } catch (Exception ex) {
        }
        return doc;
    }

    private List<Event> parseDocument(Document doc) {
        List<Event> events = new ArrayList<Event>();

        NodeList eventNodes = doc.getElementsByTagName("event");

        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventEl = (Element) eventNodes.item(i);
            Event event = new Event();

            Integer id = Integer.parseInt(eventEl.getAttribute("id"));
            event.setId(id);

            NodeList n = eventEl.getElementsByTagName("dateSince");
            String dateSinceStr = n.item(0).getTextContent();
            XMLGregorianCalendar dateSince = null;
            try {dateSince = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateSinceStr);
                event.setDate(dateSince);
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(EventManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            n = eventEl.getElementsByTagName("timeFrom");
            String[] timeFromStr = (n.item(0).getTextContent()).split(":");
            int hoursFrom = Integer.parseInt(timeFromStr[0]);
            int minutesFrom = Integer.parseInt(timeFromStr[1]);
            int secondsFrom = Integer.parseInt(timeFromStr[2]);
            event.getDate().setTime(hoursFrom, minutesFrom, secondsFrom);

            n = eventEl.getElementsByTagName("dateTo");
            String dateTo = n.item(0).getTextContent();

            n = eventEl.getElementsByTagName("timeTo");
            String[] timeToStr = (n.item(0).getTextContent()).split(":");
            int hoursTo = Integer.parseInt(timeToStr[0]);
            int minutesTo = Integer.parseInt(timeToStr[1]);
            int secondsTo = Integer.parseInt(timeToStr[2]);

            try {
                XMLGregorianCalendar to = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTo);
                to.setTime(hoursTo, minutesTo, secondsTo);
                long duration = to.toGregorianCalendar().getTime().getTime() - dateSince.toGregorianCalendar().getTime().getTime();
                Duration dur = DatatypeFactory.newInstance().newDuration(duration);
                System.out.println(dur.toString());
                event.setDuration(dur);
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(EventManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            n = eventEl.getElementsByTagName("title");
            event.setName(n.item(0).getTextContent());

            n = eventEl.getElementsByTagName("place");
            event.setPlace(n.item(0).getTextContent());

            n = eventEl.getElementsByTagName("note");
            event.setNote(n.item(0).getTextContent());

            n = eventEl.getElementsByTagName("tag");
            event.setTag(n.item(0).getTextContent());

            events.add(event);
        }

        return events;
    }
}