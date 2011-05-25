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


    private Transformer iCalTransformer;
    private Transformer hCalTransformer;
    private File calendarXml;

    public ExportImportImpl() {
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        calendarXml = new File(userDir + separator + "DesKal" + separator + "calendar.xml");

        TransformerFactory factory = TransformerFactory.newInstance();
        
        InputStream iCalStyleStream = ClassLoader.getSystemResourceAsStream("iCal.xsl");
        InputStream hCalStyleStream= ClassLoader.getSystemResourceAsStream("hCal.xsl");

        Templates iCalExport;
        Templates hCalExport;

        try {
            iCalExport = factory.newTemplates(new StreamSource(iCalStyleStream));
            hCalExport = factory.newTemplates(new StreamSource(hCalStyleStream));
            iCalTransformer = iCalExport.newTransformer();
            hCalTransformer = hCalExport.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Error while building transformers", ex);
        } finally {
            if (iCalStyleStream != null) {
                try {
                    iCalStyleStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error while closing input stream", ex);
                }
            }
            if (hCalStyleStream != null) {
                try {
                    hCalStyleStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error while closing input stream", ex);
                }
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
            hCalTransformer.transform(new StreamSource(calendarXml), new StreamResult(file));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to hCal", ex);
        }
    }

    public void exportToICal(File file) {
        try {
            iCalTransformer.transform(new StreamSource(calendarXml), new StreamResult(file));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to iCal", ex);
        }
    }
}
