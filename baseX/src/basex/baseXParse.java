package basex;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.basex.core.cmd.*;
import org.basex.core.BaseXException;
import org.basex.core.cmd.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class baseXParse {
    static org.basex.core.Context context = new org.basex.core.Context();

    public static void main(String[] args) throws BaseXException, ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException {
        new CreateDB("DBExample", "calendar.xml").execute(context);

        System.out.print(new InfoDB().execute(context));

        //labels----------------------------------------------------------------
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
            NodeList tags;
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
            

            System.out.println(eventById.toStringAll());
            
            
            //Event evenById = new Event(nodesEventById.item(0).ge, null, null)
        }

    }
}
