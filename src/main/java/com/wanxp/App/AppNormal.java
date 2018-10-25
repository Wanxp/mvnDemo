package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class AppNormal
{
    public static void main( String[] args ) throws SQLException {
        long start = System.currentTimeMillis();
        PoolManager pool = PoolManager.getInstance();
        pool.initPool();
        for (int i = 0;i < 10;i++) {
            StringBuilder sb = new StringBuilder();
               sb.append("CREATE TABLE if not exists  `pv_hour_").append(i).append("` (\n" +
                    "`nas_port_id` varchar(125) NOT NULL,\n" +
                    "`ip_address` varchar(50),\n" +
                    "`page_type` smallint(6) NOT NULL,\n" +
                    "`datetime` varchar(30) DEFAULT NULL,\n" +
                    "`username` varchar(50) DEFAULT NULL,\n" +
                    "`custom_name` varchar(50) DEFAULT NULL\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n");
            Connection connection = pool.getConnection();
            PreparedStatement statement = connection.prepareStatement(sb.toString());
            statement.execute();
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
