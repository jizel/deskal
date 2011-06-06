package cz.muni.fi.pb138.deskal;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;

/**
 *
 * @author Drak
 */
public class CalendarDB {

    private static org.basex.core.Context context;
    private String DBfile;

    public CalendarDB() {
        String userDir = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        DBfile = userDir + separator + "DesKal" + separator + "calendar.xml";
    }

    //vytvoreni kontextu databaze - soubor calendar.xml
    public void ConnectToBaseX() {
        Context con = new org.basex.core.Context();
        try {
            new CreateDB("DBCalendar", DBfile).execute(con);
        } catch (BaseXException ex) {
            throw new RuntimeException("Database connecting error", ex);
        }
        context = con;
    }
    //ziskani kontextu
    public Context getContext() {
        if(context == null)
            ConnectToBaseX();
        return context;
    }
    //ukonceni databaze
    public void closeDB(){
        context.close();
    }
}
