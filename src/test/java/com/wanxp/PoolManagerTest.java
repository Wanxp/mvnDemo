package com.wanxp;

import com.wanxp.App.connecter.PoolManager;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class PoolManagerTest extends TestCase {
    public void testPoolManagerCreate() {
        long start = System.currentTimeMillis();
        PoolManager pool = PoolManager.getInstance();
        pool.initPool();


        for (int i = 0;i < 10;i++) {

            Thread thread = new Thread(()-> {
                    Connection connection = pool.getConnection();
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet result = statement.executeQuery("show databases");
                        while (result.next()) {
                            String dbName = result.getString("Database");
                            System.out.println(dbName);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("connection:"+connection);
            });
            thread.start();
        }
        System.out.println(System.currentTimeMillis() - start);

    }

    public void testPoolCreateTable() {
        long start = System.currentTimeMillis();
        PoolManager pool = PoolManager.getInstance();
        pool.initPool();
        Connection connection = pool.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0;i < 10;i++) {

            try {
                statement.execute("CREATE TABLE if not exists pv_hour_" + i + " (\n" +
                        "                nas_port_id varchar(125) NOT NULL,\n" +
                        "                ip_address varchar(50),\n" +
                        "                page_type smallint(6) NOT NULL,\n" +
                        "                datetime varchar(30) DEFAULT NULL,\n" +
                        "                username varchar(50) DEFAULT NULL,\n" +
                        "                custom_name varchar(50) DEFAULT NULL\n" +
                        "                ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    public void testLoadFile() {
        PoolManager poolManager = PoolManager.getInstance();
        poolManager.initPool();
        Connection connection = poolManager.getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute("LOAD DATA LOCAL INFILE 'file.txt'"
                    + "INTO TABLE pv_hour_0 " +
                    " LINES START BY '' +" +
                    "LINES TERMINATED BY '\\n'");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
