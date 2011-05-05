package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.Filter;
import java.util.List;
import javax.swing.JComboBox;

/**
 *
 * @author Drak
 */
public class FiltersDialog extends javax.swing.JDialog {
    private FiltersListModel model;
    private FiltersComboBoxModel comboModel;
    /** Creates new form FiltersDialog */
    public FiltersDialog(java.awt.Frame parent, boolean modal, List<Filter> filters, JComboBox filtersComboBox) {
        super(parent, modal);
        initComponents();
        model = (FiltersListModel) filtersList.getModel();
        newFilterButton.requestFocus();
        model.setFilters(filters);
        comboModel = (FiltersComboBoxModel) filtersComboBox.getModel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        filtersList = new javax.swing.JList();
        newFilterButton = new javax.swing.JButton();
        removeFilterButton = new javax.swing.JButton();
        closeFiltersButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DesKal");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusCycleRoot(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Filters"));

        filtersList.setModel(new FiltersListModel());
        filtersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(filtersList);
        filtersList.addListSelectionListener(new FiltersListSelectionListener(removeFilterButton,filtersList));

        newFilterButton.setText("New");
        newFilterButton.setFocusCycleRoot(true);
        newFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFilterButtonActionPerformed(evt);
            }
        });

        removeFilterButton.setText("Remove");
        removeFilterButton.setEnabled(false);
        removeFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFilterButtonActionPerformed(evt);
            }
        });

        closeFiltersButton.setText("Close");
        closeFiltersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFiltersButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(removeFilterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newFilterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeFiltersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(newFilterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(removeFilterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeFiltersButton)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFilterButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new NewFilterDialog(null, true, filtersList).setVisible(true);
            }
        });
}//GEN-LAST:event_newFilterButtonActionPerformed

    private void closeFiltersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFiltersButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                comboModel.update();
                dispose();
            }
        });
}//GEN-LAST:event_closeFiltersButtonActionPerformed

    private void removeFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFilterButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                model.remove(filtersList.getSelectedIndex());
                filtersList.clearSelection();
            }
        });
    }//GEN-LAST:event_removeFilterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeFiltersButton;
    private javax.swing.JList filtersList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newFilterButton;
    private javax.swing.JButton removeFilterButton;
    // End of variables declaration//GEN-END:variables
}
