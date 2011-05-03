package cz.muni.fi.pb138.deskal;

import cz.muni.fi.pb138.deskal.gui.*;
import java.awt.EventQueue;
/**
 *
 * @author Drak
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        //starting swing gui in message dispatcher thread
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

}