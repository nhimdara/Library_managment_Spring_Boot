package edu.ite.libraryapi.views.components;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class SearchBar extends JPanel {
    private final JTextField field;

    public SearchBar() {
        field = new JTextField(20);
        add(field);
    }
}
