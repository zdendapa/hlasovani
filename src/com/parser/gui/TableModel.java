package com.parser.gui;

import com.parser.controller.ApplicationObjects;

import javax.swing.table.AbstractTableModel;

class TableModel extends AbstractTableModel {
    private String[] columnNames = {
            "#",
            "Datum",
            "Video posun",
            "Název usnesení",
            "Č. usneseni",
            "Přidělit",
            "ANO",
            "NE",
            "ZDR",
            "NEH",
            "Poznámka",
    };

    private Object[][] data = new Object[0][0];

    public void addRow(Object[] rowData) {
        if (getRowCount() == 0) {
            data = new Object[1][columnNames.length];
            data[0] = rowData;
            fireTableRowsInserted(getRowCount(), getRowCount());
            // super.addRow(convertToVector( data[0]));
        } else {
            Object[][] tempData = data;
            data = new Object[tempData.length + 1][columnNames.length];
            for (int i = 0; i < tempData.length; i++) {
                data[i] = tempData[i];
            }
            data[tempData.length] = rowData;
            fireTableRowsInserted(getRowCount(), getRowCount());
            // super.addRow(convertToVector( data[tempData.length]));
        }

    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        if (data != null) {
            return data.length;
        } else {
            return 0;
        }
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        boolean isAdminLogged = ApplicationObjects.getInstance().isAdminLogged();
        if (MainFrame.DEBUG) {
            System.out.println("isAdminLogged: " + isAdminLogged);
            System.out.println("isFalse: " + (col < 1 || col == 0 || (col == 1 && !isAdminLogged) || col == 2
                    || col == 5 || col == 6 || col == 7 || col == 8));
        }
        if (col < 1 || col == 0 || (col == 1 && !isAdminLogged) || col == 3
                || col == 6 || col == 7  || col == 8 || col == 9  || col == 10) {
            return false;
        } else {
            return true;
        }
    }



    public void setValueAt_old(Object value, int row, int col) {
        // if (DEBUG) {
        if (value != null) {
            if (MainFrame.DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                        + " to " + value
                        + " (an instance of "
                        + value.getClass() + ")");
            }
            //}

            data[row][col] = value;
            fireTableCellUpdated(row, col);
            //implementing increment in column
            if (col == 3 && row != data.length - 1 && value != null) {
                int incrementByOne = Integer.valueOf((Integer) value);
                if (incrementByOne > 0) {
                    for (int i = row + 1; i < data.length; i++) {
                        data[i][col] = ++incrementByOne;
                        fireTableCellUpdated(i, col);
                    }
                }
            }

            if (MainFrame.DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }
    }



    public void setValueAt(Object value, int row, int col) {
        // if (DEBUG) {
        if (value != null) {
            if (MainFrame.DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                        + " to " + value
                        + " (an instance of "
                        + value.getClass() + ")");
            }
            //}

            data[row][col] = value;
            fireTableCellUpdated(row, col);


            if (MainFrame.DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();
        if (MainFrame.DEBUG) {
            for (int i = 0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j = 0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }
}