package edu.ite.libraryapi.views.login;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ForgotPasswordFrame extends JFrame {
    public ForgotPasswordFrame() {
        super("Forgot Password");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Forgot password screen"));
    }
}
