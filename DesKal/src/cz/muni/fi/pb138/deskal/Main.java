package cz.muni.fi.pb138.deskal;

import cz.muni.fi.pb138.deskal.gui.MainFrame;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import javax.swing.JFrame;

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
                JFrame frame = new MainFrame();
                Toolkit tk = Toolkit.getDefaultToolkit();
                Dimension screenSize = tk.getScreenSize();
                int x = (int) screenSize.getWidth();
                int y = (int) screenSize.getHeight();
                frame.setLocation(x/4,y/10);
                frame.setVisible(true);
            }
        });
    }
}
