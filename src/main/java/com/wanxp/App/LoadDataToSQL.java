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


    /**
     * flie:portal.log.2016031920, createTableTime:321, tempStringTime:11427, tempLoadDataTime:6348
     * flie:portal.log.2016031921, createTableTime:16, tempStringTime:8530, tempLoadDataTime:4873
     * flie:portal.log.2016031922, createTableTime:18, tempStringTime:6694, tempLoadDataTime3886
     * flie:portal.log.2016031923, createTableTime:16, tempStringTime:5700, tempLoadDataTime3425
     * 总耗时: 33439
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    public static void loadFileToSQLByLines(File file) throws IOException, SQLException {
        long tempCreateTime = 0l;
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
        tempCreateTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }
        long tempStringTime = 0l;
        long tempLoadDataTime = 0l;
        while ((tempString = reader.readLine()) != null) {
            tempString = tempString.replaceAll("Ip-address", " ")
                    .replaceAll("pageType", " ")
                    .replaceAll("datetime", " ")
                    .replaceAll("username", " ")
                    .replaceAll("customName", " ");
            sb.append(tempString + "\n");
            line++;
            if (line > 20000) {
                tempStringTime += System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
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
                tempLoadDataTime += System.currentTimeMillis() - start;
            }
        }
        System.out.println("--------flie:" + file.getName() + ", createTableTime:" + tempCreateTime
                + ", tempStringTime:" + tempStringTime
                + ", tempLoadDataTime" + tempLoadDataTime + "----------");
    }
}
