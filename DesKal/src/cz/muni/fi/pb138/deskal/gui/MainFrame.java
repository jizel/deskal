package cz.muni.fi.pb138.deskal.gui;

import cz.muni.fi.pb138.deskal.CalendarDB;
import cz.muni.fi.pb138.deskal.CalendarManager;
import cz.muni.fi.pb138.deskal.CalendarManagerImpl;
import cz.muni.fi.pb138.deskal.Day;
import cz.muni.fi.pb138.deskal.Event;
import cz.muni.fi.pb138.deskal.EventManager;
import cz.muni.fi.pb138.deskal.EventManagerImpl;
import cz.muni.fi.pb138.deskal.ExportImport;
import cz.muni.fi.pb138.deskal.ExportImportImpl;
import cz.muni.fi.pb138.deskal.Filter;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;
import org.basex.core.Context;

/**
 *
 * @author Drak
 */
public class MainFrame extends javax.swing.JFrame {

    private Calendar date = null;
    private DaysTableModel tableModel;
    private EventListModel listModel;
    private FiltersComboBoxModel comboModel;
    private List<Filter> filters = new ArrayList();
    private ManagerInitSwingWorker managerInitSwingWorker;
    private RefreshTableSwingWorker refreshTableSwingWorker;
    private RemoveEventWorker removeEventWorker;
    private RemoveAllEventsWorker removeAllEvents;
    private CalendarManager calManager;
    private EventManager evtManager;
    private ExportImport exportImport;
    private CalendarDB calendarDB;
    private ExportICalWorker exportICalWorker;
    private ExportHCalWorker exportHCalWorker;
    private ImportICalWorker importICalWorker;
    private ImportHCalWorker importHCalWorker;
    private RefreshFiltersAndTableWorker refreshFiltersAndTableWorker;

    // <editor-fold defaultstate="collapsed" desc="Managers initialization swing worker">
    private class ManagerInitSwingWorker extends SwingWorker<List<Day>, Void> {

        @Override
        protected List<Day> doInBackground() throws Exception {
            calendarDB = new CalendarDB();
            calendarDB.ConnectToBaseX();
            Context context = calendarDB.getContext();
            evtManager = new EventManagerImpl(context);
            calManager = new CalendarManagerImpl(context, evtManager);
            exportImport = new ExportImportImpl(evtManager, calManager);
            Filter none = new Filter("bez filtru"); //default filter
            filters.add(none);
            for (String name : calManager.getAllTags()) {
                filters.add(new Filter(name));
            }
            Calendar date = null;
            if (date == null) {
                date = GregorianCalendar.getInstance();
                date.setTimeInMillis(System.currentTimeMillis());
            }
            return calManager.getDaysInMonthWithTag(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH) + 1, null);
        }

        protected void done() {
            comboModel = (FiltersComboBoxModel) filtersComboBox.getModel();
            comboModel.setFilters(filters);
            filtersComboBox.setSelectedIndex(0);
            listModel = (EventListModel) eventsList.getModel();
            try {
                tableModel.setMonth(get());
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Table and list refresh swing worker">
    private class RefreshTableSwingWorker extends SwingWorker<List<Day>, Void> {

        private Calendar date;

        public RefreshTableSwingWorker(Calendar date) {
            this.date = date;
        }

        @Override
        protected List<Day> doInBackground() throws Exception {
            String tag = (String) comboModel.getElementAt(filtersComboBox.getSelectedIndex());
            List<Day> days = calManager.getDaysInMonthWithTag(
                    date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, tag);
            return days;
        }

        protected void done() {
            try {
                int row = daysTable.getSelectedRow();
                int column = daysTable.getSelectedColumn();
                int eventIndex = eventsList.getSelectedIndex();
                tableModel.setMonth(get());
                if (row != -1) {
                    daysTable.getSelectionModel().setSelectionInterval(row, row);
                }
                if (column != -1) {
                    daysTable.getColumnModel().getSelectionModel().setSelectionInterval(column, column);
                }
                loadEvents();
                eventsList.setSelectedIndex(eventIndex);
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Remove event swing worker">
    private class RemoveEventWorker extends SwingWorker<Void, Void> {

        private Event event;

        public RemoveEventWorker(Event event) {
            this.event = event;
        }

        @Override
        protected Void doInBackground() throws Exception {
            evtManager.removeEvent(event);
            refreshTableSwingWorker = new RefreshTableSwingWorker(date);
            refreshTableSwingWorker.execute();
            return null;
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Export to iCal swing worker">
    private class ExportICalWorker extends SwingWorker<Void, Void> {

        private String fileName;

        public ExportICalWorker(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected Void doInBackground() throws Exception {
            File file = new File(fileName + ".ics");
            exportImport.exportToICal(file);
            return null;
        }

        protected void done() {
            JOptionPane.showMessageDialog(rootPane, "Export dokončen",
                    "iCalendar", JOptionPane.INFORMATION_MESSAGE);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Export to hCal swing worker">
    private class ExportHCalWorker extends SwingWorker<Void, Void> {

        private String fileName;

        public ExportHCalWorker(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected Void doInBackground() throws Exception {
            File file = new File(fileName + ".xhtml");
            exportImport.exportToHCal(file);
            return null;
        }

        protected void done() {
            JOptionPane.showMessageDialog(rootPane, "Export dokončen",
                    "hCalendar", JOptionPane.INFORMATION_MESSAGE);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Import from iCal swing worker">
    private class ImportICalWorker extends SwingWorker<Void, Void> {

        private File file;

        public ImportICalWorker(File file) {
            this.file = file;
        }

        @Override
        protected Void doInBackground() throws Exception {
            exportImport.importFromICal(file);
            return null;
        }

        protected void done() {
            refreshFiltersAndTableWorker = new RefreshFiltersAndTableWorker();
            refreshFiltersAndTableWorker.execute();
            JOptionPane.showMessageDialog(rootPane, "Import dokončen",
                    "iCalendar", JOptionPane.INFORMATION_MESSAGE);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Import from iCal swing worker">
    private class ImportHCalWorker extends SwingWorker<Void, Void> {

        private File file;

        public ImportHCalWorker(File file) {
            this.file = file;
        }

        @Override
        protected Void doInBackground() throws Exception {
            exportImport.importFromHCal(file);
            return null;
        }

        protected void done() {
            refreshFiltersAndTableWorker = new RefreshFiltersAndTableWorker();
            refreshFiltersAndTableWorker.execute();
            JOptionPane.showMessageDialog(rootPane, "Import dokončen",
                    "iCalendar", JOptionPane.INFORMATION_MESSAGE);
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Refresh filters swing worker">
    private class RefreshFiltersAndTableWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            filters.clear();
            Filter none = new Filter("bez filtru"); //default filter
            filters.add(none);
            for (String name : calManager.getAllTags()) {
                filters.add(new Filter(name));
            }
            return null;
        }

        protected void done() {
            comboModel.setFilters(filters);
            refreshTableSwingWorker = new RefreshTableSwingWorker(date);
            refreshTableSwingWorker.execute();
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Remove all events swing worker">
    private class RemoveAllEventsWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            List<Event> events = evtManager.getAllEvents();
            for (Event event : events) {
                evtManager.removeEvent(event);
            }
            refreshTableSwingWorker = new RefreshTableSwingWorker(date);
            refreshTableSwingWorker.execute();
            return null;
        }
    }// </editor-fold>

    /** Creates new form MainFrame */
    public MainFrame() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("GUI: look and feel error", ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException("GUI: look and feel error", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("GUI: look and feel error", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException("GUI: look and feel error", ex);
        }
        initComponents();
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                if (calendarDB.getContext() != null) {
                    calendarDB.closeDB();
                }
                dispose();
                System.exit(0);
            }
        });
        tableModel = (DaysTableModel) daysTable.getModel();
        daysTable.setRowHeight((daysTable.getHeight() - 7) / daysTable.getRowCount());
        managerInitSwingWorker = new ManagerInitSwingWorker();
        managerInitSwingWorker.execute();

        Locale cz = new Locale("cs", "CZ");
        if (date == null) {
            date = GregorianCalendar.getInstance(cz);
            date.setTimeInMillis(System.currentTimeMillis());
        }
        String thisDay = Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + ". ";
        String thisMonth = getNameOfMonth(date.get(Calendar.MONTH)) + " ";
        String thisYear = Integer.toString(date.get(Calendar.YEAR));
        yearLabel.setText(thisYear);
        monthLabel.setText(thisMonth);
        thisMonth = getNameOfMonth2(date.get(Calendar.MONTH)) + " ";
        currentDateLabel.setText(thisDay + thisMonth + thisYear);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        daysTable =  new javax.swing.JTable(){TableCellRenderer daysRenderer = new DaysTableCellRenderer();
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (tableModel.isEventAt(row, column)) {
                    return daysRenderer;
                }
                // else...
                return super.getCellRenderer(row, column);
            }};
            prevMonthButton = new javax.swing.JButton();
            nextMonthButton = new javax.swing.JButton();
            jLabel3 = new javax.swing.JLabel();
            filtersComboBox = new javax.swing.JComboBox();
            jScrollPane2 = new javax.swing.JScrollPane();
            eventsList = new javax.swing.JList();
            jPanel2 = new javax.swing.JPanel();
            timeLabel = new javax.swing.JLabel();
            durationLabel = new javax.swing.JLabel();
            placeLabel = new javax.swing.JLabel();
            noteLabel = new javax.swing.JLabel();
            nameLabel = new javax.swing.JLabel();
            tagLabel = new javax.swing.JLabel();
            jLabel9 = new javax.swing.JLabel();
            jLabel8 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            jLabel7 = new javax.swing.JLabel();
            jLabel5 = new javax.swing.JLabel();
            jLabel6 = new javax.swing.JLabel();
            jPanel5 = new javax.swing.JPanel();
            currentDateLabel = new javax.swing.JLabel();
            jPanel6 = new javax.swing.JPanel();
            editEventButton = new javax.swing.JButton();
            newEventButton = new javax.swing.JButton();
            removeEventButton = new javax.swing.JButton();
            jPanel3 = new javax.swing.JPanel();
            monthLabel = new javax.swing.JLabel();
            yearLabel = new javax.swing.JLabel();
            MenuBar = new javax.swing.JMenuBar();
            FileMenu = new javax.swing.JMenu();
            filtersMenuItem = new javax.swing.JMenuItem();
            importMenu = new javax.swing.JMenu();
            iCalImportMenu = new javax.swing.JMenuItem();
            hCalImportMenu = new javax.swing.JMenuItem();
            exportMenu = new javax.swing.JMenu();
            iCalExportMenu = new javax.swing.JMenuItem();
            hCalExportMenu = new javax.swing.JMenuItem();
            ExitMenuItem = new javax.swing.JMenuItem();
            jMenu3 = new javax.swing.JMenu();
            goToMonthMenu = new javax.swing.JMenuItem();
            prevMonthMenu = new javax.swing.JMenuItem();
            nextMonthMenu = new javax.swing.JMenuItem();
            jMenu1 = new javax.swing.JMenu();
            newEventMenu = new javax.swing.JMenuItem();
            editEventMenu = new javax.swing.JMenuItem();
            removeEventMenu = new javax.swing.JMenuItem();
            removeAllEventsMenu = new javax.swing.JMenuItem();
            jMenu2 = new javax.swing.JMenu();
            jMenuItem5 = new javax.swing.JMenuItem();

            setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            setTitle("DesKal");
            setBackground(new java.awt.Color(153, 153, 153));
            setForeground(new java.awt.Color(153, 153, 153));
            setResizable(false);

            jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

            daysTable.setModel(new DaysTableModel());
            daysTable.setDefaultRenderer(Color.class, new DaysTableCellRenderer());
            daysTable.setCellSelectionEnabled(true);
            daysTable.setRowHeight(35);
            daysTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            daysTable.getTableHeader().setReorderingAllowed(false);
            daysTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    daysTableMouseReleased(evt);
                }
            });
            daysTable.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    daysTableKeyReleased(evt);
                }
            });
            jScrollPane1.setViewportView(daysTable);
            daysTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            daysTable.getTableHeader().setResizingAllowed(false);

            prevMonthButton.setForeground(new java.awt.Color(240, 240, 240));
            prevMonthButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/muni/fi/pb138/deskal/gui/larrowicon.gif"))); // NOI18N
            prevMonthButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    prevMonthButtonActionPerformed(evt);
                }
            });

            nextMonthButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/muni/fi/pb138/deskal/gui/rarrowicon.gif"))); // NOI18N
            nextMonthButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    nextMonthButtonActionPerformed(evt);
                }
            });

            jLabel3.setText("Filtr");

            filtersComboBox.setModel(new FiltersComboBoxModel());
            filtersComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filtersComboBoxActionPerformed(evt);
                }
            });

            eventsList.setModel(new EventListModel());
            eventsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            jScrollPane2.setViewportView(eventsList);
            List<JLabel> labels = new ArrayList<JLabel>();
            labels.add(nameLabel);
            labels.add(placeLabel);
            labels.add(timeLabel);
            labels.add(durationLabel);
            labels.add(tagLabel);
            labels.add(noteLabel);
            eventsList.addListSelectionListener(new EventListSelectionListener(editEventButton,removeEventButton,eventsList,labels,editEventMenu,removeEventMenu));

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Událost"));

            timeLabel.setText("  ");

            durationLabel.setText(" ");

            placeLabel.setText(" ");

            noteLabel.setText(" ");

            nameLabel.setText(" ");

            tagLabel.setText(" ");

            jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel9.setText("poznámka");

            jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel8.setText("štítek");

            jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel4.setText("název");

            jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel7.setText("délka");

            jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel5.setText("místo");

            jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel6.setText("začátek");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5))
                    .addGap(26, 26, 26)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(placeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(timeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(durationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(tagLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(noteLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nameLabel)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(placeLabel))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(timeLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(durationLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(tagLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(noteLabel))
                    .addContainerGap(22, Short.MAX_VALUE))
            );

            currentDateLabel.setFont(new java.awt.Font("Tahoma", 1, 22));
            currentDateLabel.setText("DNES");

            javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
            jPanel5.setLayout(jPanel5Layout);
            jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(currentDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(29, Short.MAX_VALUE))
            );
            jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(currentDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
            );

            editEventButton.setText("Upravit");
            editEventButton.setEnabled(false);
            editEventButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editEventButtonActionPerformed(evt);
                }
            });

            newEventButton.setText("Nová událost");
            newEventButton.setEnabled(false);
            newEventButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newEventButtonActionPerformed(evt);
                }
            });

            removeEventButton.setText("Odstranit");
            removeEventButton.setEnabled(false);
            removeEventButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    removeEventButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(newEventButton, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                        .addComponent(editEventButton, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                        .addComponent(removeEventButton, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addContainerGap())
            );
            jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addComponent(newEventButton)
                    .addGap(18, 18, 18)
                    .addComponent(editEventButton)
                    .addGap(18, 18, 18)
                    .addComponent(removeEventButton)
                    .addContainerGap(62, Short.MAX_VALUE))
            );

            monthLabel.setFont(new java.awt.Font("Tahoma", 0, 22));
            monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            monthLabel.setText("MESIC");

            yearLabel.setFont(new java.awt.Font("Tahoma", 0, 22));
            yearLabel.setText("ROK");

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(monthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(yearLabel)
                    .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(monthLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(yearLabel, javax.swing.GroupLayout.Alignment.LEADING))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addComponent(filtersComboBox, 0, 183, Short.MAX_VALUE))
                            .addGap(36, 36, 36)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(prevMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(84, 84, 84)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                                    .addComponent(nextMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))))
                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(11, 11, 11)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(filtersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(prevMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(nextMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(30, 30, 30))
            );

            javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(15, Short.MAX_VALUE))
            );
            jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(20, Short.MAX_VALUE))
            );

            FileMenu.setText("Soubor");

            filtersMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
            filtersMenuItem.setText("Filtry");
            filtersMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filtersMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(filtersMenuItem);

            importMenu.setText("Import");

            iCalImportMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK));
            iCalImportMenu.setText("iCalendar");
            iCalImportMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    iCalImportMenuActionPerformed(evt);
                }
            });
            importMenu.add(iCalImportMenu);

            hCalImportMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.SHIFT_MASK));
            hCalImportMenu.setText("hCalendar");
            hCalImportMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    hCalImportMenuActionPerformed(evt);
                }
            });
            importMenu.add(hCalImportMenu);

            FileMenu.add(importMenu);

            exportMenu.setText("Export");

            iCalExportMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
            iCalExportMenu.setText("iCalendar");
            iCalExportMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    iCalExportMenuActionPerformed(evt);
                }
            });
            exportMenu.add(iCalExportMenu);

            hCalExportMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
            hCalExportMenu.setText("hCalendar");
            hCalExportMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    hCalExportMenuActionPerformed(evt);
                }
            });
            exportMenu.add(hCalExportMenu);

            exportMenu.setText("Export");

            FileMenu.add(exportMenu);

            ExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
            ExitMenuItem.setText("Konec");
            ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExitMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(ExitMenuItem);

            MenuBar.add(FileMenu);

            jMenu3.setText("Měsíc");

            goToMonthMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
            goToMonthMenu.setText("Jít na..");
            goToMonthMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    goToMonthMenuActionPerformed(evt);
                }
            });
            jMenu3.add(goToMonthMenu);

            prevMonthMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
            prevMonthMenu.setText("Předchozí");
            prevMonthMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    prevMonthMenuActionPerformed(evt);
                }
            });
            jMenu3.add(prevMonthMenu);

            nextMonthMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
            nextMonthMenu.setText("Další");
            nextMonthMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    nextMonthMenuActionPerformed(evt);
                }
            });
            jMenu3.add(nextMonthMenu);

            MenuBar.add(jMenu3);

            jMenu1.setText("Událost");

            newEventMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
            newEventMenu.setText("Nová událost");
            newEventMenu.setEnabled(false);
            newEventMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newEventMenuActionPerformed(evt);
                }
            });
            jMenu1.add(newEventMenu);

            editEventMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
            editEventMenu.setText("Upravit");
            editEventMenu.setEnabled(false);
            editEventMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editEventMenuActionPerformed(evt);
                }
            });
            jMenu1.add(editEventMenu);

            removeEventMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
            removeEventMenu.setText("Odstranit");
            removeEventMenu.setEnabled(false);
            removeEventMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    removeEventMenuActionPerformed(evt);
                }
            });
            jMenu1.add(removeEventMenu);

            removeAllEventsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
            removeAllEventsMenu.setText("Odstranit vše");
            removeAllEventsMenu.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    removeAllEventsMenuActionPerformed(evt);
                }
            });
            jMenu1.add(removeAllEventsMenu);

            MenuBar.add(jMenu1);

            jMenu2.setText("Info");

            jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
            jMenuItem5.setText("O programu..");
            jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem5ActionPerformed(evt);
                }
            });
            jMenu2.add(jMenuItem5);

            MenuBar.add(jMenu2);

            setJMenuBar(MenuBar);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void newEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEventButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialog addDialog = new AddDialog(null, true, daysTable, eventsList,
                        filters, evtManager, calManager);
                addDialog.setLocationRelativeTo(daysTable);
                addDialog.setVisible(true);
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_newEventButtonActionPerformed

    private void editEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEventButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Event event = listModel.getEventAt(eventsList.getSelectedIndex());
                JDialog editDialog = new EditDialog(null, true, daysTable, eventsList, filters, event, evtManager);
                editDialog.setLocationRelativeTo(daysTable);
                editDialog.setVisible(true);
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_editEventButtonActionPerformed

    private void FiltersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltersMenuItemActionPerformed
    }//GEN-LAST:event_FiltersMenuItemActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                String newline = System.getProperty("line.separator");
                JOptionPane.showMessageDialog(rootPane, "DesKal - desktopový kalendář (verze 1.0)"
                        + newline + newline + "Autoři: Jan Bruzl, Tomáš Dragoun, "
                        + "Lukáš Tesař, Jiří Železný", "DesKal", JOptionPane.PLAIN_MESSAGE);
            }
        });

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void removeEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = JOptionPane.showConfirmDialog(rootPane, "Odstranit událost  " + eventsList.getSelectedValue() + " ?", "Odstranit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (i == JOptionPane.YES_OPTION) {
                    int index = eventsList.getSelectedIndex();
                    removeEventWorker = new RemoveEventWorker(listModel.getEventAt(index));
                    removeEventWorker.execute();
                    listModel.remove(index);
                }
            }
        });
    }//GEN-LAST:event_removeEventButtonActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        calendarDB.closeDB();
        dispose();
        System.exit(0);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void filtersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtersComboBoxActionPerformed
        refreshTableSwingWorker = new RefreshTableSwingWorker(date);
        refreshTableSwingWorker.execute();
    }//GEN-LAST:event_filtersComboBoxActionPerformed

    private void daysTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_daysTableMouseReleased
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Integer day = (Integer) daysTable.getValueAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn());
                if (day == null) {
                    String thisMonth = getNameOfMonth(date.get(Calendar.MONTH));
                    monthLabel.setText(thisMonth);
                } else {
                    String thisMonth = getNameOfMonth2(date.get(Calendar.MONTH));
                    monthLabel.setText(day + ". " + thisMonth);
                }

                loadEvents();
            }
        });
    }//GEN-LAST:event_daysTableMouseReleased

    private void daysTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_daysTableKeyReleased
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Integer day = (Integer) daysTable.getValueAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn());
                if (day == null) {
                    String thisMonth = getNameOfMonth(date.get(Calendar.MONTH));
                    monthLabel.setText(thisMonth);
                } else {
                    String thisMonth = getNameOfMonth2(date.get(Calendar.MONTH));
                    monthLabel.setText(day + ". " + thisMonth);
                }

                loadEvents();
            }
        });
    }//GEN-LAST:event_daysTableKeyReleased

    private void prevMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevMonthButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int month = date.get(Calendar.MONTH);
                int year = date.get(Calendar.YEAR);
                month--;
                if (month == -1) {
                    month = 11;
                    year--;
                }
                date.set(Calendar.MONTH, month);
                date.set(Calendar.YEAR, year);
                monthLabel.setText(getNameOfMonth(date.get(Calendar.MONTH)));
                yearLabel.setText(Integer.toString(date.get(Calendar.YEAR)));
                daysTable.clearSelection();
                eventsList.clearSelection();
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });

    }//GEN-LAST:event_prevMonthButtonActionPerformed

    private void nextMonthButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMonthButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int month = date.get(Calendar.MONTH);
                int year = date.get(Calendar.YEAR);
                month++;
                if (month == 12) {
                    month = 0;
                    year++;
                }
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                monthLabel.setText(getNameOfMonth(date.get(Calendar.MONTH)));
                yearLabel.setText(Integer.toString(date.get(Calendar.YEAR)));
                daysTable.clearSelection();
                eventsList.clearSelection();
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_nextMonthButtonActionPerformed

    private void iCalExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iCalExportMenuActionPerformed
        final JFileChooser exportChooser = new JFileChooser();
        exportChooser.setFileFilter(new FileNameExtensionFilter("iCalendar", "ics"));
        exportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        exportChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = exportChooser.showSaveDialog(iCalExportMenu);
                if (i == JFileChooser.APPROVE_OPTION) {
                    String fileName = exportChooser.getSelectedFile().getAbsolutePath();
                    exportICalWorker = new ExportICalWorker(fileName);
                    exportICalWorker.execute();
                }
            }
        });
}//GEN-LAST:event_iCalExportMenuActionPerformed

    private void filtersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtersMenuItemActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialog filtersDialog = new FiltersDialog(null, true, filters, filtersComboBox, calManager);
                filtersDialog.setLocationRelativeTo(daysTable);
                filtersDialog.setVisible(true);
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_filtersMenuItemActionPerformed

    private void hCalExportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hCalExportMenuActionPerformed
        final JFileChooser exportChooser = new JFileChooser();
        exportChooser.setFileFilter(new FileNameExtensionFilter("hCalendar", "xhtml"));
        exportChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        exportChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = exportChooser.showSaveDialog(hCalExportMenu);
                if (i == JFileChooser.APPROVE_OPTION) {
                    String fileName = exportChooser.getSelectedFile().getAbsolutePath();
                    exportHCalWorker = new ExportHCalWorker(fileName);
                    exportHCalWorker.execute();
                }
            }
        });
    }//GEN-LAST:event_hCalExportMenuActionPerformed

    private void iCalImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iCalImportMenuActionPerformed
        final JFileChooser importChooser = new JFileChooser();
        importChooser.addChoosableFileFilter(new FileNameExtensionFilter("Soubor ve formátu iCalendar", "ics"));
        importChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = importChooser.showOpenDialog(iCalImportMenu);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File file = importChooser.getSelectedFile();
                    importICalWorker = new ImportICalWorker(file);
                    importICalWorker.execute();
                }
            }
        });
    }//GEN-LAST:event_iCalImportMenuActionPerformed

    private void hCalImportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hCalImportMenuActionPerformed
        final JFileChooser importChooser = new JFileChooser();
        importChooser.addChoosableFileFilter(new FileNameExtensionFilter("Soubor ve formátu hCalalendar", "xhtml"));
        importChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = importChooser.showOpenDialog(iCalImportMenu);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File file = importChooser.getSelectedFile();
                    importHCalWorker = new ImportHCalWorker(file);
                    importHCalWorker.execute();
                }
            }
        });
    }//GEN-LAST:event_hCalImportMenuActionPerformed

    private void newEventMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEventMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialog addDialog = new AddDialog(null, true, daysTable, eventsList,
                        filters, evtManager, calManager);
                addDialog.setLocationRelativeTo(daysTable);
                addDialog.setVisible(true);
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_newEventMenuActionPerformed

    private void editEventMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEventMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Event event = listModel.getEventAt(eventsList.getSelectedIndex());
                JDialog editDialog = new EditDialog(null, true, daysTable, eventsList, filters, event, evtManager);
                editDialog.setLocationRelativeTo(daysTable);
                editDialog.setVisible(true);
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_editEventMenuActionPerformed

    private void removeEventMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = JOptionPane.showConfirmDialog(rootPane, "Odstranit událost  " + eventsList.getSelectedValue() + " ?", "Odstranit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (i == JOptionPane.YES_OPTION) {
                    int index = eventsList.getSelectedIndex();
                    removeEventWorker = new RemoveEventWorker(listModel.getEventAt(index));
                    removeEventWorker.execute();
                    listModel.remove(index);
                }
            }
        });
    }//GEN-LAST:event_removeEventMenuActionPerformed

    private void prevMonthMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevMonthMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int month = date.get(Calendar.MONTH);
                int year = date.get(Calendar.YEAR);
                month--;
                if (month == -1) {
                    month = 11;
                    year--;
                }
                date.set(Calendar.MONTH, month);
                date.set(Calendar.YEAR, year);
                monthLabel.setText(getNameOfMonth(date.get(Calendar.MONTH)));
                yearLabel.setText(Integer.toString(date.get(Calendar.YEAR)));
                daysTable.clearSelection();
                eventsList.clearSelection();
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });

    }//GEN-LAST:event_prevMonthMenuActionPerformed

    private void nextMonthMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextMonthMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int month = date.get(Calendar.MONTH);
                int year = date.get(Calendar.YEAR);
                month++;
                if (month == 12) {
                    month = 0;
                    year++;
                }
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                monthLabel.setText(getNameOfMonth(date.get(Calendar.MONTH)));
                yearLabel.setText(Integer.toString(date.get(Calendar.YEAR)));
                daysTable.clearSelection();
                eventsList.clearSelection();
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_nextMonthMenuActionPerformed

    private void goToMonthMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToMonthMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialog goToMonthDialog = new GoToDialog(null, true, date);
                goToMonthDialog.setLocationRelativeTo(daysTable);
                goToMonthDialog.setVisible(true);
                monthLabel.setText(getNameOfMonth(date.get(Calendar.MONTH)));
                yearLabel.setText(Integer.toString(date.get(Calendar.YEAR)));
                daysTable.clearSelection();
                eventsList.clearSelection();
                refreshTableSwingWorker = new RefreshTableSwingWorker(date);
                refreshTableSwingWorker.execute();
            }
        });
    }//GEN-LAST:event_goToMonthMenuActionPerformed

    private void removeAllEventsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllEventsMenuActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = JOptionPane.showConfirmDialog(rootPane, "ODSTRANIT VŠECHNY UDÁLOSTI?",
                        "Odstranit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (i == JOptionPane.YES_OPTION) {
                    removeAllEvents = new RemoveAllEventsWorker();
                    removeAllEvents.execute();
                    listModel.cleanList();
                }
            }
        });
    }//GEN-LAST:event_removeAllEventsMenuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JLabel currentDateLabel;
    private javax.swing.JTable daysTable;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JButton editEventButton;
    private javax.swing.JMenuItem editEventMenu;
    private javax.swing.JList eventsList;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JComboBox filtersComboBox;
    private javax.swing.JMenuItem filtersMenuItem;
    private javax.swing.JMenuItem goToMonthMenu;
    private javax.swing.JMenuItem hCalExportMenu;
    private javax.swing.JMenuItem hCalImportMenu;
    private javax.swing.JMenuItem iCalExportMenu;
    private javax.swing.JMenuItem iCalImportMenu;
    private javax.swing.JMenu importMenu;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton newEventButton;
    private javax.swing.JMenuItem newEventMenu;
    private javax.swing.JButton nextMonthButton;
    private javax.swing.JMenuItem nextMonthMenu;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel placeLabel;
    private javax.swing.JButton prevMonthButton;
    private javax.swing.JMenuItem prevMonthMenu;
    private javax.swing.JMenuItem removeAllEventsMenu;
    private javax.swing.JButton removeEventButton;
    private javax.swing.JMenuItem removeEventMenu;
    private javax.swing.JLabel tagLabel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables

    private void loadEvents() {
        eventsList.clearSelection();

        if (tableModel.isDayAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn())) {
            newEventButton.setEnabled(true);
            newEventMenu.setEnabled(true);

        } else {
            newEventButton.setEnabled(false);
            newEventMenu.setEnabled(false);
        }
        List<Event> events = tableModel.getEventsAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn());


        if (events != null) {
            listModel.setEvents(events);
            eventsList.setSelectedIndex(0);
        } else {
            listModel.cleanList();
        }
    }

    private String getNameOfMonth(int get) {
        switch (get) {
            case 0:
                return "Leden";


            case 1:
                return "Únor";


            case 2:
                return "Březen";


            case 3:
                return "Duben";


            case 4:
                return "Květen";


            case 5:
                return "Červen";


            case 6:
                return "Červenec";


            case 7:
                return "Srpen";


            case 8:
                return "Září ";


            case 9:
                return "Říjen";


            case 10:
                return "Listopad";


            case 11:
                return "Prosinec";


            default:
                return "";


        }
    }

    private String getNameOfMonth2(int get) {
        switch (get) {
            case 0:
                return "Ledna";


            case 1:
                return "Února";


            case 2:
                return "Března";


            case 3:
                return "Dubna";


            case 4:
                return "Května";


            case 5:
                return "Června";


            case 6:
                return "Července";


            case 7:
                return "Srpna";


            case 8:
                return "Září ";


            case 9:
                return "Října";


            case 10:
                return "Listopadu";


            case 11:
                return "Prosince";


            default:
                return "";

        }
    }
}
