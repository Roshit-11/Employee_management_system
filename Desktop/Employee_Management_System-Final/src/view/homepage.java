/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;
import model.AdminModel;
import model.Employee;
import model.AttendanceRecord;
import model.LeaveRequest;
import controller.LoginController;
import controller.AttendanceController;
import controller.LeaveController;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import controller.EmployeeSortController;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author roshitlamichhane
 */
public class homepage extends javax.swing.JFrame {
        private LinkedList<Employee> employeeCache = new LinkedList<>();
        private List<AttendanceRecord> attendanceCache = new java.util.ArrayList<>();

    // Snapshot of the full attendance table (used to restore after search/clear)
    private List<AttendanceRecord> attendanceFullCache = new ArrayList<>();

    // ==============================
    // Attendance Search (Linear Search)
    // NOTE: Attendance list is NOT guaranteed to be sorted, so linear search is correct here.
    // ==============================


  
    /**
     * Call this from the Attendance panel Clear button action.
     */
   

    private void renderEmployeesToTable(List<Employee> emps) {
        DefaultTableModel tm = (DefaultTableModel) jTable2.getModel();
        tm.setRowCount(0);

        if (emps == null) return;

        for (Employee e : emps) {
            if (e == null) continue;

            tm.addRow(new Object[] {
                    safe(e.getFullName()),
                    safe(e.getUsername()),
                    safe(e.getEmail()),
                    safe(e.getPhone()),
                    safe(e.getDepartment()),
                    safe(e.getEmployeeType()),
                    safe(e.getAddress())
            });
        }
    }
    private void renderAttendanceToTable(List<AttendanceRecord> records) {
    DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
    tm.setRowCount(0);

    if (records == null) return;

    for (AttendanceRecord r : records) {
        if (r == null) continue;

        tm.addRow(new Object[] {
            r.getUsername() == null ? "" : r.getUsername(),
            r.getDate() == null ? "" : r.getDate().toString(),
            r.getPunchIn() == null ? "" : r.getPunchIn().toString(),
            r.getPunchOut() == null ? "" : r.getPunchOut().toString()
        });
    }
}

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    // ==============================
    // Leave approval (pending request)
    // ==============================
    private LeaveRequest currentPendingLeave;

    // Shared AdminModel instance for this frame (NetBeans GUI safe)
    private final AdminModel adminModel = new AdminModel();
    private final EmployeeSortController employeeSortController = new EmployeeSortController();
private void updateTotalEmployeesLabel() {
    try {
        int total = AdminModel.getTotalEmployeeCount();
        if (TotalEmployees != null) {
            TotalEmployees.setText(String.valueOf(total));
        }
    } catch (Exception ex) {
        
    }
}

    // ==============================
    // Total Pending Leaves (FIFO queue, status = Pending)
    // Note: We set the label by component NAME to avoid touching NetBeans variables.
    // In Design view, select your pending-leaves JLabel and set its Name property to: TotalPendingLeaves
    // ==============================
    
private void updateTotalPendingLeavesLabel() {
    try {
        int pending = LeaveController.countPendingLeaves();
        if (TotalPendingLeaves != null) {
            TotalPendingLeaves.setText(String.valueOf(pending));
        }
    } catch (Exception ex) {
    }
}
public void refreshAttendanceTable() {

    List<AttendanceRecord> records =
            AttendanceController.getAttendanceStackAsList();

    attendanceCache = (records == null)
            ? new ArrayList<>()
            : new ArrayList<>(records);

    attendanceFullCache = new ArrayList<>(attendanceCache);

    renderAttendanceToTable(attendanceCache);
    updatePresentCountLabel();
}
    private void setTodayDate(javax.swing.JLabel... labels) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

        String today = LocalDate.now().format(formatter);

        for (javax.swing.JLabel label : labels) {
            if (label != null) {
                label.setText(today);
            }
        }
    }
    

    // ==============================
    // Real-time Present Count
    // ==============================
    private void updatePresentCountLabel() {
    try {
        int present = AttendanceController.countPresentToday();
        if (CurrentPresentCount != null) {
            CurrentPresentCount.setText(String.valueOf(present));
        }
    } catch (Exception ex) {
    }
}

    

  

    public void refreshLeaveApplicationsTable() {
        DefaultTableModel tm = (DefaultTableModel) leave_application_table.getModel();
        tm.setRowCount(0); // clear old rows

        List<LeaveRequest> requests = LeaveController.getAllLeavesFIFO();

        for (LeaveRequest r : requests) {
            if (r == null) continue;

            tm.addRow(new Object[] {
                r.getUsername(),
                r.getFromDate() == null ? "" : r.getFromDate().toString(),
                r.getToDate() == null ? "" : r.getToDate().toString(),
                r.getLeaveType() == null ? "" : r.getLeaveType(),
                r.getReason() == null ? "" : r.getReason(),
                r.getRemarks() == null ? "" : r.getRemarks(),
                r.getStatus() == null ? "" : r.getStatus()
            });
        }
    }

    // Load the next pending leave request (oldest pending) into the UI section above the table
    public void loadNextPendingLeaveToApproveSection() {
        currentPendingLeave = LeaveController.peekNextPendingLeave();

        if (currentPendingLeave == null) {
            // No pending requests
            Name.setText("No pending leave requests");
            FromDate.setText("");
            ToDate.setText("");
            LeaveType.setText("");
            jTextArea2.setText("");
            // disable buttons when nothing to decide
            jButton10.setEnabled(false);
            jButton11.setEnabled(false);
            return;
        }

        // Show request details
        Name.setText(currentPendingLeave.getUsername());
        FromDate.setText(currentPendingLeave.getFromDate() == null ? "" : currentPendingLeave.getFromDate().toString());
        ToDate.setText(currentPendingLeave.getToDate() == null ? "" : currentPendingLeave.getToDate().toString());
        LeaveType.setText(currentPendingLeave.getLeaveType() == null ? "" : currentPendingLeave.getLeaveType());

        // Use the text area for admin remarks (you can type remarks here)
        // (If you want to show the leave reason instead, replace this with getReason())
        jTextArea2.setText("");

        jButton10.setEnabled(true);
        jButton11.setEnabled(true);
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(homepage.class.getName());

    /**
     * Creates new form homepage
     */
    public homepage() {
       
        initComponents();
    
            // Scale employee profile images to fit their labels (prevents cropping)
        setScaledIcon(jLabel5, "/images/sridhar.png");
        setScaledIcon(jLabel6, "/images/sachin.png");
        setScaledIcon(jLabel9, "/images/rabin.png");
        setScaledIcon(jLabel11, "/images/sanskar.png");
        setScaledIcon(jLabel13, "/images/oscar.png");
        setScaledIcon(jLabel10, "/images/ram.png");
        setScaledIcon(jLabel28, "/images/ems_logo.png");
        setTodayDate(date_today1, date_today2, date_today);
        updatePresentCountLabel();
        updateTotalEmployeesLabel();
        updateTotalPendingLeavesLabel();
    }  
    
    private void setScaledIcon(javax.swing.JLabel label, String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                System.out.println("Image not found: " + resourcePath);
                return;
            }

            ImageIcon icon = new ImageIcon(url);

            // Determine target size: prefer actual size; fallback to preferred size; then sensible default
            int targetW = label.getWidth();
            int targetH = label.getHeight();

            if (targetW <= 0 || targetH <= 0) {
                Dimension pref = label.getPreferredSize();
                if (pref != null && pref.width > 0 && pref.height > 0) {
                    targetW = pref.width;
                    targetH = pref.height;
                } else {
                    targetW = 170;
                    targetH = 170;
                }
            }

            // Scale while preserving aspect ratio (fit inside label)
            int imgW = icon.getIconWidth();
            int imgH = icon.getIconHeight();
            if (imgW <= 0 || imgH <= 0) return;

            double scale = Math.min((double) targetW / imgW, (double) targetH / imgH);
            int newW = Math.max(1, (int) Math.round(imgW * scale));
            int newH = Math.max(1, (int) Math.round(imgH * scale));

            Image scaled = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

            label.setIcon(new ImageIcon(scaled));
            label.setText("");
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            label.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void setUserGreeting(String username) {
        user_greet.setText("Hello, " + username + "! 👋");
        updateTotalEmployeesLabel();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        user_greet = new javax.swing.JLabel();
        date_today2 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        CurrentPresent = new javax.swing.JLabel();
        CurrentPresentCount = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        TotalEmployees = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        TotalPendingLeaves = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        leave_application = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        date_today = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        leave_application_table = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        Name = new javax.swing.JLabel();
        FromDate = new javax.swing.JLabel();
        ToDate = new javax.swing.JLabel();
        LeaveType = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        attendance = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        date_today1 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        attendanceSearchField = new javax.swing.JTextField();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        Add_Employees = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        card56 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        Full_Name = new javax.swing.JTextField();
        Username = new javax.swing.JTextField();
        jComboBox7 = new javax.swing.JComboBox<>();
        Phone_Number = new javax.swing.JTextField();
        Address = new javax.swing.JTextField();
        Email = new javax.swing.JTextField();
        Password = new javax.swing.JPasswordField();
        Employee_Type = new javax.swing.JComboBox<>();
        Add_Employee_btn = new javax.swing.JButton();
        EmpErrorLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(34, 48, 58));

        jButton1.setBackground(new java.awt.Color(34, 48, 59));
        jButton1.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 204, 204));
        jButton1.setText("Dashboard       >");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(34, 48, 59));
        jButton4.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(204, 204, 204));
        jButton4.setText("Attendance      >");
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(34, 48, 59));
        jButton2.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(204, 204, 204));
        jButton2.setText("Leave Request  >");
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(34, 48, 59));
        jButton6.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton6.setForeground(new java.awt.Color(204, 204, 204));
        jButton6.setText("Add Employees >");
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(34, 48, 59));
        jButton3.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(204, 204, 204));
        jButton3.setText("Profile          >");
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(34, 48, 59));
        jButton5.setFont(new java.awt.Font("Kannada Sangam MN", 0, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(204, 204, 204));
        jButton5.setText("Employees     >");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(34, 48, 58));
        jButton7.setForeground(new java.awt.Color(204, 204, 204));
        jButton7.setText("Log Out");
        jButton7.setToolTipText("");
        jButton7.setBorder(null);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))
                        .addContainerGap())))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 110, 240, 570);

        jPanel3.setBackground(new java.awt.Color(34, 48, 58));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ems_logo.png"))); // NOI18N
        jLabel28.setText("jLabel28");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1022, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 0, 1140, 100);

        jPanel4.setLayout(new java.awt.CardLayout());

        jPanel17.setBackground(new java.awt.Color(204, 212, 207));

        user_greet.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        user_greet.setText("Hello, John Doe! 👋");

        date_today2.setText("Friday, November 21, 2025");

        jPanel18.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        CurrentPresent.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        CurrentPresent.setText("Currently working:");

        CurrentPresentCount.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CurrentPresent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CurrentPresentCount)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CurrentPresent)
                    .addComponent(CurrentPresentCount))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel29.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel29.setText("Total Employees:");

        TotalEmployees.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TotalEmployees)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TotalEmployees)
                    .addComponent(jLabel29))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N
        jLabel30.setText("Pending Leaves:");
        jLabel30.setToolTipText("");

        TotalPendingLeaves.setFont(new java.awt.Font("Helvetica Neue", 0, 16)); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TotalPendingLeaves, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(TotalPendingLeaves))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_today2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(user_greet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(user_greet)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(date_today2)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel22.setBackground(new java.awt.Color(204, 204, 204));

        jLabel8.setFont(new java.awt.Font("Helvetica", 0, 18)); // NOI18N
        jLabel8.setText("Employees");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sridhar.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sachin.png"))); // NOI18N
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rabin.png"))); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/sanskar.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/oscar.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ram.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        jLabel14.setText("Sridhar Niraula");

        jLabel15.setText("Sachin Sigdel");

        jLabel16.setText("Rabin Bam");

        jLabel25.setText("Sanskar Nepal");

        jLabel26.setText("Oscar Kafle");

        jLabel27.setText("Ram Nepali");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel22Layout.createSequentialGroup()
                                    .addGap(41, 41, 41)
                                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 144, Short.MAX_VALUE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGap(143, 143, 143)
                                    .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGap(138, 138, 138))
                                .addGroup(jPanel22Layout.createSequentialGroup()
                                    .addGap(70, 70, 70)
                                    .addComponent(jLabel14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15)
                                    .addGap(169, 169, 169))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel22Layout.createSequentialGroup()
                                    .addGap(71, 71, 71)
                                    .addComponent(jLabel25)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(jPanel22Layout.createSequentialGroup()
                                .addGap(370, 370, 370)
                                .addComponent(jLabel26)
                                .addGap(205, 205, 205)))
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel22Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel16))
                            .addGroup(jPanel22Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel27))))
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(352, 352, 352)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(24, 24, 24)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14)
                            .addComponent(jLabel16))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(jLabel27))))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel7, "card4");

        leave_application.setBackground(new java.awt.Color(204, 204, 204));

        jPanel8.setBackground(new java.awt.Color(204, 212, 207));

        date_today.setText("Friday, November 21, 2025");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(date_today, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(767, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_today)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(204, 204, 204));

        leave_application_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "From Date", "To Date", "Leave Type", "Leave Reason", "Remarks", "Status"
            }
        ));
        jScrollPane3.setViewportView(leave_application_table);

        jLabel12.setFont(new java.awt.Font("Kailasa", 0, 18)); // NOI18N
        jLabel12.setText("Leave Applications");

        Name.setText("jLabel2");

        FromDate.setText("jLabel2");

        ToDate.setText("jLabel2");

        LeaveType.setText("jLabel2");

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane4.setViewportView(jTextArea2);

        jLabel2.setText("Remark");

        jButton10.setBackground(new java.awt.Color(102, 255, 51));
        jButton10.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jButton10.setText("Approve");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(255, 51, 51));
        jButton11.setText("Reject");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(Name)
                                .addGap(55, 55, 55)
                                .addComponent(FromDate)
                                .addGap(73, 73, 73)
                                .addComponent(ToDate)
                                .addGap(62, 62, 62)
                                .addComponent(LeaveType)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addGap(223, 223, 223)
                                        .addComponent(jLabel2)
                                        .addGap(177, 177, 177)
                                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(81, 81, 81))))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton10)
                        .addGap(54, 54, 54)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(jLabel2))
                .addGap(8, 8, 8)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Name)
                            .addComponent(FromDate)
                            .addComponent(ToDate)
                            .addComponent(LeaveType))
                        .addGap(69, 69, 69)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout leave_applicationLayout = new javax.swing.GroupLayout(leave_application);
        leave_application.setLayout(leave_applicationLayout);
        leave_applicationLayout.setHorizontalGroup(
            leave_applicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leave_applicationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(667, 667, 667))
            .addGroup(leave_applicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(leave_applicationLayout.createSequentialGroup()
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(63, 63, 63)))
        );
        leave_applicationLayout.setVerticalGroup(
            leave_applicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leave_applicationLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(leave_applicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(leave_applicationLayout.createSequentialGroup()
                    .addGap(8, 8, 8)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(616, Short.MAX_VALUE)))
        );

        jPanel4.add(leave_application, "card2");

        attendance.setBackground(new java.awt.Color(204, 204, 204));

        jPanel12.setBackground(new java.awt.Color(204, 212, 207));

        date_today1.setText("Friday, November 21, 2025");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 145, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(date_today1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(date_today1)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel15.setBackground(new java.awt.Color(204, 204, 204));

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel7.setText("Recent Punch in / Punch outs");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Date", "Punch In Time", "Punch Out Time"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton14.setText("Search");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("Clear");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jButton14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton15))
                            .addComponent(attendanceSearchField))
                        .addGap(37, 37, 37))))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(attendanceSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 29, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton14)
                                    .addComponent(jButton15))))
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout attendanceLayout = new javax.swing.GroupLayout(attendance);
        attendance.setLayout(attendanceLayout);
        attendanceLayout.setHorizontalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(attendanceLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        attendanceLayout.setVerticalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(attendance, "card3");

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel1.setText("Add New Employees");

        card56.setBackground(new java.awt.Color(234, 234, 234));

        jLabel17.setText("Full Name:");

        jLabel18.setText("UserName:");

        jLabel19.setText("Department:");

        jLabel20.setText("Phone Number:");

        jLabel21.setText("Address:");

        jLabel22.setText("Email:");

        jLabel23.setText("Password:");

        jLabel24.setText("Employee Type:");

        Full_Name.setToolTipText("");

        Username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameActionPerformed(evt);
            }
        });

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Human Resource", "IT", "Sales", "Marketing", "Accounts" }));

        Email.setToolTipText("");

        Employee_Type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Remote", "Hybrid", "On-Site" }));

        Add_Employee_btn.setText("Add Employee");

        javax.swing.GroupLayout card56Layout = new javax.swing.GroupLayout(card56);
        card56.setLayout(card56Layout);
        card56Layout.setHorizontalGroup(
            card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card56Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(card56Layout.createSequentialGroup()
                        .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(card56Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(65, 65, 65)
                                .addComponent(Address))
                            .addGroup(card56Layout.createSequentialGroup()
                                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel17))
                                .addGap(46, 46, 46)
                                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox7, 0, 183, Short.MAX_VALUE)
                                    .addComponent(Username)
                                    .addComponent(Full_Name))))
                        .addGap(172, 172, 172)
                        .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(card56Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(Employee_Type, 0, 188, Short.MAX_VALUE))
                            .addGroup(card56Layout.createSequentialGroup()
                                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel23))
                                .addGap(51, 51, 51)
                                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Email, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                    .addComponent(Password)))))
                    .addGroup(card56Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(28, 28, 28)
                        .addComponent(Phone_Number, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
            .addGroup(card56Layout.createSequentialGroup()
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(card56Layout.createSequentialGroup()
                        .addGap(357, 357, 357)
                        .addComponent(Add_Employee_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(card56Layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(EmpErrorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        card56Layout.setVerticalGroup(
            card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(card56Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel22)
                    .addComponent(Full_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel23)
                    .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel24)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Employee_Type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(Phone_Number, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(card56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(EmpErrorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(Add_Employee_btn)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout Add_EmployeesLayout = new javax.swing.GroupLayout(Add_Employees);
        Add_Employees.setLayout(Add_EmployeesLayout);
        Add_EmployeesLayout.setHorizontalGroup(
            Add_EmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_EmployeesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_EmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Add_EmployeesLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(card56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        Add_EmployeesLayout.setVerticalGroup(
            Add_EmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_EmployeesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(47, 47, 47)
                .addComponent(card56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel4.add(Add_Employees, "Add_Employees");

        jTable2.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Full Name", "Username", "Email", "Phone", "Department", "Employee Type", "Address"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jButton8.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        jButton8.setText("Display Employees");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort by Name (A–Z)", "Sort by Department (A–Z)", "Sort by Address (A–Z)" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton9.setText("Clear ");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton12.setText("Search");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 868, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jButton8)
                .addGap(97, 97, 97)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jButton9))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton12))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel5, "card6");

        jLabel31.setFont(new java.awt.Font("Hiragino Maru Gothic ProN", 0, 36)); // NOI18N
        jLabel31.setText("Profile Feature Coming Soon...");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(160, Short.MAX_VALUE)
                .addComponent(jLabel31)
                .addGap(155, 155, 155))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(jLabel31)
                .addContainerGap(423, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel16, "card5");

        jPanel1.add(jPanel4);
        jPanel4.setBounds(280, 100, 860, 580);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1144, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
                                       
    java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
    cl.show(jPanel4, "card4");   // show the dashboard (jPanel7)
    updatePresentCountLabel();
    updateTotalEmployeesLabel();
    updateTotalPendingLeavesLabel();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
        cl.show(jPanel4, "card2");
        refreshLeaveApplicationsTable();
        loadNextPendingLeaveToApproveSection();
        updateTotalPendingLeavesLabel();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
        cl.show(jPanel4, "card5");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
        cl.show(jPanel4, "card3"); 
        refreshAttendanceTable();
        // refreshAttendanceTable() already updates present count, but keep it explicit
        updatePresentCountLabel();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
           java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
        cl.show(jPanel4, "card6"); 
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
                                           
    java.awt.CardLayout cl = (java.awt.CardLayout) jPanel4.getLayout();
    cl.show(jPanel4, "Add_Employees");   // show the dashboard (jPanel7)
    }//GEN-LAST:event_jButton6ActionPerformed

    private void UsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UsernameActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
     this.dispose();

    login_form lf = new login_form();
    AdminModel model = new AdminModel();   // or reuse shared one if you store it
    new LoginController(lf, model);

    lf.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // REJECT: remove the request completely so the row disappears from the table
        if (currentPendingLeave == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No pending leave request selected.",
                    "Reject",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Optional: store admin remark before deleting (not necessary since we delete)
        // String remarks = jTextArea2.getText().trim();
        // AdminModel.updateLeaveDecision(currentPendingLeave, "Rejected", remarks);

        boolean removed = LeaveController.removeLeaveRequestFromQueue(currentPendingLeave);
        if (!removed) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not remove the leave request (maybe already processed).",
                    "Reject",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentPendingLeave = null;
        refreshLeaveApplicationsTable();
        loadNextPendingLeaveToApproveSection();
        updateTotalPendingLeavesLabel();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        openAssignDialogForEmployee("sridhar1");
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        openAssignDialogForEmployee("sachin1");
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        openAssignDialogForEmployee("rabin1");
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        openAssignDialogForEmployee("sanskar1");
    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        openAssignDialogForEmployee("Oscar");
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        openAssignDialogForEmployee("ram1");
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    
  String choice = String.valueOf(jComboBox1.getSelectedItem());

    // Ask controller: load employees from file + sort manually based on combo choice
    List<Employee> sortedEmployees = employeeSortController.getEmployeesSorted(choice);

    // Keep cache (useful for search/filter)
    employeeCache = new LinkedList<>(sortedEmployees);

    // Render in table
    renderEmployeesToTable(employeeCache);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        
    jTextField1.setText("");


    renderEmployeesToTable(employeeCache);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
  String key = jTextField1.getText();

// ALWAYS sort first
List<Employee> sorted =
    employeeSortController.getEmployeesSorted("Sort by Name (A–Z)");

List<Employee> result =
    employeeSortController.binarySearchByName(sorted, key);

renderEmployeesToTable(result);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
      
String key = attendanceSearchField.getText();
List<AttendanceRecord> result =
    AttendanceController.linearSearchAttendance(attendanceCache, key);

renderAttendanceToTable(result);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
     attendanceSearchField.setText("");
renderAttendanceToTable(attendanceFullCache);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
             // APPROVE: update status/remarks but keep the request in the table
        if (currentPendingLeave == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No pending leave request selected.",
                    "Approve",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String remarks = jTextArea2.getText() == null ? "" : jTextArea2.getText().trim();
        boolean ok = LeaveController.updateLeaveDecision(currentPendingLeave, "Approved", remarks);

        if (!ok) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Could not approve the leave request.",
                    "Approve",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Move to next pending request
        refreshLeaveApplicationsTable();
        loadNextPendingLeaveToApproveSection();
        updateTotalPendingLeavesLabel();
    }//GEN-LAST:event_jButton10ActionPerformed


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new homepage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Add_Employee_btn;
    private javax.swing.JPanel Add_Employees;
    private javax.swing.JTextField Address;
    private javax.swing.JLabel CurrentPresent;
    private javax.swing.JLabel CurrentPresentCount;
    private javax.swing.JTextField Email;
    private javax.swing.JLabel EmpErrorLabel;
    private javax.swing.JComboBox<String> Employee_Type;
    private javax.swing.JLabel FromDate;
    private javax.swing.JTextField Full_Name;
    private javax.swing.JLabel LeaveType;
    private javax.swing.JLabel Name;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Phone_Number;
    private javax.swing.JLabel ToDate;
    private javax.swing.JLabel TotalEmployees;
    private javax.swing.JLabel TotalPendingLeaves;
    private javax.swing.JTextField Username;
    private javax.swing.JPanel attendance;
    private javax.swing.JTextField attendanceSearchField;
    private javax.swing.JPanel card56;
    private javax.swing.JLabel date_today;
    private javax.swing.JLabel date_today1;
    private javax.swing.JLabel date_today2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel leave_application;
    private javax.swing.JTable leave_application_table;
    private javax.swing.JLabel user_greet;
    // End of variables declaration//GEN-END:variables
// === getters for Add Employee form ===
public String getEmpFullName() {
    return Full_Name.getText().trim();
}

public String getEmpUsername() {
    return Username.getText().trim();
}

public String getEmpEmail() {
    return Email.getText().trim();
}

public String getEmpPhone() {
    return Phone_Number.getText().trim();
}

public String getEmpDepartment() {
    return jComboBox7.getSelectedItem().toString();
}

public String getEmpAddress() {
    return Address.getText().trim();
}

public String getEmpType(){
    return Employee_Type.getSelectedItem().toString();
}

public String getEmpPassword() {
    return new String(Password.getPassword()).trim();
}
public void addEmployeeRegisterListener(java.awt.event.ActionListener l) {
    Add_Employee_btn.addActionListener(l);
}
public void showEmpError(String msg) {
    EmpErrorLabel.setText(msg);
    EmpErrorLabel.setVisible(true);
}

public void clearEmpError() {
    EmpErrorLabel.setText("");
    EmpErrorLabel.setVisible(false);
}
public String getEmpEmployeeType() {
    return Employee_Type.getSelectedItem().toString().trim();
}

    // Try to read a 0-10 rating from AssignTaskDialog WITHOUT assuming exact method names.
    // Returns null if no rating is available/selected.
    private Integer tryGetRatingFromDialog(Object dlg) {
        if (dlg == null) return null;

        // 1) First try common getter method names (if you add them later)
        String[] methodNames = {
            "getSelectedRating0to10",
            "getRatingValue",
            "getRating",
            "getWeeklyRating"
        };

        for (String m : methodNames) {
            try {
                java.lang.reflect.Method mm = dlg.getClass().getMethod(m);
                Object val = mm.invoke(dlg);
                if (val == null) continue;

                if (val instanceof Integer) {
                    int r = (Integer) val;
                    if (r >= 0 && r <= 10) return r;
                }

                if (val instanceof Number) {
                    int r = ((Number) val).intValue();
                    if (r >= 0 && r <= 10) return r;
                }

                if (val instanceof String) {
                    String s = ((String) val).trim();
                    if (s.isEmpty()) continue;
                    int r = Integer.parseInt(s);
                    if (r >= 0 && r <= 10) return r;
                }
            } catch (NoSuchMethodException ignore) {
                // try next
            } catch (Exception ex) {
                // ignore and fallback
            }
        }

        // 2) Fallback: try to locate rating UI directly via reflection (scrollpane or direct component)
        try {
            // Helper to parse to int 0-10
            java.util.function.Function<Object, Integer> parse0to10 = (obj) -> {
                if (obj == null) return null;
                try {
                    if (obj instanceof Number n) {
                        int r = n.intValue();
                        return (r >= 0 && r <= 10) ? r : null;
                    }
                    String s = obj.toString().trim();
                    if (s.isEmpty()) return null;
                    int r = Integer.parseInt(s);
                    return (r >= 0 && r <= 10) ? r : null;
                } catch (Exception e) {
                    return null;
                }
            };

            // 2a) Try a JScrollPane field named "rating"
            javax.swing.JScrollPane sp = null;
            try {
                java.lang.reflect.Field f = dlg.getClass().getDeclaredField("rating");
                f.setAccessible(true);
                Object obj = f.get(dlg);
                if (obj instanceof javax.swing.JScrollPane) sp = (javax.swing.JScrollPane) obj;
            } catch (NoSuchFieldException ignore) {
                // not found
            }

            // 2b) If not found, scan all declared fields for a JScrollPane whose name contains "rating"
            if (sp == null) {
                for (java.lang.reflect.Field f : dlg.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    if (javax.swing.JScrollPane.class.isAssignableFrom(f.getType())) {
                        String name = f.getName() == null ? "" : f.getName().toLowerCase();
                        Object obj = f.get(dlg);
                        if (obj instanceof javax.swing.JScrollPane jScrollPane) {
                            if (name.contains("rating")) {
                                sp = jScrollPane;
                                break;
                            }
                            // keep first scrollpane as a last resort
                            if (sp == null) sp = jScrollPane;
                        }
                    }
                }
            }

            // 2c) If we have a scrollpane, try to read from its viewport view
            if (sp != null) {
                java.awt.Component view = sp.getViewport().getView();
                // Sometimes viewport view can be a JPanel holding the real control
                if (view instanceof javax.swing.JPanel panel) {
                    for (java.awt.Component c : panel.getComponents()) {
                        view = c;
                        break;
                    }
                }

                if (view instanceof javax.swing.JList<?> list) {
                    return parse0to10.apply(list.getSelectedValue());
                }
                if (view instanceof javax.swing.JSpinner spinner) {
                    return parse0to10.apply(spinner.getValue());
                }
                if (view instanceof javax.swing.JSlider slider) {
                    return parse0to10.apply(slider.getValue());
                }
                if (view instanceof javax.swing.JComboBox<?> combo) {
                    return parse0to10.apply(combo.getSelectedItem());
                }
            }

            // 2d) As a final fallback, scan all declared fields for a rating-like component and read it
            for (java.lang.reflect.Field f : dlg.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object obj = f.get(dlg);

                if (obj instanceof javax.swing.JList<?> list) {
                    Integer r = parse0to10.apply(list.getSelectedValue());
                    if (r != null) return r;
                }
                if (obj instanceof javax.swing.JSpinner spinner) {
                    Integer r = parse0to10.apply(spinner.getValue());
                    if (r != null) return r;
                }
                if (obj instanceof javax.swing.JSlider slider) {
                    Integer r = parse0to10.apply(slider.getValue());
                    if (r != null) return r;
                }
                if (obj instanceof javax.swing.JComboBox<?> combo) {
                    Integer r = parse0to10.apply(combo.getSelectedItem());
                    if (r != null) return r;
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            // Any reflection/parse issues: treat as not available
        }

        return null;
    }

    // Try to save rating into AdminModel WITHOUT assuming exact method names.
    private void trySaveRatingToModel(String username, int rating) {
        if (username == null || username.trim().isEmpty()) return;
        if (rating < 0 || rating > 10) return;

        // Common method name candidates we might add in AdminModel later
        String[] methodNames = {
            "saveWeeklyRating",
            "saveRating",
            "saveEmployeeRating",
            "saveTasksRemarksAndRating" // in case you combine later
        };

        for (String m : methodNames) {
            try {
                java.lang.reflect.Method mm = AdminModel.class.getMethod(m, String.class, int.class);
                mm.invoke(null, username, rating);
                return; // saved successfully
            } catch (NoSuchMethodException ignore) {
                // try next
            } catch (IllegalAccessException | SecurityException | InvocationTargetException ex) {
                return; // method exists but failed; stop
            }
        }
    }

    
    public void openAssignDialogForEmployee(String username) {
        java.awt.Frame parent = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        AssignTaskDialog dlg = new AssignTaskDialog(parent, true);
        dlg.setEmployee(username);

        // Prefill from AdminModel (if any existing data)
        dlg.loadExistingData(adminModel);

        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            // Require rating (0-10) before allowing submission
            Integer ratingValue = tryGetRatingFromDialog(dlg);
            if (ratingValue == null) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Please select a rating (0–10) before submitting the weekly review.",
                        "Rating Required",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            java.util.List<String> tasks = dlg.getTasks();
            String remarks = dlg.getWeeklyRemarks();

            AdminModel.saveTasksAndRemarks(username, tasks, remarks);
            trySaveRatingToModel(username, ratingValue);
        }
    }
}

