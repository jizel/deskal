package cz.muni.fi.pb138.deskal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
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
    private Transformer hCalTokensTransformer;
    private CalendarBuilder iCalBuilder;
    private File calendarXml;
    private DatatypeFactory df;
    private EventManager evtManager;
    private CalendarManager calManager;
    private String charset;
    private String userDir;
    private String separator;

    public ExportImportImpl(EventManager evtManager, CalendarManager calManager) {
        userDir = System.getProperty("user.home");
        separator = System.getProperty("file.separator");
        calendarXml = new File(userDir + separator + "DesKal" + separator + "calendar.xml");
        InputStream hCalStyleStream = null;
        if (isWindows()) {
            charset = "windows-1250";
            hCalStyleStream = ClassLoader.getSystemResourceAsStream("hCalWin.xsl");
        } else if (isMac()) {
            charset = "MacCentralEurope";
            hCalStyleStream = ClassLoader.getSystemResourceAsStream("hCalMac.xsl");
        } else if (isUnix()) {
            charset = "utf-8";
            hCalStyleStream = ClassLoader.getSystemResourceAsStream("hCal.xsl");
        } else {
            charset = "utf-8";
            hCalStyleStream = ClassLoader.getSystemResourceAsStream("hCal.xsl");
        }

        this.evtManager = evtManager;
        this.calManager = calManager;
        TransformerFactory factory = TransformerFactory.newInstance();

        InputStream iCalStyleStream = ClassLoader.getSystemResourceAsStream("iCal.xsl");

        InputStream hCalTokensStyleStream = ClassLoader.getSystemResourceAsStream("hCalGetTokens.xsl");

        Templates iCalExport;
        Templates hCalExport;
        Templates hCalTokens;

        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
        iCalBuilder = new CalendarBuilder();

        try {
            iCalExport = factory.newTemplates(new StreamSource(iCalStyleStream));
            hCalExport = factory.newTemplates(new StreamSource(hCalStyleStream));
            hCalTokens = factory.newTemplates(new StreamSource(hCalTokensStyleStream));
            iCalTransformer = iCalExport.newTransformer();
            hCalTransformer = hCalExport.newTransformer();
            hCalTokensTransformer = hCalTokens.newTransformer();

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
            if (hCalTokensStyleStream != null) {
                try {
                    hCalTokensStyleStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error while closing input stream", ex);
                }
            }
        }
    }

    public void importFromHCal(File file) {
        List<Event> events = new ArrayList<Event>();
        InputStreamReader stream = null;
        BufferedReader reader = null;
        File tmp = null;
        try {
            tmp = new File(userDir + separator + "hCalImp");
            stream = new InputStreamReader(new FileInputStream(file), charset);
            hCalTokensTransformer.transform(new StreamSource(stream), new StreamResult(
                    new OutputStreamWriter(new FileOutputStream(tmp))));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(tmp)));
            String line = reader.readLine();
            while (line != null) {
                if (!line.equals("---")) {
                    Event event = new Event();
                    String[] ev = line.split(";");
                    XMLGregorianCalendar dtStart = null;
                    XMLGregorianCalendar dtEnd = null;
                    for (int i = 1; i < ev.length; i++) {
                        if (ev[i].equals("SUMMARY")) {
                            event.setName(ev[i + 1]);
                        } else if (ev[i].equals("DESCRIPTION")) {
                            event.setNote(ev[i + 1]);
                        } else if (ev[i].equals("LOCATION")) {
                            event.setPlace(ev[i + 1]);
                        } else if (ev[i].equals("CATEGORIES")) {
                            event.setTag(ev[i + 1]);
                        } else if (ev[i].equals("DTSTART")) {
                            dtStart = df.newXMLGregorianCalendar(ev[i + 1]);
                            dtStart.setTimezone(0);
                            event.setDate(dtStart);
                        } else if (ev[i].equals("DTEND")) {
                            dtEnd = df.newXMLGregorianCalendar(ev[i + 1]);
                            dtEnd.setTimezone(0);
                        }
                    }
                    long duration = dtEnd.toGregorianCalendar().getTime().getTime() - dtStart.toGregorianCalendar().getTime().getTime();
                    Duration dur = df.newDuration(duration);
                    event.setDuration(dur);

                    events.add(event);
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - soubor nebyl nalezen",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - vstup/výstup",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - špatný formát souboru",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error during closing temp file", ex);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    throw new RuntimeException("Error during closing temp file", ex);
                }
            }
            if (tmp != null) {
                tmp.delete();
            }
        }

        List<Event> dataEvents = evtManager.getAllEvents();
        List<String> tags = calManager.getAllTags();

        for (Event event : events) {
            if (!dataEvents.contains(event)) {
                evtManager.addEvent(event);
                String tag = event.getTag();
                if (tag != null && !tag.equals("null") && !tags.contains(tag)) {
                    tags.add(tag);
                    calManager.addFilter(new Filter(tag));
                }
            }
        }
    }

    public void importFromICal(File file) {
        Calendar calendar = null;
        InputStreamReader stream = null;
        try {
            stream = new InputStreamReader(new FileInputStream(file), charset);
            calendar = iCalBuilder.build(stream);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - soubor nebyl nalezen",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - vstup/výstup",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } catch (ParserException ex) {
            JOptionPane.showMessageDialog(null, "Chyba při importu - špatný format souboru",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                stream.close();
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
                    Property name = component.getProperty(Property.SUMMARY);
                    Property place = component.getProperty(Property.LOCATION);
                    Property note = component.getProperty(Property.DESCRIPTION);
                    Property tag = component.getProperty(Property.CATEGORIES);
                    Property start = component.getProperty(Property.DTSTART);
                    Property end = component.getProperty(Property.DTEND);
                    if (name != null) {
                        event.setName(name.getValue());
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
                if (tag != null && !tag.equals("null") && !tags.contains(tag)) {
                    tags.add(tag);
                    calManager.addFilter(new Filter(tag));
                }
            }
        }
    }

    public void exportToHCal(File file) {
        OutputStreamWriter stream = null;
        try {
            stream = new OutputStreamWriter(new FileOutputStream(file), charset);
            hCalTransformer.transform(new StreamSource(calendarXml),
                    new StreamResult(stream));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to hCal", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error during transformation to hCal", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException("Error while closing output stream", ex);
            }
        }
    }

    public void exportToICal(File file) {
        OutputStreamWriter stream = null;
        try {
            stream = new OutputStreamWriter(new FileOutputStream(file), charset);
            iCalTransformer.transform(new StreamSource(calendarXml),
                    new StreamResult(stream));
        } catch (TransformerException ex) {
            throw new RuntimeException("Error during transformation to iCal", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error during transformation to iCal", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException("Error while closing output stream", ex);
            }
        }
    }

    //help methods
    private boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf("win") >= 0);

    }

    private boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf("mac") >= 0);

    }

    private boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }
}
