package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Event;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.datatype.Duration;

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
    private MessageFormat dateCzFormat;

    EventListSelectionListener(JButton editEventButton, JButton removeEventButton, JList eventsList, List<JLabel> labels) {
        button1 = editEventButton;
        button2 = removeEventButton;
        list = eventsList;
        model = (EventListModel) list.getModel();
        this.labels = labels;

        Locale czech = new Locale("cs", "CZ");
        String sablonaTextu = "{0,choice,0#|1#{0} rok|2#{0} roky|3#{0} roky|4#{0} roky|4<{0} let}"
                + "  {1,choice,0#|1#{1} měsíc|2#{1} měsíce|3#{1} měsíce|4#{1} měsíce|4<{1} měsíců}"
                + "  {2,choice,0#|1#{2} den|2#{2} dny|3#{2} dny|4#{2} dny|4<{2} dní}"
                + "  {3,choice,0#|1#{3} hodina|2#{3} hodiny|3#{3} hodiny|4#{3} hodiny|4<{3} hodin}"
                + "  {4,choice,0#|1#{4} minuta|2#{4} minuty|3#{4} minuty|4#{4} minuty|4<{4} minut}";
        dateCzFormat = new MessageFormat(sablonaTextu, czech);
    }

    public String getDurationString(Event event) {
        Duration duration = event.getDuration();
        int years = duration.getYears();
        int months = duration.getMonths();
        int days = duration.getDays();
        int hours = duration.getHours();
        int minutes = duration.getMinutes();
        return dateCzFormat.format(new Object[]{years, months, days, hours, minutes});
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button1.setEnabled(false);
                        button2.setEnabled(false);
                        for (int i = 0; i < labels.size(); i++) {
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
                        labels.get(0).setText(event.getName());
                        labels.get(1).setText(event.getPlace());
                        labels.get(2).setText(event.getStartString());
                        labels.get(3).setText(getDurationString(event));
                        labels.get(4).setText(event.getTag());
                        labels.get(5).setText(event.getNote());
                    }
                });
            }
        }
    }
}
