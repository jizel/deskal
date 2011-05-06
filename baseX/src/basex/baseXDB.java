package baseX;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
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
import org.basex.core.Context;
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
}