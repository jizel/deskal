package cz.muni.fi.pb138.deskal.gui;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Drak
 */
public class FiltersListSelectionListener implements ListSelectionListener {

    private JButton button;
    private JList list;

    FiltersListSelectionListener(JButton removeFilterButton, JList filtersList) {
        button = removeFilterButton;
        list = filtersList;
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button.setEnabled(false);

                    }
                });

            } else {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        button.setEnabled(true);
                    }
                });
            }
        }
    }
}
