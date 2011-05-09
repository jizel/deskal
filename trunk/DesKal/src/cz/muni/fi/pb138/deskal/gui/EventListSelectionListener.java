package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Event;
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
    private List<JLabel> labels;
    private EventListModel model;
    private Event event;

    EventListSelectionListener(JButton editEventButton, JButton removeEventButton, JList eventsList, List<JLabel> labels) {
        button1 = editEventButton;
        button2 = removeEventButton;
        list = eventsList;
        model = (EventListModel) list.getModel();
        this.labels = labels;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button1.setEnabled(false);
                        button2.setEnabled(false);
                        for(int i = 0; i<labels.size(); i++){
                        labels.get(i).setText(" ");
                        }
                    }
                });

            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button1.setEnabled(true);
                        button2.setEnabled(true);
                        event = model.getEventAt(list.getSelectedIndex());
                        String place = event.getPlace() == null ? " " : event.getPlace();
                        String tag = event.getTag() == null ? " " : event.getTag();
                        String note = event.getNote()== null ? " " : event.getNote();
                        labels.get(0).setText(event.getName());
                        labels.get(1).setText(place);
                        labels.get(2).setText(event.getTimeString());
                        labels.get(3).setText(event.getDurationString());
                        labels.get(4).setText(tag);
                        labels.get(5).setText(note);
                    }
                });
            }
        }
    }
}
