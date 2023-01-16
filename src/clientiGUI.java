import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class clientiGUI extends JFrame {
    private JTable table1;
    private JPanel panel1;
    private JTextField cnp;
    private JButton STERGERECLIENTButton;
    private JButton CAUTAREANAGAJATButton;
    private JButton INAPOIButton;
    private JButton CLIENTULCAREAPLATITButton;
    private JTable table2;

    public clientiGUI() {
        setTitle("Clienti");
        setContentPane(panel1);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 400));
        setVisible(true);

        table2.setDefaultEditor(Object.class, null);
        table2.setModel(new DefaultTableModel(null, new String[]{"Nume", "Prenume", "Suma"}));

        STERGERECLIENTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = conectare.conect();
                try {
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM Clienti WHERE CNP='" + cnp.getText() + "'");
                    statement.execute();
                    createTable();
                    JOptionPane.showMessageDialog(clientiGUI.super.rootPane, "Clientul a fost sters!", "Action successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        CAUTAREANAGAJATButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = conectare.conect();
                try {
                    //interogare simpla 3/6
                    String query = "SELECT A.Nume, A.Prenume FROM Angajati A\n" +
                            "inner join Rezervari R2 on A.AngajatID = R2.AngajatID\n" +
                            "inner join Clienti C on R2.ClientID = C.ClientID\n" +
                            "WHERE C.CNP='" + cnp.getText() + "'";

                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();

                    String numeA = null;
                    String prenumeA = null;
                    while (resultSet.next()) {
                        numeA = resultSet.getString(1);
                        prenumeA = resultSet.getString(2);
                    }

                    JOptionPane.showMessageDialog(clientiGUI.super.rootPane, "Angajatul care se ocupa de acest client este: " + numeA + " " + prenumeA, "Angajat", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        INAPOIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rasp = JOptionPane.showConfirmDialog(clientiGUI.super.rootPane, "Vrei sa te intorci la pagina de admin?", "Action", JOptionPane.YES_NO_OPTION);
                if (rasp == 0) {
                    adminGUI admin = new adminGUI();
                    dispose();
                }
            }
        });
        CLIENTULCAREAPLATITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = conectare.conect();
                //interogare complexa 3/4
                String query = "\n" +
                        "SELECT C.CNP FROM Clienti C\n" +
                        "WHERE c.ClientID IN  (SELECT top 1 ClientID FROM Rezervari\n" +
                        "                      where suma = (select max(suma) from rezervari)\n" +
                        "                      order by Suma desc)";
                try {
                    PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();

                    String cnp = null;
                    while (resultSet.next()) {
                        cnp = resultSet.getString(1);
                    }
                    // interogare simpla 7/6
                    String query1 = "SELECT C.Nume, C.Prenume, Suma FROM Clienti C\n" +
                            "INNER JOIN Rezervari R2 on C.ClientID = R2.ClientID\n" +
                            "WHERE C.CNP = '" + cnp + "'";

                    PreparedStatement statement1 = connection.prepareStatement(query1);
                    ResultSet resultSet1 = statement1.executeQuery();
                    ResultSetMetaData resultSetMetaData1 = resultSet1.getMetaData();
                    DefaultTableModel model1 = (DefaultTableModel) table2.getModel();
                    model1.setRowCount(0);

                    table2.setRowHeight(30);
                    table2.setPreferredSize(new Dimension(200, 50));
                    int cols = resultSetMetaData1.getColumnCount();
                    String[] colName = new String[cols];

                    //System.out.println(cols);
                    for (int i = 0; i < cols; i++) {
                        colName[i] = resultSetMetaData1.getColumnName(i + 1);
                    }

                    model1.setColumnIdentifiers(colName);

                    String nume, prenume;
                    int suma;
                    while (resultSet1.next()) {
                        nume = resultSet1.getString(1);
                        prenume = resultSet1.getString(2);
                        suma = resultSet1.getInt(3);
                        Object[] row = {nume, prenume, suma};
                        model1.addRow(row);
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    public void createTable() {
        Connection connection = conectare.conect();
        try {

            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT Nume, Prenume, NumarTelefon, Email, CNP FROM Clienti");
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            ResultSetMetaData resultSetMetaData1 = resultSet1.getMetaData();
            DefaultTableModel model1 = (DefaultTableModel) table1.getModel();
            model1.setRowCount(0);

            int cols = resultSetMetaData1.getColumnCount();
            String[] colName = new String[cols];

            System.out.println(cols);
            for (int i = 0; i < cols; i++) {
                colName[i] = resultSetMetaData1.getColumnName(i + 1);
            }
            //testare facuta pentru mine
//            for (int i = 0; i < cols; i++) {
//                System.out.println(colName[i]);
//            }

            model1.setColumnIdentifiers(colName);

            //table1.setDefaultEditor(Object.class, null);
            //table1.setModel(new DefaultTableModel(null, new String[]{"1", "2", "3", "4", "5", "6", "7"}));

            String nume, prenume, nr, email, cnp;
            while (resultSet1.next()) {
                nume = resultSet1.getString(1);
                prenume = resultSet1.getString(2);
                nr = resultSet1.getString(3);
                email = resultSet1.getString(4);
                cnp = resultSet1.getString(5);
                String[] row = {nume, prenume, nr, email, cnp};
                model1.addRow(row);
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        clientiGUI clienti = new clientiGUI();
        clienti.createTable();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        cnp = new JTextField();
        cnp.setText("");
        panel2.add(cnp, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Introduceti CNP:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        STERGERECLIENTButton = new JButton();
        STERGERECLIENTButton.setText("STERGERE CLIENT");
        panel2.add(STERGERECLIENTButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CAUTAREANAGAJATButton = new JButton();
        CAUTAREANAGAJATButton.setText("CAUTARE ANAGAJAT");
        panel2.add(CAUTAREANAGAJATButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel5, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        CLIENTULCAREAPLATITButton = new JButton();
        CLIENTULCAREAPLATITButton.setText("CLIENTUL CARE A PLATIT CEA MAI MARE SUMA");
        panel5.add(CLIENTULCAREAPLATITButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel6.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table2 = new JTable();
        scrollPane2.setViewportView(table2);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel7, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel7.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel9, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/resources/clienti150.png")));
        label2.setText("");
        panel9.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        INAPOIButton = new JButton();
        INAPOIButton.setText("INAPOI");
        panel1.add(INAPOIButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
