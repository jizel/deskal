package cz.muni.fi.pb138.deskal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
    private String DBfile;

    public CalendarManagerImpl(Context context){
        this.context = context;
    }

    public List<String> getAllTags() {
        List<String> labels = new ArrayList<String>();

        String queryForLabels = "<labels> "
                + "{ "
                + "let $doc := doc('calendar.xml') "
                + "for $x in $doc//label "
                + "return <label> { $x/@name/string() } </label>"
                + "} "
                + "</labels>";

        String labelsParseXML = null;
        try {
            labelsParseXML = new XQuery(queryForLabels).execute(context);
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

        NodeList nodes = doc.getElementsByTagName("label");

        for(int i = 0; i < nodes.getLength(); i++){
            labels.add(nodes.item(i).getTextContent());
        }

        return labels;
    }

    public List<Day> getDaysInMonth(int year, int month) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void runFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeFilter(Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
