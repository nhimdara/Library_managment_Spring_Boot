package edu.ite.libraryapi.views.login;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        super("Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Registration screen"));
    }
}
