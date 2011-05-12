package basex;


import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import org.basex.core.BaseXException;
import org.xml.sax.SAXException;

public class baseXParse {
    


    public static void main(String[] args) throws BaseXException, ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException {

        baseX.baseXDB DB = new baseX.baseXDB();

        DB.ConnectToBaseX("calendar.xml");
        System.out.println(DB.ReturnLabels().toString());

      //  Event event = DB.GetEventByID(2);
        /*if(event!=null)
            System.out.println(event.toStringAll());
*/

        XMLGregorianCalendar since;
        XMLGregorianCalendar to;
        DatatypeFactory df = DatatypeFactory.newInstance();
        since = df.newXMLGregorianCalendarDate(2011, 05, 8, 1);
        to = df.newXMLGregorianCalendarDate(2011, 05, 15, 0);

        DB.GetEventsByInterval(since, to);


        //labels----------------------------------------------------------------
        /*String queryForLabels = "<labels> "
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

        ArrayList<String> labels = new ArrayList<String>();
        for(int i = 0; i < nodes.getLength(); i++){
            labels.add(nodes.item(i).getTextContent());
        }
        System.out.println(labels.toString());

        //eventID----------------------------------------------------------------
        int id = 2;
        String queryForEvents = "<events> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "return $doc//event[@id="
                + id
                + "] "
                + "} "
                + "</events>";
        String eventsParseXML = new XQuery(queryForEvents).execute(context);
        //System.out.println(eventsParseXML);

        InputSource iSS = new InputSource();
        iSS.setCharacterStream(new StringReader(eventsParseXML));
        doc = builder.parse(iSS);

        NodeList nodesEventById = doc.getElementsByTagName("event");
        if(nodesEventById.getLength()==1){
            Node node = nodesEventById.item(0);
            NodeList childNodes = node.getChildNodes();

            String since;
            String date = "";
            NodeList tags = doc.getElementsByTagName("");
            String durationS = "";

            DatatypeFactory df = DatatypeFactory.newInstance();
            Event eventById = new Event(Integer.parseInt(node.getAttributes().item(0).getTextContent()));
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
                System.out.println(tags.item(i).getNodeName());
                if(tags.item(i).getNodeName().compareTo("tag")==0){
                    tagList.add(tags.item(i).getAttributes().item(0).getTextContent());
                }
            }
            if(tagList.size()>0)
                eventById.setTag(tagList.get(0));
            System.out.println(tagList.toString());

            System.out.println(eventById.toStringAll());
            
            
            //Event evenById = new Event(nodesEventById.item(0).ge, null, null)*/
        }



    }

    

