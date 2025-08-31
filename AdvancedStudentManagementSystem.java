import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.RowFilter;
public class AdvancedStudentManagementSystem {
    static class Student {
        String roll, name, dept, year, collegeEmail, personalEmail;
        int attendance; 
        double cgpa;   
        String fees;    
        Student(String roll, String name, String dept, String year, String collegeEmail,
             String personalEmail, String attendance, String cgpa, String fees) {
            this.roll = roll; this.name = name; this.dept = dept; this.year = year;
            this.collegeEmail = collegeEmail; this.personalEmail = personalEmail;
            this.attendance = parsePercent(attendance);
            this.cgpa = Double.parseDouble(cgpa);
            this.fees = fees;
        }
        static int parsePercent(String s){
            s = s.trim().replace("%","");
            try { return Math.min(100, Math.max(0, Integer.parseInt(s))); } catch(Exception e){return 0;}
        }
        Object[] toRow(){
            return new Object[]{roll, name, dept, year, collegeEmail, personalEmail, attendance+"%", cgpa, fees};
        }
    }

    // --- Data ---
    private static final List<Student> students = new ArrayList<>();
    private static final String[] COLS = {"Roll No","Name","Department","Year","College Email","Personal Email","Attendance","CGPA","Fees"};

    // --- UI State ---
    private static JFrame frame;
    private static JTextField userField; private static JPasswordField passField; private static JComboBox<String> roleBox;
    private static JCheckBox darkToggle; private static JPanel root;

    // Admin tables & helpers
    private static JTable studentTable; private static DefaultTableModel studentModel; private static TableRowSorter<DefaultTableModel> sorter;
    private static JTextField searchField; private static JComboBox<String> deptFilter; private static JComboBox<String> yearFilter; private static JComboBox<String> feeFilter;

    // Logged in user cache
    private static Student loggedStudent = null; private static String loggedRole = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            seed();
            showLogin();
        });
    }

    private static void seed(){
        String[][] base = {
                
        {"101","Akila","AI&DS","II","akila@clg.com","akila@gmail.com","92%","9","Paid"},
        {"102","Ramesh","CSE","II","ramesh@clg.com","ramesh@gmail.com","80%","7","Unpaid"},
        {"103","Kavya","ECE","IV","kavya@clg.com","kavya@gmail.com","88%","8","Paid"},
        {"104","Priya","EEE","III","priya@clg.com","priya@gmail.com","95%","10","Paid"},
        {"105","Suresh","MECH","II","suresh@clg.com","suresh@gmail.com","70%","6","Unpaid"},
        {"106","Deepa","CIVIL","I","deepa@clg.com","deepa@gmail.com","85%","8","Paid"},
        {"107","Vishal","CSE","III","vishal@clg.com","vishal@gmail.com","78%","7","Paid"},
        {"108","Nandhini","AI&DS","II","nandhini@clg.com","nandhini@gmail.com","90%","9","Paid"},
        {"109","Arun","ECE","I","arun@clg.com","arun@gmail.com","60%","5","Unpaid"},
        {"110","Meena","EEE","IV","meena@clg.com","meena@gmail.com","83%","8","Paid"},
        {"111","Ravi","MECH","III","ravi@clg.com","ravi@gmail.com","72%","6","Unpaid"},
        {"112","Shalini","CIVIL","II","shalini@clg.com","shalini@gmail.com","87%","9","Paid"},
        {"113","Karthik","CSE","IV","karthik@clg.com","karthik@gmail.com","91%","10","Paid"},
        {"114","Divya","AI&DS","I","divya@clg.com","divya@gmail.com","89%","9","Paid"},
        {"115","Manoj","ECE","III","manoj@clg.com","manoj@gmail.com","75%","7","Unpaid"},
        {"116","Aishwarya","EEE","II","aish@clg.com","aish@gmail.com","94%","10","Paid"},
        {"117","Harish","MECH","I","harish@clg.com","harish@gmail.com","65%","6","Unpaid"},
        {"118","Pooja","CIVIL","III","pooja@clg.com","pooja@gmail.com","82%","8","Paid"},
        {"119","Vignesh","CSE","II","vignesh@clg.com","vignesh@gmail.com","76%","7","Paid"},
        {"120","Sneha","AI&DS","IV","sneha@clg.com","sneha@gmail.com","97%","10","Paid"},

        {"121","Rahul","ECE","II","rahul@clg.com","rahul@gmail.com","84%","8","Paid"},
        {"122","Lavanya","EEE","III","lavanya@clg.com","lavanya@gmail.com","79%","7","Unpaid"},
        {"123","Prakash","MECH","IV","prakash@clg.com","prakash@gmail.com","88%","8","Paid"},
        {"124","Monika","CIVIL","I","monika@clg.com","monika@gmail.com","93%","10","Paid"},
        {"125","Gokul","CSE","II","gokul@clg.com","gokul@gmail.com","81%","7","Unpaid"},
        {"126","Anitha","AI&DS","III","anitha@clg.com","anitha@gmail.com","89%","9","Paid"},
        {"127","Sanjay","ECE","I","sanjay@clg.com","sanjay@gmail.com","67%","6","Unpaid"},
        {"128","Keerthi","EEE","IV","keerthi@clg.com","keerthi@gmail.com","85%","8","Paid"},
        {"129","Dinesh","MECH","III","dinesh@clg.com","dinesh@gmail.com","74%","6","Unpaid"},
        {"130","Shreya","CIVIL","II","shreya@clg.com","shreya@gmail.com","91%","9","Paid"},
        {"131","Varun","CSE","IV","varun@clg.com","varun@gmail.com","90%","10","Paid"},
        {"132","Ishita","AI&DS","I","ishita@clg.com","ishita@gmail.com","87%","9","Paid"},
        {"133","Aravind","ECE","III","aravind@clg.com","aravind@gmail.com","77%","7","Unpaid"},
        {"134","Swathi","EEE","II","swathi@clg.com","swathi@gmail.com","95%","10","Paid"},
        {"135","Naveen","MECH","I","naveen@clg.com","naveen@gmail.com","69%","6","Unpaid"},
        {"136","Revathi","CIVIL","III","revathi@clg.com","revathi@gmail.com","84%","8","Paid"},
        {"137","Ajay","CSE","II","ajay@clg.com","ajay@gmail.com","79%","7","Paid"},
        {"138","Janani","AI&DS","IV","janani@clg.com","janani@gmail.com","96%","10","Paid"},
        {"139","Balaji","ECE","I","balaji@clg.com","balaji@gmail.com","62%","5","Unpaid"},
        {"140","Krithika","EEE","IV","krithika@clg.com","krithika@gmail.com","86%","8","Paid"},
        {"141","Surya","MECH","III","surya@clg.com","surya@gmail.com","73%","6","Unpaid"},
        {"142","Gayathri","CIVIL","II","gayathri@clg.com","gayathri@gmail.com","89%","9","Paid"},
        {"143","Lokesh","CSE","IV","lokesh@clg.com","lokesh@gmail.com","92%","10","Paid"},
        {"144","Preethi","AI&DS","I","preethi@clg.com","preethi@gmail.com","88%","9","Paid"},
        {"145","Sathish","ECE","III","sathish@clg.com","sathish@gmail.com","76%","7","Unpaid"},
        {"146","Bhavana","EEE","II","bhavana@clg.com","bhavana@gmail.com","93%","10","Paid"},
        {"147","Mohan","MECH","I","mohan@clg.com","mohan@gmail.com","68%","6","Unpaid"},
        {"148","Sandhya","CIVIL","III","sandhya@clg.com","sandhya@gmail.com","83%","8","Paid"},
        {"149","Yogesh","CSE","II","yogesh@clg.com","yogesh@gmail.com","75%","7","Paid"},
        {"150","Anjali","AI&DS","IV","anjali@clg.com","anjali@gmail.com","98%","10","Paid"}
    };

        for(String[] s: base) students.add(new Student(s[0],s[1],s[2],s[3],s[4],s[5],s[6],s[7],s[8]));
    }

    // --- Login ---
    private static void showLogin(){
        frame = new JFrame("Student Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 360);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16,16,16,16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login Portal", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(title, gbc);

        gbc.gridwidth=1; gbc.gridy++;
        panel.add(new JLabel("Role:"), gbc);
        roleBox = new JComboBox<>(new String[]{"Admin","Teacher","Student"});
        gbc.gridx=1; panel.add(roleBox, gbc);

        gbc.gridx=0; gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        userField = new JTextField(15); gbc.gridx=1; panel.add(userField, gbc);

        gbc.gridx=0; gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        passField = new JPasswordField(15); gbc.gridx=1; panel.add(passField, gbc);

        // simple CAPTCHA
        int a = new Random().nextInt(9)+1, b = new Random().nextInt(9)+1;
        String captchaQ = "What is "+a+" + "+b+"?";
        JTextField captchaAns = new JTextField();
        gbc.gridx=0; gbc.gridy++; panel.add(new JLabel(captchaQ), gbc);
        gbc.gridx=1; panel.add(captchaAns, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton loginBtn = new JButton("Login");
        JButton resetBtn = new JButton("Reset");
        darkToggle = new JCheckBox("Dark mode");
        actions.add(darkToggle); actions.add(resetBtn); actions.add(loginBtn);
        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2; panel.add(actions, gbc);

        loginBtn.addActionListener(e -> {
            String role = Objects.toString(roleBox.getSelectedItem(), "");
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if(!Objects.equals(Integer.toString(a+b), captchaAns.getText().trim())){
                JOptionPane.showMessageDialog(frame, "Captcha incorrect");
                return;
            }
            switch (role){
                case "Admin":
                    if(username.equalsIgnoreCase("admin") && password.equals("admin123")){
                        loggedRole = "Admin"; frame.dispose(); showAdminDashboard();
                    } else JOptionPane.showMessageDialog(frame, "Invalid Admin credentials");
                    break;
                case "Teacher":
                    if(password.equals("teacher123")){
                        loggedRole = "Teacher"; frame.dispose(); showTeacherDashboard(username);
                    } else JOptionPane.showMessageDialog(frame, "Invalid Teacher password");
                    break;
                default:
                    if(password.equals("student123")){
                        Student s = findByName(username);
                        if(s!=null){ loggedStudent = s; loggedRole = "Student"; frame.dispose(); showStudentDashboard(); }
                        else JOptionPane.showMessageDialog(frame, "Student not found");
                    } else JOptionPane.showMessageDialog(frame, "Invalid Student password");
            }
        });
        resetBtn.addActionListener(e -> { userField.setText(""); passField.setText(""); });
        darkToggle.addActionListener(e -> applyDarkMode(panel, darkToggle.isSelected()));

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private static Student findByName(String name){
        for(Student s: students) if(s.name.equalsIgnoreCase(name)) return s;
        return null;
    }

    private static void applyDarkMode(JComponent root, boolean dark){
        Color bg = dark ? new Color(30,30,30) : new Color(240,245,255);
        Color fg = dark ? new Color(230,230,230) : Color.DARK_GRAY;
        SwingUtilities.invokeLater(() -> {
            for(Window w: Window.getWindows()){
                setColors((Container) w, bg, fg);
            }
        });
        root.setBackground(bg);
    }
    private static void setColors(Container c, Color bg, Color fg){
        c.setBackground(bg);
        for(Component comp: c.getComponents()){
            comp.setBackground(bg);
            comp.setForeground(fg);
            if(comp instanceof JTable){
                JTable t = (JTable) comp; t.getTableHeader().setBackground(bg.darker()); t.getTableHeader().setForeground(fg);
            }
            if(comp instanceof Container) setColors((Container) comp, bg, fg);
        }
    }

    // --- Admin Dashboard ---
    private static void showAdminDashboard(){
        JFrame admin = new JFrame("Admin Dashboard");
        admin.setSize(1100, 600); admin.setLocationRelativeTo(null);
        admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        admin.setLayout(new BorderLayout(8,8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", buildStudentsTab());
        tabs.addTab("Attendance", buildAttendanceTab());
        tabs.addTab("Fees", buildFeesTab());
        tabs.addTab("Reports", buildReportsTab());

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Admin • Manage Students / Attendance / Fees", SwingConstants.LEFT);
        title.setBorder(new EmptyBorder(8,12,8,12));
        top.add(title, BorderLayout.WEST);

        JButton exportBtn = new JButton("Export CSV");
        exportBtn.addActionListener(e -> exportCSV(admin));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { admin.dispose(); showLogin(); });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8)); right.add(exportBtn); right.add(logout);
        top.add(right, BorderLayout.EAST);

        admin.add(top, BorderLayout.NORTH);
        admin.add(tabs, BorderLayout.CENTER);
        admin.setVisible(true);
    }

    private static JPanel buildStudentsTab(){
        JPanel panel = new JPanel(new BorderLayout(8,8));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        searchField = new JTextField(18); searchField.putClientProperty("JTextField.placeholderText","Search name/email/roll");
        deptFilter = new JComboBox<>(new String[]{"All","AI&DS","CSE","ECE","EEE","MECH","CIVIL"});
        yearFilter = new JComboBox<>(new String[]{"All","I","II","III","IV"});
        feeFilter = new JComboBox<>(new String[]{"All","Paid","Unpaid"});
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        toolbar.add(new JLabel("Search:")); toolbar.add(searchField);
        toolbar.add(new JLabel("Dept:")); toolbar.add(deptFilter);
        toolbar.add(new JLabel("Year:")); toolbar.add(yearFilter);
        toolbar.add(new JLabel("Fees:")); toolbar.add(feeFilter);
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(delBtn);

        // Table
        studentModel = new DefaultTableModel(COLS, 0){ public boolean isCellEditable(int r,int c){ return false; } };
        reloadStudentModel();
        studentTable = new JTable(studentModel);
        studentTable.setFillsViewportHeight(true);
        studentTable.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(studentModel);
        studentTable.setRowSorter(sorter);

        // Search & filter behavior
        DocumentListener dl = new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ applyFilters(); }
            public void removeUpdate(DocumentEvent e){ applyFilters(); }
            public void changedUpdate(DocumentEvent e){ applyFilters(); }
        };
        searchField.getDocument().addDocumentListener(dl);
        ActionListener fl = e -> applyFilters();
        deptFilter.addActionListener(fl); yearFilter.addActionListener(fl); feeFilter.addActionListener(fl);

        addBtn.addActionListener(e -> openStudentDialog(null));
        editBtn.addActionListener(e -> {
            int r = studentTable.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(panel,"Select a row to edit"); return; }
            int modelRow = studentTable.convertRowIndexToModel(r);
            openStudentDialog(modelRow);
        });
        delBtn.addActionListener(e -> deleteSelected());

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return panel;
    }

    private static void reloadStudentModel(){
        studentModel.setRowCount(0);
        for(Student s: students) studentModel.addRow(s.toRow());
    }

    private static void applyFilters(){
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        String q = searchField.getText().trim();
        if(!q.isEmpty()){
            RowFilter<Object,Object> rf = RowFilter.regexFilter("(?i)"+Pattern.quote(q));
            filters.add(rf);
        }
        if(!Objects.equals(deptFilter.getSelectedItem(), "All")){
            filters.add(RowFilter.regexFilter("^"+deptFilter.getSelectedItem()+"$", 2));
        }
        if(!Objects.equals(yearFilter.getSelectedItem(), "All")){
            filters.add(RowFilter.regexFilter("^"+yearFilter.getSelectedItem()+"$", 3));
        }
        if(!Objects.equals(feeFilter.getSelectedItem(), "All")){
            filters.add(RowFilter.regexFilter("^"+feeFilter.getSelectedItem()+"$", 8));
        }
        RowFilter<Object,Object> combo = filters.isEmpty()? null : RowFilter.andFilter(filters);
        sorter.setRowFilter(combo);
    }

    private static void openStudentDialog(Integer modelRow){
        boolean editing = modelRow!=null;
        JTextField roll = new JTextField(editing? Objects.toString(studentModel.getValueAt(modelRow,0)): "");
        JTextField name = new JTextField(editing? Objects.toString(studentModel.getValueAt(modelRow,1)): "");
        JComboBox<String> dept = new JComboBox<>(new String[]{"AI&DS","CSE","ECE","EEE","MECH","CIVIL"});
        if(editing) dept.setSelectedItem(Objects.toString(studentModel.getValueAt(modelRow,2)));
        JComboBox<String> year = new JComboBox<>(new String[]{"I","II","III","IV"});
        if(editing) year.setSelectedItem(Objects.toString(studentModel.getValueAt(modelRow,3)));
        JTextField cMail = new JTextField(editing? Objects.toString(studentModel.getValueAt(modelRow,4)): "");
        JTextField pMail = new JTextField(editing? Objects.toString(studentModel.getValueAt(modelRow,5)): "");
        JSpinner attendance = new JSpinner(new SpinnerNumberModel(editing? Student.parsePercent(Objects.toString(studentModel.getValueAt(modelRow,6))) : 75, 0, 100, 1));
        JSpinner cgpa = new JSpinner(new SpinnerNumberModel(editing? Double.parseDouble(Objects.toString(studentModel.getValueAt(modelRow,7))) : 7.5, 0.0, 10.0, 0.1));
        JComboBox<String> fees = new JComboBox<>(new String[]{"Paid","Unpaid"});
        if(editing) fees.setSelectedItem(Objects.toString(studentModel.getValueAt(modelRow,8)));

        JPanel form = new JPanel(new GridLayout(0,2,8,8));
        form.add(new JLabel("Roll No:")); form.add(roll);
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Department:")); form.add(dept);
        form.add(new JLabel("Year:")); form.add(year);
        form.add(new JLabel("College Email:")); form.add(cMail);
        form.add(new JLabel("Personal Email:")); form.add(pMail);
        form.add(new JLabel("Attendance %:")); form.add(attendance);
        form.add(new JLabel("CGPA:")); form.add(cgpa);
        form.add(new JLabel("Fees:")); form.add(fees);

        int res = JOptionPane.showConfirmDialog(null, form, editing?"Edit Student":"Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(res==JOptionPane.OK_OPTION){
            if(roll.getText().trim().isEmpty() || name.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(null, "Roll and Name are required"); return;
            }
            Student s = new Student(roll.getText().trim(), name.getText().trim(), Objects.toString(dept.getSelectedItem()),
                    Objects.toString(year.getSelectedItem()), cMail.getText().trim(), pMail.getText().trim(),
                    attendance.getValue().toString()+"%", cgpa.getValue().toString(), Objects.toString(fees.getSelectedItem()));
            if(editing){
                // update list
                String targetRoll = Objects.toString(studentModel.getValueAt(modelRow,0));
                for(int i=0;i<students.size();i++) if(students.get(i).roll.equals(targetRoll)){ students.set(i, s); break; }
            } else {
                // prevent duplicate roll
                for(Student st: students) if(st.roll.equals(s.roll)){ JOptionPane.showMessageDialog(null,"Roll no already exists"); return; }
                students.add(s);
            }
            reloadStudentModel();
            applyFilters();
        }
    }

    private static void deleteSelected(){
        int r = studentTable.getSelectedRow(); if(r<0){ JOptionPane.showMessageDialog(null,"Select a row to delete"); return; }
        int modelRow = studentTable.convertRowIndexToModel(r);
        String roll = Objects.toString(studentModel.getValueAt(modelRow,0));
        int confirm = JOptionPane.showConfirmDialog(null, "Delete student "+roll+"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm==JOptionPane.YES_OPTION){
            students.removeIf(s -> s.roll.equals(roll));
            reloadStudentModel();
            applyFilters();
        }
    }

    private static JPanel buildAttendanceTab(){
        JPanel panel = new JPanel(new BorderLayout(8,8));
        String[] cols = {"Roll","Name","Dept","Year","Attendance %"};
        DefaultTableModel m = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){ return c==4; } };
        for(Student s: students) m.addRow(new Object[]{s.roll,s.name,s.dept,s.year,s.attendance});
        JTable t = new JTable(m);
        t.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        JButton save = new JButton("Save Attendance");
        save.addActionListener(e -> {
            for(int i=0;i<m.getRowCount();i++){
                String roll = Objects.toString(m.getValueAt(i,0));
                int att = Integer.parseInt(Objects.toString(m.getValueAt(i,4)));
                for(Student s: students) if(s.roll.equals(roll)){ s.attendance = att; break; }
            }
            reloadStudentModel();
            JOptionPane.showMessageDialog(panel, "Attendance saved");
        });
        panel.add(save, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildFeesTab(){
        JPanel panel = new JPanel(new BorderLayout(8,8));
        String[] cols = {"Roll","Name","Dept","Year","Fees"};
        DefaultTableModel m = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){ return c==4; } };
        for(Student s: students) m.addRow(new Object[]{s.roll,s.name,s.dept,s.year,s.fees});
        JTable t = new JTable(m);
        JComboBox<String> feeEditor = new JComboBox<>(new String[]{"Paid","Unpaid"});
        t.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(feeEditor));
        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        JButton save = new JButton("Save Fees");
        save.addActionListener(e -> {
            for(int i=0;i<m.getRowCount();i++){
                String roll = Objects.toString(m.getValueAt(i,0));
                String fee = Objects.toString(m.getValueAt(i,4));
                for(Student s: students) if(s.roll.equals(roll)){ s.fees = fee; break; }
            }
            reloadStudentModel();
            JOptionPane.showMessageDialog(panel, "Fees status updated");
        });
        panel.add(save, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildReportsTab(){
        JPanel panel = new JPanel(new GridLayout(2,2,12,12));
        panel.setBorder(new EmptyBorder(12,12,12,12));
        panel.add(metric("Total Students", Integer.toString(students.size())));
        long unpaid = students.stream().filter(s -> s.fees.equals("Unpaid")).count();
        panel.add(metric("Unpaid Fees", Long.toString(unpaid)));
        double avgAtt = students.stream().mapToInt(s -> s.attendance).average().orElse(0);
        panel.add(metric("Avg Attendance", String.format(Locale.US, "%.1f%%", avgAtt)));
        double avgCgpa = students.stream().mapToDouble(s -> s.cgpa).average().orElse(0);
        panel.add(metric("Avg CGPA", String.format(Locale.US, "%.2f", avgCgpa)));
        return panel;
    }
    private static JComponent metric(String title, String value){
        JPanel card = new JPanel(new BorderLayout()); card.setBorder(new EmptyBorder(12,12,12,12));
        JLabel t = new JLabel(title); t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
        JLabel v = new JLabel(value); v.setFont(v.getFont().deriveFont(Font.BOLD, 28f)); v.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(t, BorderLayout.WEST); card.add(v, BorderLayout.EAST);
        return card;
    }

    private static void exportCSV(Component parent){
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export Students to CSV");
        if(fc.showSaveDialog(parent)==JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            if(!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getParentFile(), f.getName()+".csv");
            try(PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))){
                // header
                pw.println(String.join(",", COLS));
                for(Student s: students){
                    pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", s.roll, s.name, s.dept, s.year, s.collegeEmail, s.personalEmail, s.attendance+"%", s.cgpa, s.fees);
                }
                JOptionPane.showMessageDialog(parent, "Exported: "+f.getAbsolutePath());
            } catch(Exception ex){
                JOptionPane.showMessageDialog(parent, "Failed to export: "+ex.getMessage());
            }
        }
    }

    // --- Student Dashboard ---
    private static void showStudentDashboard(){
        JFrame stu = new JFrame("Student Dashboard");
        stu.setSize(560, 360); stu.setLocationRelativeTo(null);
        stu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea details = new JTextArea(); details.setEditable(false);
        JButton editProfile = new JButton("Edit Profile");
        JButton logout = new JButton("Logout");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.add(editProfile); bottom.add(logout);

        Runnable refresh = () -> {
            details.setText("");
            details.append("Welcome "+loggedStudent.name+" ("+loggedStudent.dept+" - Year "+loggedStudent.year+")\n\n");
            details.append("Roll No: "+loggedStudent.roll+"\n");
            details.append("College Email: "+loggedStudent.collegeEmail+"\n");
            details.append("Personal Email: "+loggedStudent.personalEmail+"\n");
            details.append("Attendance: "+loggedStudent.attendance+"%\n");
            details.append("CGPA: "+loggedStudent.cgpa+"/10\n");
            details.append("Fees Status: "+loggedStudent.fees+"\n");
        };
        refresh.run();

        editProfile.addActionListener(e -> {
            JTextField pMail = new JTextField(loggedStudent.personalEmail);
            int res = JOptionPane.showConfirmDialog(stu, pMail, "Update Personal Email", JOptionPane.OK_CANCEL_OPTION);
            if(res==JOptionPane.OK_OPTION){ loggedStudent.personalEmail = pMail.getText().trim(); reloadStudentModel(); refresh.run(); }
        });
        logout.addActionListener(e -> { stu.dispose(); showLogin(); });

        stu.add(new JScrollPane(details), BorderLayout.CENTER);
        stu.add(bottom, BorderLayout.SOUTH);
        stu.setVisible(true);
    }

    // --- Teacher Dashboard ---
    private static void showTeacherDashboard(String teacherName){
        JFrame t = new JFrame("Teacher Dashboard - "+teacherName);
        t.setSize(900, 520); t.setLocationRelativeTo(null); t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        t.setLayout(new BorderLayout(8,8));

        String[] cols = {"Roll","Name","Dept","Year","Attendance %","CGPA","Fees"};
        DefaultTableModel m = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){ return c==4 || c==5 || c==6; } };
        for(Student s: students) m.addRow(new Object[]{s.roll,s.name,s.dept,s.year,s.attendance,s.cgpa,s.fees});
        JTable table = new JTable(m);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        top.add(new JLabel("You can edit Attendance, CGPA, Fees"));

        JButton save = new JButton("Save Changes");
        JButton logout = new JButton("Logout");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8)); bottom.add(save); bottom.add(logout);

        save.addActionListener(e -> {
            for(int i=0;i<m.getRowCount();i++){
                String roll = Objects.toString(m.getValueAt(i,0));
                int att = Integer.parseInt(Objects.toString(m.getValueAt(i,4)));
                double cg = Double.parseDouble(Objects.toString(m.getValueAt(i,5)));
                String fee = Objects.toString(m.getValueAt(i,6));
                for(Student s: students) if(s.roll.equals(roll)){ s.attendance = att; s.cgpa = cg; s.fees = fee; }
            }
            reloadStudentModel();
            JOptionPane.showMessageDialog(t, "Saved");
        });
        logout.addActionListener(e -> { t.dispose(); showLogin(); });

        t.add(top, BorderLayout.NORTH);
        t.add(new JScrollPane(table), BorderLayout.CENTER);
        t.add(bottom, BorderLayout.SOUTH);
        t.setVisible(true);
    }
}