package com.wanxp.App;

import java.sql.*;

public class MySQLConnecttor {
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://localhost:3306/testimport?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static String USER = "root";
    private static String PASSWORD = "9111";

    private static MySQLConnecttor connecttor = null;

    private MySQLConnecttor() {
        init();
    }

    public static MySQLConnecttor getIntstance() {
        if (connecttor == null)
            connecttor = new MySQLConnecttor();
        return connecttor;
    }

    private void init() {

    }

    public void execute(String sql) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("链接数据库中...");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("实例化Statement对象");
            statement = connection.createStatement();
            System.out.println("执行sql");
            statement.execute(sql);
            System.out.println("执行sql成功");
            statement.close();
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public  query(String sql) {
//        try {
//            System.out.println("执行sql");
//            statement.execute(sql);
//            System.out.println("执行sql成功");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


}
