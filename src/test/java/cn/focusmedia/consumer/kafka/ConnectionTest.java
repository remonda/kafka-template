package cn.focusmedia.consumer.kafka;

import org.junit.Test;

import java.sql.*;

public class ConnectionTest {
    public static void main(String[] args) {

    }
    @Test
    public void connectionTest(){
        Connection connection=null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://192.168.7.10:1433;DatabaseName=DB_FDO_Monitor",
                    "fwo_reader",
                    "1C441CA7@D2f7");
            PreparedStatement pstm = connection.prepareStatement("select top 10 *from dbo.device_status_table");
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }

            System.out.println(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
