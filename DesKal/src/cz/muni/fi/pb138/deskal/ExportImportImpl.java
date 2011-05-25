package cz.muni.fi.pb138.deskal;

import java.io.File;
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
    private static final String styleFile = "stylesheet/iCal.xsl";
    private Transformer iCalTransformer;

    public ExportImportImpl() {
        TransformerFactory factory = TransformerFactory.newInstance();

        Templates iCalExport;
        try {
            iCalExport = factory.newTemplates(new StreamSource(new File(styleFile)));
            iCalTransformer = iCalExport.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Error while building iCal transformer", ex);
        }
}

    public void importFromHCal(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void importFromICal(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exportToHCal(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void exportToICal(File file) {
        try {
            iCalTransformer.transform(new StreamSource(new File(calendarXml)), new StreamResult(file));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to iCal", ex);
        }
    }
}
