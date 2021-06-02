import java.awt.*;
import java.awt.event.ActionEvent;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class viewRCM {
    private JPanel viewRcmPane;
    private JButton activateDeactivateButton;
    private JTable statusTable;
    private JTabbedPane addModifyTab;
    private JButton emptyButton;
    private JFormattedTextField newItemPrice;
    private JButton addButton;
    private JComboBox modifyItemCB;
    private JFormattedTextField modifiedItemPrice;
    private JButton modifyButton;
    private JPanel buttonPane;
    private JSplitPane modifySplitPane;
    private JPanel statusPane;
    private JPanel addTab;
    private JPanel modifyTab;
    private JPanel addButtonPane;
    private JPanel modifyButtonPane;
    private JComboBox addItemCB;
    private JPanel addLblErrorPanel;
    private JPanel modifyLblErrorPanel;
    private JLabel addLbl;
    private JLabel modifyLbl;
    private JPanel removeTab;
    private JComboBox removeItemCB;
    private JButton removeButton;
    private JLabel removeItemLbl;
    private JPanel removeButtonPane;
    private JLabel removeLbl;
    private JButton deleteButton;
    RCM rcm;
    RMOS rmos;



    public viewRCM(RCM rcm) throws Exception {
        this.rcm = rcm;
        rmos = RMOS.get_instance();

        initComponents();
    }

    public void initComponents() throws Exception {
        JFrame frame = new JFrame();
        frame.setContentPane(viewRcmPane);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 1200, 725);
        frame.setLocationRelativeTo(null);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        loadStatusTable();
        loadAddItems();
        loadModifyItems();
        loadRemoveItems();
        loadButtons();
        frame.setVisible(true);
    }

    private void loadButtons(){
        if ((rcm.getStatus() == Status.valueOf("ACTIVE")) || (rcm.getStatus() == Status.valueOf("FULL"))){
            activateDeactivateButton.setText("DEACTIVATE");
        }
        else {
            activateDeactivateButton.setText("ACTIVATE");
        }

        if (rcm.getCapacityLeft() == 0){
            emptyButton.setVisible(true);
        }
        else {
            emptyButton.setVisible(false);
        }

        activateDeactivateButton.addActionListener(evt -> switchPower(evt));
        emptyButton.addActionListener(evt -> emptyRCM(evt));
        deleteButton.addActionListener(evt -> deleteRCM(evt));
        addButton.addActionListener(evt -> addItem(evt));
        modifyButton.addActionListener(evt -> modifyItem(evt));
        removeButton.addActionListener(evt -> removeItem(evt));
    }

    private void deleteRCM(ActionEvent actionEvent){

    }

    void removeItem(ActionEvent actionEvent){
        String itemToRemove, itemId;
        if (removeItemCB.getSelectedItem().toString() == " -- Select Item --"){
            removeLbl.setText("Please select an item to remove from the list of available items");
            removeLbl.setVisible(true);
            return;
        }
        else{
            itemToRemove = removeItemCB.getSelectedItem().toString();
        }

        removeLbl.setText(rmos.removeItemFromRCM(rcm, removeItemCB.getSelectedItem().toString()));
        removeLbl.setVisible(true);
        removeItemCB.setSelectedIndex(0);

        loadAddItems();
        loadModifyItems();
    }

    void addItem(ActionEvent event) {
        String itemToAdd, itemId;
        Double price;
        if (addItemCB.getSelectedItem().toString() == " -- Select Item --"){
            addLbl.setText("Please select an item to add from the list of available items");
            addLbl.setVisible(true);
            return;
        }
        else{
            itemToAdd = addItemCB.getSelectedItem().toString();
        }

        if ((newItemPrice.getText() == "") || (newItemPrice.getText().matches("[A-Za-z]"))){
            addLbl.setText("Please set a valid price for the selected item");
            addLbl.setVisible(true);
            return;
        }
        else {
            price = new Double(newItemPrice.getText());
        }

        addLbl.setText(rmos.addItemToRCM(rcm, itemToAdd, price));
        addLbl.setVisible(true);
        addItemCB.setSelectedIndex(0);
        newItemPrice.setText("");

        loadAddItems();
        loadModifyItems();
        loadRemoveItems();
    }

    void modifyItem(ActionEvent event) {

        String itemToModify, itemId;
        double newPrice;
        if (modifyItemCB.getSelectedItem().toString() == " -- Select Item --"){
            modifyLbl.setText("Please select an item to modify from the list of items serviced by the RCM");
            modifyLbl.setVisible(true);
            return;
        }
        else{
            itemToModify = modifyItemCB.getSelectedItem().toString();
        }

        if ((modifiedItemPrice.getText() == "") || (modifiedItemPrice.getText().matches("[A-Za-z]"))){
            modifyLbl.setText("Please set a valid new price for the selected item");
            modifyLbl.setVisible(true);
            return;
        }
        else {
            newPrice = Double.parseDouble(modifiedItemPrice.getText());
            modifyLbl.setText(rmos.modifyItemOfRCM(rcm, itemToModify, newPrice));
            modifyLbl.setVisible(true);
            modifyItemCB.setSelectedIndex(0);
            modifiedItemPrice.setText("");
        }

    }


    private void loadStatusTable(){

        String[] rowNames = {"LOCATION", "OP_STATUS", "WEIGHT OF ITEMS", "MONEY LEFT", "LAST EMPTIED"};
        String[] columnNames = {"Attribute", "Value"};
        String[] data = {rcm.getLocation(),
                String.valueOf(rcm.getStatus()),
                String.valueOf(rcm.getCapacity() - rcm.getCapacityLeft()),
                String.valueOf(rcm.getMoneyLeft()),
                rcm.getLastEmptiedStr()};
        statusTable.setModel(new DefaultTableModel(columnNames, 0) {public boolean isCellEditable(int row, int column) { return false; }});
        statusTable.setRowHeight(40);
        statusTable.getTableHeader().setReorderingAllowed(false);
        statusTable.getTableHeader();
        statusTable.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 20));
        //((DefaultTableCellRenderer)statusTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        statusTable.setFont(new Font("Montserrat", Font.PLAIN, 15));
        //statusTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        statusTable.getColumnModel().getColumn(0);
        statusTable.getColumnModel().getColumn(1);
        DefaultTableModel rcmTableModel = (DefaultTableModel)(statusTable.getModel());

        for (int i = 0; i < rowNames.length; i++) {
            rcmTableModel.addRow(new String[]{rowNames[i], data[i]});
        }
    }

    private void switchPower(ActionEvent e){
        if ((rcm.getStatus() == Status.valueOf("ACTIVE")) || (rcm.getStatus() == Status.valueOf("FULL"))){
            rmos.deactivate(rcm);
        }
        else {
           rmos.activate(rcm);
        }
        loadButtons();
    }

    private void emptyRCM(ActionEvent e){
        rmos.empty(rcm);
        this.loadStatusTable();
        emptyButton.setVisible(false);
    }

    private void loadAddItems() {
        addItemCB.removeAllItems();
        ArrayList<String> rcmItemIds = new ArrayList<>(rcm.getAvailableItems().keySet());
        ArrayList<String> allItemIds = new ArrayList<>(rmos.getItemMapToId().keySet());

        //System.out.println(allItemIds);
        //System.out.println(rcmItemIds);

        if (allItemIds.size() == rcmItemIds.size()){
            addItemCB.addItem(" -- RCM already services all available items -- ");
            addButton.setEnabled(false);
            return;
        }

        addButton.setEnabled(true);

        allItemIds.removeAll(rcmItemIds);
        //System.out.println(allItemIds);
        //System.out.println(rcmItemIds);

        addItemCB.addItem(" -- Select Item -- ");
        for (String id : allItemIds) {
            if (!rcmItemIds.contains(id)) {
                addItemCB.addItem(rmos.getItemMapToId().get(id));
            }
        }
        //System.out.println(allItems);

    }

    private void loadModifyItems() {
        modifyItemCB.removeAllItems();
        //System.out.println(rcmItems);
        //System.out.println(allItems);
        modifyItemCB.addItem(" -- Select Item --");
        for (String id : rcm.getAvailableItems().keySet()) {
            modifyItemCB.addItem(rmos.getItemMapToId().get(id));
        }

        //modifyItemCB.addItemListener(evt -> addCbChanged(evt));
    }

    private void loadRemoveItems() {
        removeItemCB.removeAllItems();
        //System.out.println(rcmItems);
        //System.out.println(allItems);
        removeItemCB.addItem(" -- Select Item --");
        for (String id : rcm.getAvailableItems().keySet()) {
            removeItemCB.addItem(rmos.getItemMapToId().get(id));
        }

        //modifyItemCB.addItemListener(evt -> addCbChanged(evt));
    }

    public static void main(String args[]) throws Exception{
        //viewRCM v = new viewRCM("SCU-001");
    }

}
