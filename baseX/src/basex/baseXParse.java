package basex;


import java.io.IOException;
import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.basex.core.BaseXException;
import org.xml.sax.SAXException;

public class baseXParse {
    


    public static void main(String[] args) throws BaseXException, ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException, ParseException, TransformerConfigurationException, TransformerException {

        baseX.baseXDB DB = new baseX.baseXDB();

        DB.ConnectToBaseX("calendar.xml");

        DB.exportICAL();
      
        }



    }

    

