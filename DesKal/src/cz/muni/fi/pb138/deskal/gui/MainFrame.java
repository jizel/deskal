package cz.muni.fi.pb138.deskal.gui;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import cz.muni.fi.pb138.deskal.CalendarDB;
import cz.muni.fi.pb138.deskal.CalendarManager;
import cz.muni.fi.pb138.deskal.CalendarManagerImpl;
import cz.muni.fi.pb138.deskal.Day;
import cz.muni.fi.pb138.deskal.Event;
import cz.muni.fi.pb138.deskal.Filter;
import java.awt.Color;
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
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.swing.UIManager;

/**
 *
 * @author Drak
 */
public class MainFrame extends javax.swing.JFrame {

    private Calendar date;
    private DaysTableModel tableModel;
    private EventListModel listModel;
    private FiltersComboBoxModel comboModel;
    private List<Filter> filters = new ArrayList();
    private InitGuiSwingWorker initGuiSwingWorker;
    private CalendarManager calManager;
    private CalendarDB calendarDB;

    private class InitGuiSwingWorker extends SwingWorker<List<String>, Void> {

        @Override
        protected List<String> doInBackground() throws Exception {
            List<String> labels = new ArrayList<String>();
            Locale cz = new Locale("cs", "CZ");
            date = GregorianCalendar.getInstance(cz);
            date.setTimeInMillis(System.currentTimeMillis());
            String thisDay = Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + ". ";
            String thisMonth = getNameOfMonth(date.get(Calendar.MONTH)) + " ";
            String thisYear = Integer.toString(date.get(Calendar.YEAR));
            labels.add(thisDay);
            labels.add(thisMonth);
            labels.add(thisYear);

            calendarDB = new CalendarDB();
            calendarDB.ConnectToBaseX("calendar.xml");
            calManager = new CalendarManagerImpl(calendarDB.getContext());
            for (String name : calManager.getAllTags()) {
                filters.add(new Filter(name));
            }
            return labels;
        }

        protected void done() {
            try {
                yearLabel.setText(get().get(2));
                monthLabel.setText(get().get(1));
                currentDateLabel.setText(get().get(0) + get().get(1) + get().get(2));
                comboModel.setFilters(filters);
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
            }
        }
    }

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
        initGuiSwingWorker = new InitGuiSwingWorker();
        initGuiSwingWorker.execute();
        Filter none = new Filter("default"); //default filter
        filters.add(none);
        comboModel = (FiltersComboBoxModel) filtersComboBox.getModel();
        comboModel.setFilters(filters);
        filtersComboBox.setSelectedIndex(0);
        daysTable.setRowHeight((daysTable.getHeight() - 7) /daysTable.getRowCount());
        //table and list test
        List<Day> month = new ArrayList<Day>();
        Day day = new Day();
        Event event = new Event();
        event.setName("Some event");
        event.setDate(new XMLGregorianCalendarImpl());
        event.getDate().setTime(12, 30, 0);
        DatatypeFactory df = null;
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
        }
        Duration duration = df.newDuration(10000000);
        Duration duration2 = df.newDuration(100000000);
        event.setDuration(duration);
        Event event2 = new Event();
        event2.setDuration(duration2);
        event2.setName("Some other event");
        event2.setDate(new XMLGregorianCalendarImpl());
        event2.getDate().setTime(13, 00, 0);
        day.addEvent(event);
        day.addEvent(event2);
        day.setDate(new XMLGregorianCalendarImpl());
        day.getDate().setYear(2011);
        day.getDate().setMonth(5);
        day.getDate().setDay(1);
        month.add(day);
        tableModel = (DaysTableModel) daysTable.getModel();
        tableModel.setMonth(month);
        listModel = (EventListModel) eventsList.getModel();
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
            FiltersMenuItem = new javax.swing.JMenuItem();
            ImportMenuItem = new javax.swing.JMenuItem();
            ExportMenuItem = new javax.swing.JMenuItem();
            ExitMenuItem = new javax.swing.JMenuItem();
            jMenu2 = new javax.swing.JMenu();
            jMenuItem5 = new javax.swing.JMenuItem();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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

            nextMonthButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cz/muni/fi/pb138/deskal/gui/rarrowicon.gif"))); // NOI18N

            jLabel3.setText("Filter");

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
            eventsList.addListSelectionListener(new EventListSelectionListener(editEventButton,removeEventButton,eventsList,labels));

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Event"));

            timeLabel.setText("  ");

            durationLabel.setText(" ");

            placeLabel.setText(" ");

            noteLabel.setText(" ");

            nameLabel.setText(" ");

            tagLabel.setText(" ");

            jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel9.setText("note");

            jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel8.setText("tag");

            jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel4.setText("name");

            jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel7.setText("duration");

            jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel5.setText("place");

            jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
            jLabel6.setText("time");

            javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(16, 16, 16))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(22, 22, 22)))
                        .addComponent(jLabel7)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9))
                    .addGap(22, 22, 22)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(placeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(durationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(tagLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(noteLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(nameLabel))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(placeLabel))
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

            currentDateLabel.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
            currentDateLabel.setText("TODAY");

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
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(currentDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            editEventButton.setText("Edit");
            editEventButton.setEnabled(false);
            editEventButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editEventButtonActionPerformed(evt);
                }
            });

            newEventButton.setText("New event");
            newEventButton.setEnabled(false);
            newEventButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    newEventButtonActionPerformed(evt);
                }
            });

            removeEventButton.setText("Remove");
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

            monthLabel.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
            monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            monthLabel.setText("MONTH");

            yearLabel.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
            yearLabel.setText("YEAR");

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(monthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(yearLabel)
                    .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(yearLabel)
                        .addComponent(monthLabel))
                    .addContainerGap())
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
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(prevMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(84, 84, 84)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(nextMonthButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
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
                    .addContainerGap(30, Short.MAX_VALUE))
            );
            jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE))
            );

            FileMenu.setText("File");

            FiltersMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
            FiltersMenuItem.setText("Filters");
            FiltersMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FiltersMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(FiltersMenuItem);

            ImportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
            ImportMenuItem.setText("Import");
            ImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ImportMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(ImportMenuItem);

            ExportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
            ExportMenuItem.setText("Export");
            ExportMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExportMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(ExportMenuItem);

            ExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
            ExitMenuItem.setText("Exit");
            ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExitMenuItemActionPerformed(evt);
                }
            });
            FileMenu.add(ExitMenuItem);

            MenuBar.add(FileMenu);

            jMenu2.setText("Info");

            jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
            jMenuItem5.setText("About");
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
                JDialog addDialog = new AddDialog(null, true, daysTable, eventsList, filters);
                addDialog.setLocationRelativeTo(daysTable);
                addDialog.setVisible(true);
                eventsList.clearSelection();
            }
        });
    }//GEN-LAST:event_newEventButtonActionPerformed

    private void editEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEventButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Event event = listModel.getEventAt(eventsList.getSelectedIndex());
                JDialog editDialog = new EditDialog(null, true, daysTable, eventsList, filters, event);
                editDialog.setLocationRelativeTo(daysTable);
                editDialog.setVisible(true);
                eventsList.clearSelection();
            }
        });
    }//GEN-LAST:event_editEventButtonActionPerformed

    private void FiltersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltersMenuItemActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialog filtersDialog = new FiltersDialog(null, true, filters, filtersComboBox);
                filtersDialog.setLocationRelativeTo(daysTable);
                filtersDialog.setVisible(true);
            }
        });
    }//GEN-LAST:event_FiltersMenuItemActionPerformed

    private void ImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMenuItemActionPerformed
        final JFileChooser importChooser = new JFileChooser();
        importChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML file in iCal or hCal format", "xml"));
        importChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                importChooser.showOpenDialog(ImportMenuItem);
            }
        });
    }//GEN-LAST:event_ImportMenuItemActionPerformed

    private void ExportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportMenuItemActionPerformed
        final JFileChooser exportChooser = new JFileChooser();
        exportChooser.addChoosableFileFilter(new FileNameExtensionFilter(".xml", "xml"));
        exportChooser.setAcceptAllFileFilterUsed(false);
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                exportChooser.showSaveDialog(ExportMenuItem);
            }
        });
    }//GEN-LAST:event_ExportMenuItemActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JOptionPane.showMessageDialog(rootPane, "DesKal - version 1.0", "DesKal", JOptionPane.PLAIN_MESSAGE);
            }
        });

    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void removeEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEventButtonActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                int i = JOptionPane.showConfirmDialog(rootPane, "Remove event " + eventsList.getSelectedValue() + " ?", "Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (i == JOptionPane.YES_OPTION) {
                    listModel.remove(eventsList.getSelectedIndex());
                    eventsList.clearSelection();
                    tableModel.fireTableCellUpdated(daysTable.getSelectedRow(), daysTable.getSelectedColumn());
                }
            }
        });

    }//GEN-LAST:event_removeEventButtonActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void filtersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtersComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtersComboBoxActionPerformed

    private void daysTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_daysTableMouseReleased
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                loadEvents();
            }
        });
    }//GEN-LAST:event_daysTableMouseReleased

    private void daysTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_daysTableKeyReleased
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                loadEvents();
            }
        });
    }//GEN-LAST:event_daysTableKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenuItem ExportMenuItem;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuItem FiltersMenuItem;
    private javax.swing.JMenuItem ImportMenuItem;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JLabel currentDateLabel;
    private javax.swing.JTable daysTable;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JButton editEventButton;
    private javax.swing.JList eventsList;
    private javax.swing.JComboBox filtersComboBox;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
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
    private javax.swing.JButton nextMonthButton;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel placeLabel;
    private javax.swing.JButton prevMonthButton;
    private javax.swing.JButton removeEventButton;
    private javax.swing.JLabel tagLabel;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables

    private void loadEvents() {
        eventsList.clearSelection();
        if (tableModel.isDayAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn())) {
            newEventButton.setEnabled(true);
        } else {
            newEventButton.setEnabled(false);
        }
        List<Event> events = tableModel.getEventsAt(daysTable.getSelectedRow(), daysTable.getSelectedColumn());
        if (events != null) {
            listModel.setEvents(events);
        } else {
            listModel.cleanList();
        }
    }

    private String getNameOfMonth(int get) {
        switch (get) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "";
        }
    }
}