package cz.muni.fi.pb138.deskal;

import java.io.IOException;
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
import org.basex.core.cmd.Export;
import org.basex.core.cmd.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class EventManagerImpl implements EventManager {

    private org.basex.core.Context context;
    private String calendarXml;
    private DatatypeFactory df;
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder builder;

    public EventManagerImpl(Context context) {
        this.context = context;
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        calendarXml = userDir + separator + "DesKal" + separator + "calendar.xml";
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            df = DatatypeFactory.newInstance();
            builder = docFactory.newDocumentBuilder();
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("Datatype factory init error", ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Document builder init error", ex);
        }
    }

    public void addEvent(Event e) {

        //create dateSince without time
        XMLGregorianCalendar dateFrom = df.newXMLGregorianCalendar();
        dateFrom.setYear(e.getDate().getYear());
        dateFrom.setMonth(e.getDate().getMonth());
        dateFrom.setDay(e.getDate().getDay());
        String dateFromStr = dateFrom.toXMLFormat() + "Z";

        //getHour()/minute/second vraci jen int
        String hourStr = e.getDate().getHour() < 10 ? "0" + Integer.toString(e.getDate().getHour())
                : Integer.toString(e.getDate().getHour());
        String minuteStr = e.getDate().getMinute() < 10 ? "0" + Integer.toString(e.getDate().getMinute())
                : Integer.toString(e.getDate().getMinute());
        String timeFromStr = hourStr + ":" + minuteStr + ":00";

        Duration dur = e.getDuration();
        XMLGregorianCalendar dateTo = (XMLGregorianCalendar) e.getDate().clone();
        dateTo.add(dur);

        //create dateTo without time
        XMLGregorianCalendar dateToHelp = df.newXMLGregorianCalendar();
        dateToHelp.setYear(dateTo.getYear());
        dateToHelp.setMonth(dateTo.getMonth());
        dateToHelp.setDay(dateTo.getDay());
        String dateToStr = dateToHelp.toXMLFormat() + "Z";

        String hoursToStr = dateTo.getHour() < 10 ? "0" + Integer.toString(dateTo.getHour())
                : Integer.toString(dateTo.getHour());
        String minutesToStr = dateTo.getMinute() < 10 ? "0" + Integer.toString(dateTo.getMinute())
                : Integer.toString(dateTo.getMinute());
        String timeToStr = hoursToStr + ":" + minutesToStr + ":00";

        //some attributes can be null
        String place = e.getPlace() == null ? "" : e.getPlace();
        String note = e.getNote() == null ? "" : e.getNote();

        String tagNode = null;
        if(e.getTag() != null){
        String tag = "tagref='" + e.getTag() + "'";
        tagNode = "<tag " + tag + "/>";
        }
        
        String xquery =                
                 "insert node <event id=\"{generate-id()}\">"
                + "<dateSince>" + dateFromStr + "</dateSince>"
                + "<dateTo>" + dateToStr + "</dateTo>"
                + "<timeFrom>" + timeFromStr + "</timeFrom>"
                + "<timeTo>" + timeToStr + "</timeTo>"
                + "<title>" + e.getName() + "</title>"
                + "<place>" + place + "</place>"
                + "<note>" + note + "</note>"
                + (tagNode == null ? "" : tagNode)
                + "</event> "
                + "as last into /calendar";

        XQuery xQuery = new XQuery(xquery);
        try {
            xQuery.execute(context);
        } catch (BaseXException ex) {
            Logger.getLogger(EventManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Err while adding node" + ex.getCause());
        }
        try {
            Export.export(context, context.data);
        } catch (IOException ex) {
            Logger.getLogger(EventManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeEvent(Event e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * nefunguje se stringem
     * a neni ani potreba
     * 
    public Event getEventById(String id) {

        String queryForEvent = "<events> "
                + "{ "
                + "let $doc := doc('" + calendarXml + "') "
                + "return $doc//event[@id="
                + id
                + "] "
                + "} "
                + "</events>";

        Document doc = getDocumentFromQuery(queryForEvent);

        List<Event> eventsById = parseDocument(doc);

        return eventsById.get(0);
    }
     * 
     */

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
        sinceHelp.set(Calendar.YEAR, year);
        sinceHelp.set(Calendar.MONTH, month - 1);//XMLGregCal jine cislovani
        sinceHelp.set(Calendar.DAY_OF_MONTH, 1);

        int lastDay = sinceHelp.getActualMaximum(Calendar.DAY_OF_MONTH);

        XMLGregorianCalendar since = null;
        XMLGregorianCalendar to = null;

        since = df.newXMLGregorianCalendar(sinceHelp);
        since.setTime(0, 0, 0);
        to = df.newXMLGregorianCalendar();
        to.setYear(year);
        to.setMonth(month);
        to.setDay(lastDay);

        String sSince = since.toXMLFormat();
        String sTo = to.toXMLFormat();

        String queryForEvents2 = "<events> "
                + "{ "
                + "let $doc := doc('" + calendarXml + "') "
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

    public List<Event> getEventsForMonth(int year, int month, String tag) {

        GregorianCalendar sinceHelp = new GregorianCalendar();
        sinceHelp.set(Calendar.YEAR, year);
        sinceHelp.set(Calendar.MONTH, month - 1);//XMLGregCal jine cislovani
        sinceHelp.set(Calendar.DAY_OF_MONTH, 1);

        int lastDay = sinceHelp.getActualMaximum(Calendar.DAY_OF_MONTH);

        XMLGregorianCalendar since = null;
        XMLGregorianCalendar to = null;

        since = df.newXMLGregorianCalendar(sinceHelp);
        since.setTime(0, 0, 0);
        to = df.newXMLGregorianCalendar();
        to.setYear(year);
        to.setMonth(month);
        to.setDay(lastDay);

        String sSince = since.toXMLFormat();
        String sTo = to.toXMLFormat();

        String queryForEvents3 = "<events> "
                + "{ "
                + "let $doc := doc('" + calendarXml + "') "
                + "return $doc//event[ "
                + "(tag/@tagref='" + tag + "') and "
                + "((dateSince/text() >= '" + sSince + "' and dateTo/text() <= '" + sTo + "') " //cely event je mezi daty
                + " or (dateTo/text() >= '" + sSince + "' and dateTo/text() < '" + sTo + "') "
                + " or (dateSince/text() >= '" + sSince + "' and dateSince/text() <= '" + sTo + "') "
                + " or (dateSince/text() < '" + sSince + "' and dateTo/text() > '" + sTo + "')" //event zacina pred pocatecnim datem a konci po koncovym
                + " )] "
                + "} "
                + "</events>";



        Document doc = getDocumentFromQuery(queryForEvents3);

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

           // Integer id = Integer.parseInt(eventEl.getAttribute("id"));
            String id=eventEl.getAttribute("id");
            event.setId(id);

            NodeList n = eventEl.getElementsByTagName("dateSince");
            String dateSinceStr = n.item(0).getTextContent();
            XMLGregorianCalendar dateSince = null;
            dateSince = df.newXMLGregorianCalendar(dateSinceStr);
            event.setDate(dateSince);

            n = eventEl.getElementsByTagName("timeFrom");
            String[] timeFromStr = (n.item(0).getTextContent()).split(":");
            int hoursFrom = Integer.parseInt(timeFromStr[0]);
            int minutesFrom = Integer.parseInt(timeFromStr[1]);
            int secondsFrom = Integer.parseInt(timeFromStr[2]);
            event.getDate().setTime(hoursFrom, minutesFrom, secondsFrom);

            n = eventEl.getElementsByTagName("dateTo");
            String dateToStr = n.item(0).getTextContent();

            n = eventEl.getElementsByTagName("timeTo");
            String[] timeToStr = (n.item(0).getTextContent()).split(":");
            int hoursTo = Integer.parseInt(timeToStr[0]);
            int minutesTo = Integer.parseInt(timeToStr[1]);
            int secondsTo = Integer.parseInt(timeToStr[2]);

            XMLGregorianCalendar to = df.newXMLGregorianCalendar(dateToStr);
            to.setTime(hoursTo, minutesTo, secondsTo);
            long duration = to.toGregorianCalendar().getTime().getTime() - dateSince.toGregorianCalendar().getTime().getTime();
            Duration dur = df.newDuration(duration);

            event.setDuration(dur);

            n = eventEl.getElementsByTagName("title");
            event.setName(n.item(0).getTextContent());

            n = eventEl.getElementsByTagName("place");
            if (n.getLength() > 0) {
                event.setPlace(n.item(0).getTextContent());
            }

            n = eventEl.getElementsByTagName("note");
            if (n.getLength() > 0) {
                event.setNote(n.item(0).getTextContent());
            }

            n = eventEl.getElementsByTagName("tag");
            if (n.getLength() > 0) {
                Element tagElement = (Element) n.item(0);
                event.setTag(tagElement.getAttribute("tagref"));
            }
            events.add(event);
        }

        return events;
    }
}
