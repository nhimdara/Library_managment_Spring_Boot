package edu.ite.libraryapi.views.login;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        super("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Login screen"));
    }
}
