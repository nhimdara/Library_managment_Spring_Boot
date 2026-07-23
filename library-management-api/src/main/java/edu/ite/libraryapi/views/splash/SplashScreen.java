package edu.ite.libraryapi.views.splash;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        super("Splash Screen");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Loading library management system..."));
    }
}
