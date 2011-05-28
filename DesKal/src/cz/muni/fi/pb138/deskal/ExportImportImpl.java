package cz.muni.fi.pb138.deskal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;

public class ExportImportImpl implements ExportImport {

    private Transformer iCalTransformer;
    private Transformer hCalTransformer;
    private CalendarBuilder iCalBuilder;
    private File calendarXml;
    private DatatypeFactory df;
    private EventManager evtManager;
    private CalendarManager calManager;

    public ExportImportImpl(EventManager evtManager, CalendarManager calManager) {
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        calendarXml = new File(userDir + separator + "DesKal" + separator + "calendar.xml");

        this.evtManager = evtManager;
        this.calManager = calManager;
        TransformerFactory factory = TransformerFactory.newInstance();

        InputStream iCalStyleStream = ClassLoader.getSystemResourceAsStream("iCal.xsl");
        InputStream hCalStyleStream = ClassLoader.getSystemResourceAsStream("hCal.xsl");

        Templates iCalExport;
        Templates hCalExport;

        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        iCalBuilder = new CalendarBuilder();

        try {
            iCalExport = factory.newTemplates(new StreamSource(iCalStyleStream));
            hCalExport = factory.newTemplates(new StreamSource(hCalStyleStream));
            iCalTransformer = iCalExport.newTransformer();
            hCalTransformer = hCalExport.newTransformer();
            df = DatatypeFactory.newInstance();

        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Error while building transformers", ex);
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("Error while building datatype factory", ex);
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
        Reader fileReader = null;
        Calendar calendar = null;
        try {
            fileReader = new FileReader(file);
            calendar = iCalBuilder.build(fileReader);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Chyba pri importu - soubor nebyl nalezen",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Chyba pri importu - vstup/vystup",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (ParserException ex) {
            JOptionPane.showMessageDialog(null, "Chyba pri importu - spatny format souboru",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                throw new RuntimeException("Error while closing input file", ex);
            }
        }
        List<Event> events = new ArrayList<Event>();
        if (calendar != null) {

            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component) i.next();

                if (component.getName().equals(Component.VEVENT)) {
                    Event event = new Event();
                    Property name1 = component.getProperty(Property.NAME);
                    Property name2 = component.getProperty(Property.SUMMARY);
                    Property place = component.getProperty(Property.LOCATION);
                    Property note = component.getProperty(Property.DESCRIPTION);
                    Property tag = component.getProperty(Property.CATEGORIES);
                    Property start = component.getProperty(Property.DTSTART);
                    Property end = component.getProperty(Property.DTEND);
                    if (name2 != null) {
                        event.setName(name2.getValue());
                    }
                    if (name1 != null) {
                        event.setName(name1.getValue());
                    }
                    if (place != null) {
                        event.setPlace(place.getValue());
                    }
                    if (note != null) {
                        event.setNote(note.getValue());
                    }
                    if (tag != null) {
                        event.setTag(tag.getValue());
                    }
                    if (event.getName() == null) {
                        event.setName("Bez názvu");                        
                    }
                    String dateString = start.getValue();
                    String startDate = dateString.substring(0, 4) + "-" + dateString.substring(4, 6)
                            + "-" + dateString.substring(6, 8) + "Z";
                    String[] dtstart = dateString.split("T");
                    event.setDate(df.newXMLGregorianCalendar(startDate));
                    int hour = Integer.parseInt(dtstart[1].substring(0, 2));
                    int minute = Integer.parseInt(dtstart[1].substring(2, 4));
                    int second = Integer.parseInt(dtstart[1].substring(4, 6));

                    event.getDate().setTime(hour, minute, second);

                    dateString = end.getValue();
                    String endDate = dateString.substring(0, 4) + "-" + dateString.substring(4, 6)
                            + "-" + dateString.substring(6, 8) + "Z";
                    String[] dtend = dateString.split("T");
                    XMLGregorianCalendar endDay = df.newXMLGregorianCalendar(endDate);
                    hour = Integer.parseInt(dtend[1].substring(0, 2));
                    minute = Integer.parseInt(dtend[1].substring(2, 4));
                    second = Integer.parseInt(dtend[1].substring(4, 6));
                    endDay.setTime(hour, minute, second);

                    long dur = endDay.toGregorianCalendar().getTime().getTime()
                            - event.getDate().toGregorianCalendar().getTime().getTime();
                    event.setDuration(df.newDuration(dur));

                    events.add(event);
                }
            }
        }

        List<Event> dataEvents = evtManager.getAllEvents();
        List<String> tags = calManager.getAllTags();

        for (Event event : events) {
            if (!dataEvents.contains(event)) {
                evtManager.addEvent(event);
                String tag = event.getTag();
                if(!tag.equals("null") && !tags.contains(tag)){
                    tags.add(tag);
                    calManager.addFilter(new Filter(tag));
                }
            }
        }
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
