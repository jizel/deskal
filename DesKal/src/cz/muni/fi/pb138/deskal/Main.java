package cz.muni.fi.pb138.deskal;

import cz.muni.fi.pb138.deskal.gui.MainFrame;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {

        //create DesKal/calendar.xml in user's directory if it doesn't exist
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");

        File desKalDir = new File(userDir + separator + "DesKal");
        if (!desKalDir.exists()) {
            desKalDir.mkdir();
        }
        File calendar = new File(userDir + separator + "DesKal" + separator + "calendar.xml");
        if (!calendar.exists()) {
            PrintWriter writer = null;
            try {
                calendar.createNewFile();
                writer = new PrintWriter(calendar);
                String s1 = "<?xml version='1.0' encoding='UTF-8'?>";
                String s2 = "<calendar xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='calendar-schema.xsd'>";
                String s3 = "<labels>";
                String s4 = "</labels>";
                String s5 = "</calendar>";
                writer.println(s1);
                writer.println(s2);
                writer.println(s3);
                writer.println(s4);
                writer.println(s5);
                if (writer.checkError()) {
                    throw new RuntimeException("Error during writing into calendar.xml file");
                }
            } catch (IOException ex) {
                throw new RuntimeException("Can not create calendar.xml file in " + desKalDir.toString(), ex);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
        
        // Validate calendar.xml against calendar-schema.xsd
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaLocation = new File("calendar-schema.xsd");
        Schema schema = null;
        try {
            schema = factory.newSchema(schemaLocation);
        } catch (SAXException ex) {
            throw new RuntimeException("Schema error", ex);
        }
        Validator validator = schema.newValidator();
        Source source = new StreamSource(calendar);
        try {validator.validate(source);
        } //if invalid:
        catch (SAXException ex) {
            String newline = System.getProperty("line.separator");
            JOptionPane.showMessageDialog(null, calendar.toURI() + newline +
                    "is corrupted, please delete it", "Calendar.xml error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        //starting swing gui in message dispatcher thread
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new MainFrame();
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension screenSize = tk.getScreenSize();
                int x = (int) screenSize.getWidth();
                int y = (int) screenSize.getHeight();
                frame.setLocation(x / 4, y / 20);
                frame.setVisible(true);
            }
        });
    }
}
