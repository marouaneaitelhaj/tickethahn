package com.example.ui;


import javax.swing.table.AbstractTableModel;

import com.example.entities.Ticket;

import java.util.List;

public class TicketTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Title", "Priority", "Category", "Status", "Assigned To" };
    private List<Ticket> tickets;

    public TicketTableModel(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public int getRowCount() {
        return tickets.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ticket ticket = tickets.get(rowIndex);
        switch (columnIndex) {
            case 0: return ticket.getId();
            case 1: return ticket.getTitle();
            case 2: return ticket.getPriority();
            case 3: return ticket.getCategory();
            case 4: return ticket.getStatus();
            case 5: return ticket.getAssignedToId();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Allow editing only for the "Status" column (index 4)
        return columnIndex == 4;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 4) {
            Ticket ticket = tickets.get(rowIndex);
            ticket.setStatus((String) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public Ticket getTicketAt(int rowIndex) {
        return tickets.get(rowIndex);
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        fireTableDataChanged();
    }
}
