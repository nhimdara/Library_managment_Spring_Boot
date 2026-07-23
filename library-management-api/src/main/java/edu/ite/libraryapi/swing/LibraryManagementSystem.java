package edu.ite.libraryapi.swing;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

// --- Database Connection Class ---
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/librarymanagementsystems";
    private static final String USER = "root";
    private static final String PASSWORD = "NewPassword123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Check the project library configuration.", e);
        }
    }
}

// --- Main Application Controller ---
public class LibraryManagementSystem extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Logged in user tracking
    private String currentRole = "";
    private int loggedInId = -1;
    private String loggedInName = "";

    private String getBookStutus(int quantity) {
        return quantity > 0 ? "Available" : "Unavailable";
    }

    private void selectBookStutus(JComboBox<String> cmbStutus, int quantity) {
        cmbStutus.setSelectedItem(getBookStutus(quantity));
    }

    private java.sql.Date getTodayDate() {
        return java.sql.Date.valueOf(LocalDate.now());
    }

    private void updateBookStutusFromQuantityField(JTextField txtQty, JComboBox<String> cmbStutus) {
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            selectBookStutus(cmbStutus, qty);
        } catch (NumberFormatException ignored) {
            cmbStutus.setSelectedItem("Unavailable");
        }
    }

    private boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            pstmt.setString(2, columnName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void ensureBookStutusColumn() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            if (!columnExists(conn, "books", "stutus") && columnExists(conn, "books", "status")) {
                stmt.executeUpdate("UPDATE books SET status = CASE WHEN quantity > 0 THEN 'Available' ELSE 'Unavailable' END");
                stmt.executeUpdate("ALTER TABLE books CHANGE status stutus ENUM('Available','Unavailable') NOT NULL DEFAULT 'Unavailable'");
            } else if (columnExists(conn, "books", "stutus")) {
                stmt.executeUpdate("UPDATE books SET stutus = CASE WHEN quantity > 0 THEN 'Available' ELSE 'Unavailable' END");
                stmt.executeUpdate("ALTER TABLE books MODIFY stutus ENUM('Available','Unavailable') NOT NULL DEFAULT 'Unavailable'");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not update books.stutus column: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    private void logout() {
        currentRole = "";
        loggedInId = -1;
        loggedInName = "";
        cardLayout.show(mainPanel, "LOGIN");
    }

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ensureBookStutusColumn();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Build screens
        mainPanel.add(createLoginPanel(), "LOGIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // --- SCREEN 1: LOGIN PANEL ---
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Library System Portal", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel lblRole = new JLabel("Select Role:");
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"Student", "Admin"});

        JLabel lblUser = new JLabel("Username / Student ID:");
        JTextField txtUser = new JTextField(15);

        JLabel lblPass = new JLabel("Password:");
        JPasswordField txtPass = new JPasswordField(15);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; loginPanel.add(lblRole, gbc);
        gbc.gridx = 1; loginPanel.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy = 2; loginPanel.add(lblUser, gbc);
        gbc.gridx = 1; loginPanel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 3; loginPanel.add(lblPass, gbc);
        gbc.gridx = 1; loginPanel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        loginPanel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> {
            String role = cmbRole.getSelectedItem().toString();
            String inputUser = txtUser.getText().trim();
            String inputPass = new String(txtPass.getPassword());

            if (inputUser.isEmpty() || inputPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (role.equals("Admin")) {
                authenticateAdmin(inputUser, inputPass);
            } else {
                authenticateStudent(inputUser, inputPass);
            }
        });

        return loginPanel;
    }

    private void authenticateAdmin(String username, String password) {
        String query = "SELECT admin_id FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentRole = "Admin";
                loggedInId = rs.getInt("admin_id");
                loggedInName = username;

                mainPanel.add(createAdminPanel(), "ADMIN_DASHBOARD");
                cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin credentials.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void authenticateStudent(String idStr, String password) {
        try {
            int studentId = Integer.parseInt(idStr);
            String query = "SELECT name FROM students WHERE student_id = ? AND password = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    currentRole = "Student";
                    loggedInId = studentId;
                    loggedInName = rs.getString("name");

                    mainPanel.add(createStudentPanel(), "STUDENT_DASHBOARD");
                    cardLayout.show(mainPanel, "STUDENT_DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Student ID or Password.");
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Student ID must be numeric.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- SCREEN 2: ADMIN PANEL ---
    private JPanel createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(43, 60, 80));
        JLabel lblWelcome = new JLabel("  Admin Dashboard | Logged in as: " + loggedInName, SwingConstants.LEFT);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        header.add(lblWelcome, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        adminPanel.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Books", createAdminBooksTab());
        tabs.addTab("Manage Students", createAdminStudentsTab());
        tabs.addTab("View Borrow History", createAdminHistoryTab());

        adminPanel.add(tabs, BorderLayout.CENTER);
        return adminPanel;
    }

    private JPanel createAdminBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Book Properties"));
        JTextField txtBookId = new JTextField(); txtBookId.setEditable(false);
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JTextField txtCat = new JTextField();
        JTextField txtQty = new JTextField();
        JComboBox<String> cmbStutus = new JComboBox<>(new String[]{"Available", "Unavailable"});
        txtQty.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateBookStutusFromQuantityField(txtQty, cmbStutus); }
            public void removeUpdate(DocumentEvent e) { updateBookStutusFromQuantityField(txtQty, cmbStutus); }
            public void changedUpdate(DocumentEvent e) { updateBookStutusFromQuantityField(txtQty, cmbStutus); }
        });

        form.add(new JLabel("Book ID:")); form.add(txtBookId);
        form.add(new JLabel("Title:")); form.add(txtTitle);
        form.add(new JLabel("Author:")); form.add(txtAuthor);
        form.add(new JLabel("Category:")); form.add(txtCat);
        form.add(new JLabel("Quantity:")); form.add(txtQty);
        form.add(new JLabel("Stutus:")); form.add(cmbStutus);

        JPanel actions = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Book");
        JButton btnUpd = new JButton("Update Book");
        JButton btnDel = new JButton("Delete Book");
        actions.add(btnAdd); actions.add(btnUpd); actions.add(btnDel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(form, BorderLayout.CENTER);
        leftPanel.add(actions, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Filters"));
        JTextField txtSearch = new JTextField(20);
        JComboBox<String> cmbCriteria = new JComboBox<>(new String[]{"Title", "Author", "Category"});
        JButton btnSearch = new JButton("Filter Search");
        JButton btnClear = new JButton("Reset");
        searchPanel.add(new JLabel("Search Keyword: "));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("By: "));
        searchPanel.add(cmbCriteria);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Quantity", "Stutus"}, 0);
        JTable table = new JTable(model);

        java.util.function.BiConsumer<String, String> refreshBooks = (keyword, criteria) -> {
            model.setRowCount(0);
            String sql = "SELECT book_id, title, author, category, quantity, " +
                    "CASE WHEN quantity > 0 THEN 'Available' ELSE 'Unavailable' END AS stutus FROM books";
            if (keyword != null && !keyword.isEmpty()) {
                String col = criteria.toLowerCase();
                sql += " WHERE " + col + " LIKE ?";
            }
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (keyword != null && !keyword.isEmpty()) {
                    pstmt.setString(1, "%" + keyword + "%");
                }
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    model.addRow(new Object[]{rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("category"), rs.getInt("quantity"), rs.getString("stutus")});
                }
            } catch(Exception ex) { ex.printStackTrace(); }
        };

        refreshBooks.accept("", "");

        btnSearch.addActionListener(e -> refreshBooks.accept(txtSearch.getText().trim(), cmbCriteria.getSelectedItem().toString()));
        btnClear.addActionListener(e -> { txtSearch.setText(""); refreshBooks.accept("", ""); });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                txtBookId.setText(model.getValueAt(row, 0).toString());
                txtTitle.setText(model.getValueAt(row, 1).toString());
                txtAuthor.setText(model.getValueAt(row, 2).toString());
                txtCat.setText(model.getValueAt(row, 3).toString());
                txtQty.setText(model.getValueAt(row, 4).toString());
                cmbStutus.setSelectedItem(model.getValueAt(row, 5).toString());
            }
        });

        btnAdd.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO books(title, author, category, quantity, stutus) VALUES(?,?,?,?,?)")) {
                ps.setString(1, txtTitle.getText());
                ps.setString(2, txtAuthor.getText());
                ps.setString(3, txtCat.getText());
                int qty = Integer.parseInt(txtQty.getText());
                ps.setInt(4, qty);
                ps.setString(5, getBookStutus(qty));
                ps.executeUpdate();
                selectBookStutus(cmbStutus, qty);
                refreshBooks.accept("", "");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Operation Error"); }
        });

        btnUpd.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE books SET title=?, author=?, category=?, quantity=?, stutus=? WHERE book_id=?")) {
                ps.setString(1, txtTitle.getText());
                ps.setString(2, txtAuthor.getText());
                ps.setString(3, txtCat.getText());
                int qty = Integer.parseInt(txtQty.getText());
                ps.setInt(4, qty);
                ps.setString(5, getBookStutus(qty));
                ps.setInt(6, Integer.parseInt(txtBookId.getText()));
                ps.executeUpdate();
                selectBookStutus(cmbStutus, qty);
                refreshBooks.accept("", "");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Operation Error"); }
        });

        btnDel.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id=?")) {
                ps.setInt(1, Integer.parseInt(txtBookId.getText()));
                ps.executeUpdate();
                refreshBooks.accept("", "");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Select record first."); }
        });

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));

        JTextField txtSId = new JTextField();
        JTextField txtSName = new JTextField();
        JTextField txtSEmail = new JTextField();
        JTextField txtSPhone = new JTextField();
        JTextField txtSPass = new JTextField();

        form.add(new JLabel("Student ID (Required):")); form.add(txtSId);
        form.add(new JLabel("Name (Required):")); form.add(txtSName);
        form.add(new JLabel("Email:")); form.add(txtSEmail);
        form.add(new JLabel("Phone:")); form.add(txtSPhone);
        form.add(new JLabel("Account Password (Required):")); form.add(txtSPass);

        JButton btnAdd = new JButton("Register Student");
        JPanel actions = new JPanel(new FlowLayout()); actions.add(btnAdd);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(form, BorderLayout.CENTER); leftPanel.add(actions, BorderLayout.SOUTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
        JTable table = new JTable(model);

        Runnable refreshStudents = () -> {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT student_id, name, email, phone FROM students")) {
                while(rs.next()) {
                    model.addRow(new Object[]{rs.getInt("student_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone")});
                }
            } catch(Exception ex) { ex.printStackTrace(); }
        };
        refreshStudents.run();

        btnAdd.addActionListener(e -> {
            String idText = txtSId.getText().trim();
            String nameText = txtSName.getText().trim();
            String passText = txtSPass.getText().trim();

            if (idText.isEmpty() || nameText.isEmpty() || passText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (ID, Name, and Password).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO students(student_id, name, email, phone, password) VALUES(?,?,?,?,?)")) {

                ps.setInt(1, Integer.parseInt(idText));
                ps.setString(2, nameText);
                ps.setString(3, txtSEmail.getText().trim());
                ps.setString(4, txtSPhone.getText().trim());
                ps.setString(5, passText);

                ps.executeUpdate();
                refreshStudents.run();

                txtSId.setText("");
                txtSName.setText("");
                txtSEmail.setText("");
                txtSPhone.setText("");
                txtSPass.setText("");

                JOptionPane.showMessageDialog(this, "Student registered successfully!");

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Student ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error Saving Student Profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        DefaultTableModel historyModel = new DefaultTableModel(
                new String[]{"Borrow ID", "Student ID", "Student Name", "Book Title", "Borrow Date", "Due Date", "Return Status"}, 0
        );
        JTable tableHistory = new JTable(historyModel);

        JButton btnRefresh = new JButton("Refresh History Log");
        btnRefresh.setBackground(new Color(70, 130, 180));
        btnRefresh.setForeground(Color.WHITE);

        Runnable loadGlobalHistory = () -> {
            historyModel.setRowCount(0);
            String sql = "SELECT b.borrow_id, b.student_id, s.name AS student_name, bk.title AS book_title, " +
                    "b.borrow_date, b.due_date, b.return_date " +
                    "FROM borrow b " +
                    "JOIN students s ON b.student_id = s.student_id " +
                    "JOIN books bk ON b.book_id = bk.book_id " +
                    "ORDER BY b.borrow_id DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    String returnStatus = rs.getDate("return_date") != null
                            ? sdf.format(rs.getDate("return_date"))
                            : "Still Borrowed Out";

                    historyModel.addRow(new Object[]{
                            rs.getInt("borrow_id"),
                            rs.getInt("student_id"),
                            rs.getString("student_name"),
                            rs.getString("book_title"),
                            rs.getDate("borrow_date") != null ? sdf.format(rs.getDate("borrow_date")) : "-",
                            rs.getDate("due_date") != null ? sdf.format(rs.getDate("due_date")) : "-",
                            returnStatus
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };

        loadGlobalHistory.run();
        btnRefresh.addActionListener(e -> loadGlobalHistory.run());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(btnRefresh);

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableHistory), BorderLayout.CENTER);
        return panel;
    }

    // --- SCREEN 3: STUDENT PANEL ---
    private JPanel createStudentPanel() {
        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 130, 90));
        JLabel lblWelcome = new JLabel("  Student Access | Welcome, " + loggedInName + " (ID: " + loggedInId + ")", SwingConstants.LEFT);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        header.add(lblWelcome, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        studentPanel.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // SUBTAB 1: Browse & Borrow Books
        JPanel browsePanel = new JPanel(new BorderLayout(5, 5));

        JPanel studentSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentSearchPanel.setBorder(BorderFactory.createTitledBorder("Search Engines"));
        JTextField txtStudSearch = new JTextField(20);
        JComboBox<String> cmbStudCriteria = new JComboBox<>(new String[]{"Title", "Author", "Category"});
        JButton btnStudSearch = new JButton("Search Library");
        JButton btnStudReset = new JButton("Reset View");
        studentSearchPanel.add(new JLabel("Keywords: "));
        studentSearchPanel.add(txtStudSearch);
        studentSearchPanel.add(new JLabel("Filter By: "));
        studentSearchPanel.add(cmbStudCriteria);
        studentSearchPanel.add(btnStudSearch);
        studentSearchPanel.add(btnStudReset);

        DefaultTableModel bookModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Category", "Available Qty", "Stutus"}, 0);
        JTable tableBooks = new JTable(bookModel);

        java.util.function.BiConsumer<String, String> loadAvailableBooks = (keyword, criteria) -> {
            bookModel.setRowCount(0);
            String sql = "SELECT book_id, title, author, category, quantity, " +
                    "CASE WHEN quantity > 0 THEN 'Available' ELSE 'Unavailable' END AS stutus FROM books";
            if (keyword != null && !keyword.isEmpty()) {
                sql += " WHERE " + criteria.toLowerCase() + " LIKE ?";
            }
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (keyword != null && !keyword.isEmpty()) {
                    pstmt.setString(1, "%" + keyword + "%");
                }
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    bookModel.addRow(new Object[]{rs.getInt("book_id"), rs.getString("title"), rs.getString("author"), rs.getString("category"), rs.getInt("quantity"), rs.getString("stutus")});
                }
            } catch(Exception e){ e.printStackTrace(); }
        };

        loadAvailableBooks.accept("", "");

        btnStudSearch.addActionListener(e -> loadAvailableBooks.accept(txtStudSearch.getText().trim(), cmbStudCriteria.getSelectedItem().toString()));
        btnStudReset.addActionListener(e -> { txtStudSearch.setText(""); loadAvailableBooks.accept("", ""); });

        JButton btnBorrow = new JButton("Borrow Selected Book");
        btnBorrow.setBackground(new Color(60, 130, 90));
        btnBorrow.setForeground(Color.WHITE);
        JPanel browseActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        browseActionPanel.add(btnBorrow);

        browsePanel.add(studentSearchPanel, BorderLayout.NORTH);
        browsePanel.add(new JScrollPane(tableBooks), BorderLayout.CENTER);
        browsePanel.add(browseActionPanel, BorderLayout.SOUTH);
        tabs.addTab("Browse Available Library Books", browsePanel);

        // SUBTAB 2: My Borrowed History & Returns
        JPanel borrowPanel = new JPanel(new BorderLayout(5, 5));
        DefaultTableModel borrowModel = new DefaultTableModel(new String[]{"Borrow ID", "Book ID", "Book Title", "Borrow Date", "Due Date", "Return Date"}, 0);
        JTable tableBorrow = new JTable(borrowModel);

        Runnable loadBorrowHistory = () -> {
            borrowModel.setRowCount(0);
            String sql = "SELECT b.borrow_id, b.book_id, bk.title, b.borrow_date, b.due_date, b.return_date " +
                    "FROM borrow b JOIN books bk ON b.book_id = bk.book_id WHERE b.student_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, loggedInId);
                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while(rs.next()) {
                    borrowModel.addRow(new Object[]{
                            rs.getInt("borrow_id"),
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getDate("borrow_date") != null ? sdf.format(rs.getDate("borrow_date")) : "-",
                            rs.getDate("due_date") != null ? sdf.format(rs.getDate("due_date")) : "-",
                            rs.getDate("return_date") != null ? sdf.format(rs.getDate("return_date")) : "Active Borrow"
                    });
                }
            } catch(Exception e){ e.printStackTrace(); }
        };
        loadBorrowHistory.run();

        JButton btnReturn = new JButton("Return Selected Book");
        btnReturn.setBackground(new Color(178, 34, 34));
        btnReturn.setForeground(Color.WHITE);
        JPanel returnActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        returnActionPanel.add(btnReturn);

        borrowPanel.add(new JScrollPane(tableBorrow), BorderLayout.CENTER);
        borrowPanel.add(returnActionPanel, BorderLayout.SOUTH);
        tabs.addTab("My Borrowed Books History", borrowPanel);

        // --- ACTION LISTENERS ---
        btnBorrow.addActionListener(e -> {
            int selectedRow = tableBooks.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book from the table first.");
                return;
            }

            int bookId = (int) bookModel.getValueAt(selectedRow, 0);
            int currentQty = (int) bookModel.getValueAt(selectedRow, 4);

            if (currentQty <= 0) {
                JOptionPane.showMessageDialog(this, "This book is currently out of stock!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    String insertBorrowSql = "INSERT INTO borrow (student_id, book_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertBorrowSql)) {
                        java.sql.Date today = getTodayDate();

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(today);
                        cal.add(Calendar.DAY_OF_YEAR, 14);
                        java.sql.Date dueDate = new java.sql.Date(cal.getTimeInMillis());

                        pstmt.setInt(1, loggedInId);
                        pstmt.setInt(2, bookId);
                        pstmt.setDate(3, today);
                        pstmt.setDate(4, dueDate);
                        pstmt.executeUpdate();
                    }

                    String updateBookSql = "UPDATE books SET quantity = quantity - 1, stutus = IF(quantity - 1 > 0, 'Available', 'Unavailable') WHERE book_id = ? AND quantity > 0";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                        pstmt.setInt(1, bookId);
                        int updatedRows = pstmt.executeUpdate();
                        if (updatedRows == 0) {
                            throw new SQLException("This book is no longer available.");
                        }
                    }

                    conn.commit();
                } catch (SQLException ex) {
                    rollbackQuietly(conn);
                    throw ex;
                }

                JOptionPane.showMessageDialog(this, "Book successfully borrowed! Due in 14 days.");

                loadAvailableBooks.accept("", "");
                loadBorrowHistory.run();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
            }
        });

        btnReturn.addActionListener(e -> {
            int selectedRow = tableBorrow.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a borrowed record to return.");
                return;
            }

            int borrowId = (int) borrowModel.getValueAt(selectedRow, 0);
            int bookId = (int) borrowModel.getValueAt(selectedRow, 1);
            String returnStatus = borrowModel.getValueAt(selectedRow, 5).toString();

            if (!"Active Borrow".equals(returnStatus)) {
                JOptionPane.showMessageDialog(this, "This book has already been returned!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    String updateBorrowSql = "UPDATE borrow SET return_date = ? WHERE borrow_id = ? AND return_date IS NULL";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBorrowSql)) {
                        pstmt.setDate(1, getTodayDate());
                        pstmt.setInt(2, borrowId);
                        int updatedRows = pstmt.executeUpdate();
                        if (updatedRows == 0) {
                            throw new SQLException("Record already updated or invalid.");
                        }
                    }

                    String updateBookSql = "UPDATE books SET quantity = quantity + 1, stutus = 'Available' WHERE book_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBookSql)) {
                        pstmt.setInt(1, bookId);
                        pstmt.executeUpdate();
                    }

                    conn.commit();
                } catch (SQLException ex) {
                    rollbackQuietly(conn);
                    throw ex;
                }

                JOptionPane.showMessageDialog(this, "Book returned successfully!");

                loadAvailableBooks.accept("", "");
                loadBorrowHistory.run();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
            }
        });

        studentPanel.add(tabs, BorderLayout.CENTER);
        return studentPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryManagementSystem().setVisible(true));
    }
}