import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RezervariGUI extends JFrame {
    private JPanel rezervari;
    private JTable table1;
    private JTextField dataPreluare;
    private JTextField dataReturnare;
    private JTextField clientID;
    private JTextField masinaID;
    private JTextField angajatID;
    private JTextField suma;
    private JButton adaugaRezervareButton;
    private JTextField dataCautare;
    private JButton cautaButton;
    private JButton INAPOIButton;
    private JTable table2;
    private JButton TABELEUTILEButton;
    private JButton VEZILUNACUCELEButton;
    private JTable table3;

    public RezervariGUI() throws SQLException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(this.rezervari);
        this.setTitle("Rezervari");
        this.setPreferredSize(new Dimension(700, 800));
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.pack();

        table3.setDefaultEditor(Object.class, null);
        table3.setModel(new DefaultTableModel(null, new String[]{"Nume", "Prenume", "DataPreluare", "DataReturnare"}));

        table2.setDefaultEditor(Object.class, null);
        table2.setModel(new DefaultTableModel(null, new String[]{"Marca", "AnFabricare", "SerieSasiu", "NrInmatriculare"}));



        adaugaRezervareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection connection = conectare.conect();
                    String query = "INSERT INTO Rezervari ( Suma, DataPreluare, DataReturnare, ClientID, MasinaID, AngajatID) VALUES( ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);


                    int suma1 = Integer.valueOf(suma.getText());
                    String data = dataPreluare.getText();
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date data1 = formatter.parse(data);
                    Date sqlData = new Date(data1.getTime());

                    String dataa = dataReturnare.getText();
                    DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date data2 = formatter.parse(data);
                    Date sqlData1 = new Date(data2.getTime());

                    int clientid = Integer.valueOf(clientID.getText());
                    int masinaid = Integer.valueOf(masinaID.getText());
                    int angajatid = Integer.valueOf(angajatID.getText());


                    statement.setInt(1, suma1);
                    statement.setDate(2, sqlData);
                    statement.setDate(3, sqlData1);
                    statement.setInt(4, clientid);
                    statement.setInt(5, masinaid);
                    statement.setInt(6, angajatid);
                    statement.execute();
                    JOptionPane.showMessageDialog(RezervariGUI.super.rootPane, "Rezervare adaugata cu succes!", "Action successful", JOptionPane.INFORMATION_MESSAGE);

                    createTable();

                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        cautaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = conectare.conect();
                try {
                    //interogare simpla 2/6
                    String query = "select NrInmatriculare from TaxeMasina T\n" +
                            "inner join Rezervari R2 on R2.MasinaID = T.MasinaID\n" +
                            "WHERE R2.DataPreluare = ?";
                    PreparedStatement statement = connection.prepareStatement(query);


                    String data = dataCautare.getText();
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date data1 = formatter.parse(data);
                    Date sqlData = new Date(data1.getTime());

                    statement.setDate(1, sqlData);
                    statement.execute();

                    ResultSet resultSet = statement.executeQuery();
                    String nr = null;
                    while (resultSet.next()) {
                        nr = resultSet.getString(1);
                    }

                    JOptionPane.showMessageDialog(RezervariGUI.super.rootPane, "Masina care este inchiriata in data de " + dataCautare.getText() + " are numarul de inmatriculare: " + nr, "Numar inmatriculare", JOptionPane.INFORMATION_MESSAGE);
                    //interogare simpla 5/6
                    String query1 = "SELECT M.Marca, M.AnFabricare, M.SerieSasiu, NrInmatriculare from Masini M\n" +
                            "INNER JOIN Rezervari R on M.MasinaID = R.MasinaID\n" +
                            "INNER JOIN TaxeMasina TM on M.MasinaID = TM.MasinaID\n" +
                            "WHERE R.DataPreluare <> ?";
                    PreparedStatement statement1 = connection.prepareStatement(query1);

                    String data2 = dataCautare.getText();
                    DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date data3 = formatter.parse(data2);
                    Date sqlData1 = new Date(data1.getTime());

                    statement1.setDate(1, sqlData1);
                    statement1.execute();

                    ResultSet resultSet1 = statement1.executeQuery();
                    ResultSetMetaData resultSetMetaData = resultSet1.getMetaData();
                    DefaultTableModel model1 = (DefaultTableModel) table2.getModel();
                    model1.setRowCount(0);


                    int cols1 = resultSetMetaData.getColumnCount();
                    String[] colName1 = new String[cols1];

                    System.out.println(cols1);
                    for (int i = 0; i < cols1; i++) {
                        colName1[i] = resultSetMetaData.getColumnName(i + 1);
                    }

                    model1.setColumnIdentifiers(colName1);

                    String marca, serie, nr1;
                    int an;
                    while (resultSet1.next()) {
                        marca = resultSet1.getString(1);
                        an = resultSet1.getInt(2);
                        serie = resultSet1.getString(3);
                        nr1 = resultSet1.getString(4);
                        Object[] row = {marca, an, serie, nr1};
                        model1.addRow(row);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        INAPOIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rasp = JOptionPane.showConfirmDialog(RezervariGUI.super.rootPane, "Vrei sa te intorci la pagina de admin?", "Action", JOptionPane.YES_NO_OPTION);
                if (rasp == 0) {
                    adminGUI admin = new adminGUI();
                    dispose();
                    //AjutorRezervariGUI.super.dispose();
                }
            }
        });
        TABELEUTILEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AjutorRezervariGUI ajutorRezervariGUI = new AjutorRezervariGUI();
                try {
                    ajutorRezervariGUI.createTable1();
                    ajutorRezervariGUI.createTable2();
                    ajutorRezervariGUI.createTable3();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        VEZILUNACUCELEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = conectare.conect();
                //interogare complexa 1/4
                String query = "select distinct month(DataPreluare)\n" +
                        "from Rezervari\n" +
                        "where month(DataPreluare) in (select top 1 month(DataPreluare) as Luna\n" +
                        "                              from Rezervari\n" +
                        "                              group by month(DataPreluare)\n" +
                        "                              order by count(*) desc)";
                try {
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.execute();
                    ResultSet resultSet = statement.executeQuery();
                    int luna = 0;

                    while (resultSet.next()) {
                        luna = resultSet.getInt(1);
                    }

                    String lunaString = null;

                    switch (luna) {
                        case 1:
                            lunaString = "Ianuarie";
                            break;
                        case 2:
                            lunaString = "Februarie";
                            break;
                        case 3:
                            lunaString = "Martie";
                            break;
                        case 4:
                            lunaString = "Aprilie";
                            break;
                        case 5:
                            lunaString = "Mai";
                            break;
                        case 6:
                            lunaString = "Iunie";
                            break;
                        case 7:
                            lunaString = "Iulie";
                            break;
                        case 8:
                            lunaString = "Iulie";
                            break;
                        case 9:
                            lunaString = "Septembrie";
                            break;
                        case 10:
                            lunaString = "Octombrie";
                            break;
                        case 11:
                            lunaString = "Noiemebrie";
                            break;
                        case 12:
                            lunaString = "Decembrie";
                            break;
                    }

                    JOptionPane.showMessageDialog(RezervariGUI.super.rootPane, "Luna cu cele mai multe rezervari este: " + lunaString, "Luna", JOptionPane.INFORMATION_MESSAGE);


                    String query1 = "SELECT Nume, Prenume,DataPreluare, DataReturnare\n" +
                            "FROM Rezervari INNER JOIN Clienti on Rezervari.ClientID = Clienti.ClientID\n" +
                            "WHERE MONTH(DataPreluare) = " + luna;
                    PreparedStatement statement1 = connection.prepareStatement(query1);
                    ResultSet resultSet1 = statement1.executeQuery();

                    ResultSetMetaData resultSetMetaData1 = resultSet1.getMetaData();
                    DefaultTableModel model = (DefaultTableModel) table3.getModel();
                    model.setRowCount(0);

                    table3.setPreferredScrollableViewportSize(table3.getPreferredSize());
                    table3.setFillsViewportHeight(true);

                    table3.setMinimumSize(new Dimension(200, 500));

                    int cols = resultSetMetaData1.getColumnCount();
                    String[] colName = new String[cols];

                    System.out.println(cols);
                    for (int i = 0; i < cols; i++) {
                        colName[i] = resultSetMetaData1.getColumnName(i + 1);
                    }

                    model.setColumnIdentifiers(colName);

                    String nume, prenume;
                    Date datep, dater;

                    while (resultSet1.next()) {
                        nume = resultSet1.getString(1);
                        prenume = resultSet1.getString(2);
                        datep = resultSet1.getDate(3);
                        dater = resultSet1.getDate(4);
                        Object[] row = {nume, prenume, datep, dater};
                        model.addRow(row);
                    }

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }

    public void createTable() throws SQLException {
        Connection connection = conectare.conect();

        //interogari simple 1/6
        String statement = "SELECT Nume, Prenume,DataPreluare, DataReturnare\n" +
                "FROM Rezervari INNER JOIN Clienti on Rezervari.ClientID = Clienti.ClientID";
        PreparedStatement preparedStatement1 = connection.prepareStatement(statement);
        ResultSet resultSet = preparedStatement1.executeQuery();
        ResultSetMetaData resultSetMetaData1 = resultSet.getMetaData();
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0);


        table1.setPreferredSize(new Dimension(500, 300));
        table1.setPreferredScrollableViewportSize(table1.getPreferredSize());
        table1.setFillsViewportHeight(true);
        //table1.setRowHeight(25);

        int cols = resultSetMetaData1.getColumnCount();
        String[] colName = new String[cols];

        System.out.println(cols);
        for (int i = 0; i < cols; i++) {
            colName[i] = resultSetMetaData1.getColumnName(i + 1);
        }

        model.setColumnIdentifiers(colName);

        String nume, prenume;
        Date datep, dater;

        while (resultSet.next()) {
            nume = resultSet.getString(1);
            prenume = resultSet.getString(2);
            datep = resultSet.getDate(3);
            dater = resultSet.getDate(4);
            Object[] row = {nume, prenume, datep, dater};
            model.addRow(row);
        }
    }

    public static void main(String[] args) throws SQLException {
        RezervariGUI rez = new RezervariGUI();
        rez.createTable();
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
        rezervari = new JPanel();
        rezervari.setLayout(new GridLayoutManager(9, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(14, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        dataPreluare = new JTextField();
        panel1.add(dataPreluare, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        dataReturnare = new JTextField();
        panel1.add(dataReturnare, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        clientID = new JTextField();
        panel1.add(clientID, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        masinaID = new JTextField();
        panel1.add(masinaID, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        angajatID = new JTextField();
        panel1.add(angajatID, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Data Preluare");
        panel1.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Data Returnare");
        panel1.add(label2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("ClientID");
        panel1.add(label3, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("MasinaID");
        panel1.add(label4, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("AngajatID");
        panel1.add(label5, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        suma = new JTextField();
        suma.setText("");
        panel1.add(suma, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Suma ");
        panel1.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adaugaRezervareButton = new JButton();
        adaugaRezervareButton.setText("AdaugaRezervare");
        panel1.add(adaugaRezervareButton, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TABELEUTILEButton = new JButton();
        TABELEUTILEButton.setText("TABELE UTILE");
        panel1.add(TABELEUTILEButton, new GridConstraints(13, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        rezervari.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel3, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel3.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table2 = new JTable();
        scrollPane2.setViewportView(table2);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        INAPOIButton = new JButton();
        INAPOIButton.setText("INAPOI");
        panel4.add(INAPOIButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel4.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$("Times New Roman", Font.BOLD, 20, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        label7.setText("REZERVARI");
        panel4.add(label7, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$("Times New Roman", Font.BOLD, 20, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("TABEL MASINI LIBERE");
        panel5.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        VEZILUNACUCELEButton = new JButton();
        VEZILUNACUCELEButton.setText("VEZI LUNA CU CELE MAI MULTE REZERVARI");
        rezervari.add(VEZILUNACUCELEButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        panel6.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table3 = new JTable();
        scrollPane3.setViewportView(table3);
        final JLabel label9 = new JLabel();
        label9.setIcon(new ImageIcon(getClass().getResource("/resources/rent.png")));
        label9.setText("");
        rezervari.add(label9, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        rezervari.add(panel7, new GridConstraints(5, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        dataCautare = new JTextField();
        panel7.add(dataCautare, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Introduceti data de preluare:");
        panel7.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cautaButton = new JButton();
        cautaButton.setText("Cauta");
        panel7.add(cautaButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rezervari;
    }

}
