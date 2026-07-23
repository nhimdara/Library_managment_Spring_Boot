package edu.ite.libraryapi.views.student;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class StudentDashboard extends JFrame {
    public StudentDashboard() {
        super("Student Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Student dashboard"));
    }
}
