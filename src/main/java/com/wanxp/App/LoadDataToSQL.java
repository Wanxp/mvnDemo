package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import com.wanxp.App.connecter.file.ReplacingInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoadDataToSQL {
    private static Logger LOGGER = LogManager.getLogger(LoadDataToSQL.class);

    private static String SQL_CREATE_TABLE_PREFIX = "CREATE TABLE if not exists `"; //创建表格SQL前缀
    private static String SQL_CREATE_TABLE_SUBFIX = "` (\n" +
            "    `nas_port_id` varchar(125) NOT NULL,\n" +
            "    `ip_address` varchar(50),\n" +
            "    `page_type` smallint(6) NOT NULL,\n" +
            "    `datetime` varchar(30) DEFAULT NULL,\n" +
            "    `username` varchar(50) DEFAULT NULL,\n" +
            "    `custom_name` varchar(50) DEFAULT NULL\n" +
            "    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";//创建表格SQL后缀
    private static String SQL_IMPORT_TABLE_PREFIEX = " LOAD DATA LOCAL INFILE 'datafile' INTO TABLE `" ;//导入数据SQL前缀
    private static String SQL_IMPORT_TABLE_SUFIX = "`\n" +
            "FIELDS TERMINATED BY ']  ['\n" +
            "LINES STARTING BY 'NasPortId[' TERMINATED BY ']\\n';";//导入数据SQL后缀

    private static int STRING_BUILDER_SIZE = 4000000; // 缓存导入至SQL的数据的string builder的容量
    private static int BUFFERED_READER_MAX_LINE = 20000; // 每次导入的行数

    private static byte[] STRING_BEING_REPLACED_BYTES_1 = "Ip-address".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_2 = "pageType".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_3 = "datetime".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_4 = "username".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_5 = "customName".getBytes();
    private static byte[] STRING_REPLACE_TO_BYTES_1 = " ".getBytes();

    /**
     * 读取文件,导入至mysql
     * 此方法文件读取耗时:
     * flie:portal.log.2016031920, createTableTime:321, tempStringTime:11427, tempLoadDataTime:6348
     * flie:portal.log.2016031921, createTableTime:16, tempStringTime:8530, tempLoadDataTime:4873
     * flie:portal.log.2016031922, createTableTime:18, tempStringTime:6694, tempLoadDataTime3886
     * flie:portal.log.2016031923, createTableTime:16, tempStringTime:5700, tempLoadDataTime3425
     * 总耗时: 47816
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    public static void loadFileToSQLByLines(File file) throws IOException, SQLException {
        //读取文件
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder(STRING_BUILDER_SIZE );
        String tempString = null;
        //初始化连接池从数据库中获取数据库连接
        //初始化连接池
        PoolManager poolManager = PoolManager.getInstance();
        Connection connection = poolManager.getConnection();
        String tabName = "pv_hour_" + file.getName().substring(file.getName().length() - 10);
        String sql = SQL_CREATE_TABLE_PREFIX + tabName + SQL_CREATE_TABLE_SUBFIX;
        String sqlImport = SQL_IMPORT_TABLE_PREFIEX + tabName + SQL_IMPORT_TABLE_SUFIX;
        connection.createStatement().execute(sql);
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }
        int line = 0;
        InputStream is = null;
        while ((tempString = reader.readLine()) != null) {
            tempString = tempString.replaceAll("Ip-address", " ")
                    .replaceAll("pageType", " ")
                    .replaceAll("datetime", " ")
                    .replaceAll("username", " ")
                    .replaceAll("customName", " ");
            sb.append(tempString + "\n");
            line++;
            if (line > BUFFERED_READER_MAX_LINE) {
                is = new ByteArrayInputStream(sb.toString().getBytes());
                try {
                    jdbcPs.setLocalInfileInputStream(is);
                    jdbcPs.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.error("load file into table failed.", e);
                }finally {
                    line = 0;
//                    sb = null;
                    sb.setLength(0);
//                    sb = new StringBuilder(STRING_BUILDER_SIZE);
                }
            }
        }
        is= new ByteArrayInputStream(sb.toString().getBytes());
        try {
            jdbcPs.setLocalInfileInputStream(is);
            jdbcPs.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("load file into table failed.", e);
        }finally {
            sb = null;// 48164 4个文件
//            sb.setLength(0);//49230 4个文件
            poolManager.returnConnection(connection);
        }
    }

    /**
     * 读取文件,导入至mysql
     * 此方法文件读取耗时:
     * flie:portal.log.2016031920, createTableTime:321, tempStringTime:11427, tempLoadDataTime:6348
     * flie:portal.log.2016031921, createTableTime:16, tempStringTime:8530, tempLoadDataTime:4873
     * flie:portal.log.2016031922, createTableTime:18, tempStringTime:6694, tempLoadDataTime3886
     * flie:portal.log.2016031923, createTableTime:16, tempStringTime:5700, tempLoadDataTime3425
     * 总耗时: 43870
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    public static void loadFileToSQLByLinesOpr1(File file) throws IOException, SQLException {
        //读取文件
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder(STRING_BUILDER_SIZE);
        String tempString = null;

        //初始化连接池
        PoolManager poolManager = PoolManager.getInstance();
        Connection connection = poolManager.getConnection();
        String tabName = "pv_hour_" + file.getName().substring(file.getName().length() - 10);
        String sql = SQL_CREATE_TABLE_PREFIX + tabName + SQL_CREATE_TABLE_SUBFIX;
        String sqlImport = SQL_IMPORT_TABLE_PREFIEX + tabName + SQL_IMPORT_TABLE_SUFIX;
        connection.createStatement().execute(sql);
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }
        int line = 0;
        InputStream is = null;
        while ((tempString = reader.readLine()) != null) {
            sb.append(tempString + "\n");
            line++;
            if (line > BUFFERED_READER_MAX_LINE) {
                is = new ByteArrayInputStream(sb.toString().replaceAll("Ip-address", " ")
                        .replaceAll("pageType", " ")
                        .replaceAll("datetime", " ")
                        .replaceAll("username", " ")
                        .replaceAll("customName", " ").getBytes());
                try {
                    jdbcPs.setLocalInfileInputStream(is);
                    jdbcPs.executeUpdate();
                } catch (SQLException e) {
                    LOGGER.error("load file into table failed.", e);
                }finally {
                    line = 0;
                    sb = new StringBuilder(STRING_BUILDER_SIZE);
                }
            }
        }
        is = new ByteArrayInputStream(sb.toString().replaceAll("Ip-address", " ")
                .replaceAll("pageType", " ")
                .replaceAll("datetime", " ")
                .replaceAll("username", " ")
                .replaceAll("customName", " ").getBytes());
        try {
            jdbcPs.setLocalInfileInputStream(is);
            jdbcPs.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("load file into table failed.", e);
        }finally {
            poolManager.returnConnection(connection);
        }
    }


    /**
     * 分批次读取文件,导入至mysql
     * 此方法文件读取耗时:
     * flie:portal.log.2016031920, createTableTime:321, tempStringTime:11427, tempLoadDataTime:6348
     * flie:portal.log.2016031921, createTableTime:16, tempStringTime:8530, tempLoadDataTime:4873
     * flie:portal.log.2016031922, createTableTime:18, tempStringTime:6694, tempLoadDataTime3886
     * flie:portal.log.2016031923, createTableTime:16, tempStringTime:5700, tempLoadDataTime3425
     * 总耗时: 33439
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    public static void loadFileToSQLByBytes(File file) throws IOException, SQLException {
        //读取文件
        FileInputStream inputStream = new FileInputStream(file);
        StringBuilder sb = new StringBuilder(4000000 );
        //初始化连接池从数据库中获取数据库连接
        PoolManager poolManager = PoolManager.getInstance();
        Connection connection = poolManager.getConnection();
        String tabName = "pv_hour_" + file.getName().substring(file.getName().length() - 10);
        String sql = SQL_CREATE_TABLE_PREFIX + tabName + SQL_CREATE_TABLE_SUBFIX;
        String sqlImport = SQL_IMPORT_TABLE_PREFIEX + tabName + SQL_IMPORT_TABLE_SUFIX;
        connection.createStatement().execute(sql);
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }

        InputStream is =
                new ReplacingInputStream(
                new ReplacingInputStream(
                new ReplacingInputStream(
                new ReplacingInputStream(
                new ReplacingInputStream(
                inputStream,
                        STRING_BEING_REPLACED_BYTES_1, STRING_REPLACE_TO_BYTES_1 ),
                        STRING_BEING_REPLACED_BYTES_2, STRING_REPLACE_TO_BYTES_1 ),
                        STRING_BEING_REPLACED_BYTES_3, STRING_REPLACE_TO_BYTES_1 ),
                        STRING_BEING_REPLACED_BYTES_4, STRING_REPLACE_TO_BYTES_1 ),
                        STRING_BEING_REPLACED_BYTES_5, STRING_REPLACE_TO_BYTES_1 );
        try {
            jdbcPs.setLocalInfileInputStream(is);
            jdbcPs.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("load file into table failed.", e);
        }finally {
            poolManager.returnConnection(connection);
        }

        }
}
