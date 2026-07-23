package edu.ite.libraryapi.views.components;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Table extends JTable {
    public Table(Object[] columns, Object[][] data) {
        super(new DefaultTableModel(data, columns));
    }
}
