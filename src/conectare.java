import java.sql.*;

public class conectare {

    public static Connection conect(){
        String url = "jdbc:sqlserver://localhost:1433;DatabaseName=Proiect_Fatu_Cristian;encrypt=true;trustServerCertificate=true";
        String user = "caic";
        String pass = "cristianfatu1";
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url, user, pass);
            //testare conexiune
          /*  String sql = "SELECT * FROM Angajati";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                System.out.println(result.getString(2) + " " +  result.getString(3));//afisare coloana 2 si 3 din angajati
            }*/
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return connection;
    }
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection con = conect();
    }
}
