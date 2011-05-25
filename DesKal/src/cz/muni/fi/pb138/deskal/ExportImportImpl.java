package cz.muni.fi.pb138.deskal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ExportImportImpl implements ExportImport {

    private String userDir = System.getProperty("user.home");
    private String separator = System.getProperty("file.separator");
    private String calendarXml = userDir + separator + "DesKal" + separator + "calendar.xml";
    private InputStream iCalStyleFile;
    private InputStream hCalStyleFile;
    private Transformer iCalTransformer;
    private Transformer hCalTransformer;

    public ExportImportImpl() {
        TransformerFactory factory = TransformerFactory.newInstance();

        iCalStyleFile = ClassLoader.getSystemResourceAsStream("iCal.xsl");
        hCalStyleFile = ClassLoader.getSystemResourceAsStream("hCal.xsl");

        Templates iCalExport;
        Templates hCalExport;

        try {
            iCalExport = factory.newTemplates(new StreamSource(iCalStyleFile));
            hCalExport = factory.newTemplates(new StreamSource(hCalStyleFile));
            iCalTransformer = iCalExport.newTransformer();
            hCalTransformer = hCalExport.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Error while building transformers", ex);
        } finally {
            if(iCalStyleFile != null)
                try {
                iCalStyleFile.close();
            } catch (IOException ex) {
               throw new RuntimeException("Error while closing input stream", ex);
            }
            if(hCalStyleFile != null)
                try {
                hCalStyleFile.close();
            } catch (IOException ex) {
               throw new RuntimeException("Error while closing input stream", ex);
            }
        }
    }

    public void importFromHCal(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void importFromICal(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exportToHCal(File file) {
        try {
            hCalTransformer.transform(new StreamSource(new File(calendarXml)), new StreamResult(file));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to hCal", ex);
        }
    }

    public void exportToICal(File file) {
        try {
            iCalTransformer.transform(new StreamSource(new File(calendarXml)), new StreamResult(file));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to iCal", ex);
        }
    }    
}
