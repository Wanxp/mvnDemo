package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadDataToSQL {
    private static Logger LOGGER = LogManager.getLogger(LoadDataToSQL.class);

    private static String SQL_CREATE_TABLE_PREFIX = "CREATE TABLE if not exists  `pv_hour_";
    private static String SQL_CREATE_TABLE_SUBFIX = "` (\n" +
            "    `nas_port_id` varchar(125) NOT NULL,\n" +
            "    `ip_address` varchar(50),\n" +
            "    `page_type` smallint(6) NOT NULL,\n" +
            "    `datetime` varchar(30) DEFAULT NULL,\n" +
            "    `username` varchar(50) DEFAULT NULL,\n" +
            "    `custom_name` varchar(50) DEFAULT NULL\n" +
            "    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";
    private static String SQL_IMPORT_TABLE_PREFIEX = " LOAD DATA LOCAL INFILE 'datafile' INTO TABLE `pv_hour_" ;
    private static String SQL_IMPORT_TABLE_SUFIX = "`\n" +
            "FIELDS TERMINATED BY ']  ['\n" +
            "LINES STARTING BY 'NasPortId[' TERMINATED BY ']\\n';";

    public static void loadFileToSQLByLines(File file) throws IOException, SQLException {
        int[]
        long start = System.currentTimeMillis();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        int line = 0;
        StringBuilder sb = new StringBuilder(4000000 );
        String tempString = null;
        PoolManager poolManager= PoolManager.getInstance();
        poolManager.initPool();
        Connection connection = poolManager.getConnection();
        String sql = SQL_CREATE_TABLE_PREFIX + file.getName().substring(file.getName().length() - 10)
                + SQL_CREATE_TABLE_SUBFIX;
        String sqlImport = SQL_IMPORT_TABLE_PREFIEX + file.getName().substring(file.getName().length() - 10)
                + SQL_IMPORT_TABLE_SUFIX;
        Statement statement = connection.createStatement();
        statement.execute(sql);
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }
        while ((tempString = reader.readLine()) != null) {
            tempString = tempString.replaceAll("Ip-address", " ")
                    .replaceAll("pageType", " ")
                    .replaceAll("datetime", " ")
                    .replaceAll("username", " ")
                    .replaceAll("customName", " ");
            sb.append(tempString + "\n");
            line++;
            if (line > 20000) {
                byte[] bytes = sb.toString().getBytes();
                InputStream is = new ByteArrayInputStream(bytes);
                try {
                    jdbcPs.setLocalInfileInputStream(is);
                    jdbcPs.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.error("load file into table failed.", e);
                }finally {
                    line = 0;
                    sb = null;
                    sb = new StringBuilder(4000000);
                }
            }
        }

        System.out.println(System.currentTimeMillis() - start);
    }
}
