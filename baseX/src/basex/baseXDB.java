package baseX;

import basex.Event;
import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.basex.core.cmd.*;
import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class baseXDB{
    private static org.basex.core.Context context;
    String DBfile;

    public  void ConnectToBaseX(String file) throws BaseXException{
        DBfile = file;
        org.basex.core.Context con = new org.basex.core.Context();
        new CreateDB("DBCalendar", file).execute(con);
        context = con;
    }

    public void insert() throws BaseXException, IOException{

        String xquery = "insert node <label name='purple'></label> into /calendar/labels";
        String res = new XQuery(xquery).execute(context);
        System.out.println(res);

        String queryForLabels = "<labels> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "return $doc"
                + "} "
                + "</labels>";

        System.out.println(new XQuery(queryForLabels).execute(context));
        Export.export(context, context.data);
        
    }

    public ArrayList<String> ReturnLabels() throws BaseXException, ParserConfigurationException, SAXException, IOException{
        ArrayList<String> labels = new ArrayList<String>();

        String queryForLabels = "<labels> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "for $x in $doc//label "
                + "return <label> { $x/@name/string() } </label>"
                + "} "
                + "</labels>";

        String labelsParseXML = new XQuery(queryForLabels).execute(context);

        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource iS = new InputSource();
        iS.setCharacterStream(new StringReader(labelsParseXML));
        doc = builder.parse(iS);

        NodeList nodes = doc.getElementsByTagName("label");

        for(int i = 0; i < nodes.getLength(); i++){
            labels.add(nodes.item(i).getTextContent());
        }

        return labels;
    }
/*
    
    public basex.Event GetEventByID(int id) throws BaseXException, DatatypeConfigurationException, ParserConfigurationException, SAXException, IOException{
        String queryForEvents = "<events> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "return $doc//event[@id="
                + id
                + "] "
                + "} "
                + "</events>";
        String eventsParseXML = new XQuery(queryForEvents).execute(context);

        InputSource iSS = new InputSource();
        iSS.setCharacterStream(new StringReader(eventsParseXML));
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(iSS);

        NodeList nodesEventById = doc.getElementsByTagName("event");

        Event eventById = null;

        if(nodesEventById.getLength()==1){
            eventById = new Event();
            Node node = nodesEventById.item(0);
            NodeList childNodes = node.getChildNodes();

            String date = "";
            NodeList tags = doc.getElementsByTagName("");
            String durationS = "";

            DatatypeFactory df = DatatypeFactory.newInstance();
            eventById.setId(Integer.parseInt(node.getAttributes().item(0).getTextContent()));
            for(int i = 0; i < childNodes.getLength(); i++){
                if(childNodes.item(i).getNodeName().compareTo("title")==0){
                    eventById.setName(childNodes.item(i).getTextContent());
                }
                if(childNodes.item(i).getNodeName().compareTo("place")==0){
                    eventById.setPlace(childNodes.item(i).getTextContent());
                }
                if(childNodes.item(i).getNodeName().compareTo("note")==0){
                    eventById.setNote(childNodes.item(i).getTextContent());
                }
                if(childNodes.item(i).getNodeName().compareTo("date")==0){
                    date = childNodes.item(i).getTextContent();
                }
                if(childNodes.item(i).getNodeName().compareTo("tags")==0){
                    tags = childNodes.item(i).getChildNodes();
                }
                if(childNodes.item(i).getNodeName().compareTo("duration")==0){
                    durationS = childNodes.item(i).getTextContent();
                }
            }
            XMLGregorianCalendar datetime = df.newXMLGregorianCalendar(date);
            Duration duration = df.newDuration(durationS);

            eventById.setDate(datetime);
            eventById.setDuration(duration);

            List<String> tagList = new ArrayList<String>();

            System.out.println(tags.getLength());
            for(int i = 0; i < tags.getLength(); i++){               
                if(tags.item(i).getNodeName().compareTo("tag")==0){
                    tagList.add(tags.item(i).getAttributes().item(0).getTextContent());
                }
            }
            if(tagList.size()>0)
                eventById.setTag(tagList.get(0));          
        }
        return eventById;

    }
*/
    public ArrayList<basex.Day> GetEventsByInterval(XMLGregorianCalendar since, XMLGregorianCalendar to) throws BaseXException, ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException, ParseException{
        ArrayList<basex.Day> month = new ArrayList<basex.Day>();

        //System.out.println(since.toXMLFormat());
        //System.out.println(to.toXMLFormat());
        /*
         * nefunguje omg
         */
        String sSince = since.toString().substring(0, 10) + "Z";
        String sTo = to.toString().substring(0, 10) + "Z";
        //System.out.println(sSince);
        /*
         * upravit: zmenit schema na odDatum doDatum, odCas, doCas a podle datumu filtrovat
         */

        //System.out.println(sTo);
        //System.out.println(sSince);

        String queryForEvents2 = "<events> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "return $doc//event[ "
                + "(dateSince/text() >= '" + sSince + "' and dateTo/text() <= '" + sTo + "') "  //cely event je mezi daty
                + " or (dateTo/text() >= '" + sSince + "' and dateTo/text() < '" + sTo + "') "
                + " or (dateSince/text() >= '" + sSince + "' and dateSince/text() <= '" + sTo + "') "
                + " or (dateSince/text() < '" + sSince + "' and dateTo/text() > '" + sTo + "')" //event zacina pred pocatecnim datem a konci po koncovym
                + " ] "
                + "} "
                + "</events>";
        //System.out.println(queryForEvents2);
        String eventsParseXML = new XQuery(queryForEvents2).execute(context);
        //System.out.println(eventsParseXML);

        InputSource iSS = new InputSource();
        iSS.setCharacterStream(new StringReader(eventsParseXML));
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(iSS);



        NodeList nodesEvent = doc.getElementsByTagName("event");

        DatatypeFactory df = DatatypeFactory.newInstance();
        ArrayList<basex.Day> daysInterval = new ArrayList<basex.Day>();
        XMLGregorianCalendar tmp = since;
        Duration day = df.newDurationDayTime(true, 1, 0, 0, 0);
        int daysBetween = 1;
        int j = 0;
        if(to.toString().compareTo(since.toString())>0){
            while(to.toString().compareTo(tmp.toString())>0){
                daysBetween++;
                daysInterval.add(new basex.Day());
                daysInterval.get(j++).setDate(df.newXMLGregorianCalendarDate(tmp.getYear(), tmp.getMonth(), tmp.getDay(), 0));
                tmp.add(day);
            }
            daysInterval.add(new basex.Day());
            daysInterval.get(j++).setDate(df.newXMLGregorianCalendarDate(tmp.getYear(), tmp.getMonth(), tmp.getDay(), 0));
        }

        /*
         * Zpracování xml do eventu
         */
        Event event;
        NodeList tags;
        String dateTo;
        String dateSince;
        String timeSince;
        String timeTo;


        for(int i = 0; i < nodesEvent.getLength();i++){
            event = new Event();
            Node node = nodesEvent.item(i);
            NodeList childNodes = node.getChildNodes();

            dateTo = "";
            timeSince = "";
            timeTo = "";
            dateSince = "";
            tags = doc.getElementsByTagName("");


            event.setId(Integer.parseInt(node.getAttributes().item(0).getTextContent()));
            for(int k = 0; k < childNodes.getLength(); k++){
                if(childNodes.item(k).getNodeName().compareTo("title")==0){
                    event.setName(childNodes.item(k).getTextContent());
                }
                if(childNodes.item(k).getNodeName().compareTo("place")==0){
                    event.setPlace(childNodes.item(k).getTextContent());
                }
                if(childNodes.item(k).getNodeName().compareTo("note")==0){
                    event.setNote(childNodes.item(k).getTextContent());
                }
                if(childNodes.item(k).getNodeName().compareTo("dateTo")==0){
                    dateTo = childNodes.item(k).getTextContent();
                }
                if(childNodes.item(k).getNodeName().compareTo("dateSince")==0){
                    dateSince = childNodes.item(k).getTextContent();
                }
                if(childNodes.item(k).getNodeName().compareTo("timeTo")==0){
                    timeTo = childNodes.item(k).getTextContent();
                }
                if(childNodes.item(k).getNodeName().compareTo("timeFrom")==0){
                    timeSince = childNodes.item(k).getTextContent();
                }
                if(childNodes.item(k).getNodeName().compareTo("tags")==0){
                    tags = childNodes.item(k).getChildNodes();
                }
                
            }
            XMLGregorianCalendar dateSinceXML = df.newXMLGregorianCalendar(dateSince);
            

            
            //pokus o prevodu casu ve stringu na objekt Time
            

            event.setDate(dateSinceXML);
            XMLGregorianCalendar dateToXML = df.newXMLGregorianCalendar(dateTo);
            event.setDateTo(dateToXML);

            
            /*
             * spocitat duration podle dat a casu a vlozit
             */

            List<String> tagList = new ArrayList<String>();

            //System.out.println(tags.getLength());
            for(int n = 0; n < tags.getLength(); n++){
                if(tags.item(n).getNodeName().compareTo("tag")==0){
                    tagList.add(tags.item(n).getAttributes().item(0).getTextContent());
                }
            }
            if(tagList.size()>0)
                event.setTag(tagList.get(0));

            //System.out.println(event.toStringAll());
            
            /*
             * nafrkani eventu do dnu
             * 
             */
            for(int p=0;p<daysInterval.size();p++){
                if(((daysInterval.get(p).getDate().compare(dateSinceXML) == DatatypeConstants.GREATER) || (daysInterval.get(p).getDate().compare(dateSinceXML) == DatatypeConstants.EQUAL)) &&
                        (daysInterval.get(p).getDate().compare(dateToXML) == DatatypeConstants.EQUAL || daysInterval.get(p).getDate().compare(dateToXML) == DatatypeConstants.LESSER)){
                    daysInterval.get(p).addEvent(event);
                }       
            }
       }

       //Vypsani eventu po dnech
        for(int p=0;p<daysInterval.size();p++){
            System.out.println(daysInterval.get(p).getDate().toString());
            for(int o=0; o < daysInterval.get(p).getEvents().size(); o++){
                System.out.println(daysInterval.get(p).getEvents().get(o).toStringAll());
            }
        }
      
        return daysInterval;
    }


    private static final String sourceFile = "calendar.xml";

    private static final String styleFile = "iCal.xsl";

    private static final String destFile = "iCal.txt";

    public void exportICAL() throws TransformerConfigurationException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        Templates templates = factory.newTemplates(new StreamSource(new File(styleFile)));

        Transformer transformer = templates.newTransformer();


        transformer.transform(
                new StreamSource(new File(sourceFile)),
                new StreamResult(new File(destFile)));
    }

}