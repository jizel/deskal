package baseX;

import basex.Event;
import java.util.List;
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

public class baseXDB{
    private static org.basex.core.Context context;
    String DBfile;

    public  void ConnectToBaseX(String file) throws BaseXException{
        DBfile = file;
        org.basex.core.Context con = new org.basex.core.Context();
        new CreateDB("DBCalendar", file).execute(con);
        context = con;
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

        Event eventById = new Event();

        if(nodesEventById.getLength()==1){
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
    
}