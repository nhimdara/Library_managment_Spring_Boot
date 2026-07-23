package edu.ite.libraryapi.views.admin;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        super("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Admin dashboard"));
    }
}
