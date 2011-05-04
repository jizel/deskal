package cz.muni.fi.pb138.deskal.gui;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Drak
 */
public class EventListSelectionListener implements ListSelectionListener {

    private JButton button1;
    private JButton button2;
    private JList list;

    EventListSelectionListener(JButton editEventButton, JButton removeEventButton, JList eventsList) {
        button1 = editEventButton;
        button2 = removeEventButton;
        list = eventsList;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button1.setEnabled(false);
                        button2.setEnabled(false);
                    }
                });

            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button1.setEnabled(true);
                        button2.setEnabled(true);
                    }
                });
            }
        }
    }
}
