package cz.muni.fi.pb138.deskal.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Drak
 */
public class MonthsComboBoxModel extends DefaultComboBoxModel{
    private List<String> months = new ArrayList<String>();

    public MonthsComboBoxModel() {
        months.add("Leden");
        months.add("Únor");
        months.add("Březen");
        months.add("Duben");
        months.add("Květen");
        months.add("Červen");
        months.add("Červenec");
        months.add("Srpen");
        months.add("Září");
        months.add("Říjen");
        months.add("Listopad");
        months.add("Prosinec");
    }

    public Object getElementAt(int index) {
        return months.get(index);
    }

    @Override
    public int getSize(){
        return months.size();
    }
    
}
