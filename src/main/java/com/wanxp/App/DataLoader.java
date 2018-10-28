package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

/**
 * 文件导入器
 */
public class DataLoader {
    private static Logger LOGGER = LogManager.getLogger(DataLoader.class);

    private static int STRING_BUILDER_SIZE = 4000000; // 缓存导入至SQL的数据的string builder的容量
    private static int BUFFERED_READER_MAX_LINE = 20000; // 每次导入的行数
    private static int TIME_LENGTH = 10;//文件名时间后缀长度

    private static String SQL_CREATE_TABLE_PREFIX = " CREATE TABLE if not exists `"; //创建表格SQL前缀
    private static String SQL_CREATE_TABLE_SUBFIX = "` (\n" +
            "    `nas_port_id` varchar(125) NOT NULL,\n" +
            "    `ip_address` varchar(50),\n" +
            "    `page_type` smallint(6) NOT NULL,\n" +
            "    `datetime` varchar(30) DEFAULT NULL,\n" +
            "    `username` varchar(50) DEFAULT NULL,\n" +
            "    `custom_name` varchar(50) DEFAULT NULL\n" +
            "    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";//创建表格SQL后缀
    private static String SQL_DROP_TABLE = " DROP TABLE IF EXISTS ";
    private static String SQL_IMPORT_TABLE_PREFIEX = " LOAD DATA LOCAL INFILE 'datafile' INTO TABLE `";//导入数据SQL前缀
    private static String SQL_IMPORT_TABLE_SUFIX = "`\n" +
            "FIELDS TERMINATED BY ']  ['\n" +
            "LINES STARTING BY 'NasPortId[' TERMINATED BY ']\\n'";//导入数据SQL后缀

    //添加主键ID ALTER TABLE table1 ADD `id` BIGINT(20) NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY(`id`);
    private static String SQL_ALTER_TABLE_PREFIX = " ALTER TABLE `";
    private static String SQL_ALTER_TABLE_ADD_INDEX_SUFIX = "` ADD `id` BIGINT(20) NOT NULL AUTO_INCREMENT," +
            " ADD PRIMARY KEY(`id`)," +
            " ADD INDEX NAS_PORT_ID_INDEX(`nas_port_id`)," +
            " ADD INDEX PAGE_TYPE_INDEX(`page_type`) ";

    //复制表语句INSERT INTO table1 SELECT * FROM table2;
    private static String SQL_INSERT_INTO = " INSERT INTO ";
    private static String SQL_SELECT_FROM = " SELECT * FROM ";

    //文件路径
    private String path = "";
    public DataLoader(String path) {
        this.path = path;
    }


    /**
     * 上传数据
     * @return
     */
    public Queue<String> uploadData() {
        Queue<String> tableNames = new LinkedList<>();
        File pathFile = new File(path);
        tableNames = uploadHourData(pathFile.listFiles());
        uploadDaliyData(pathFile.listFiles());
        return tableNames;
    }

    /**
     * 添加索引
     * @param tableNames
     */
    public void addIndex(Queue<String> tableNames) {
        for (String tableName : tableNames) {
            try {
                addIndexToTable(tableName);
            } catch (SQLException e) {
                LOGGER.error(String.format("Table %s add index failed.", tableName), e);
            }
        }
    }

    /**
     * 读取文件并上传数据
     * @param files
     */
    public Queue<String> uploadHourData(File[] files) {
        Queue<String> tableNames = new LinkedList<>();
        Arrays.stream(files).filter(x ->
                x.isFile() && (x.getName().lastIndexOf("portal.log.") > -1)
        ).forEach(file -> {
                    LOGGER.info("File " + file.getName() + " is handle");
                    try {
                         tableNames.offer(loadFileToSQLByLines(file));
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                }
        );
        return tableNames;
    }

    /**
     * 读取文件,导入至mysql
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    private String loadFileToSQLByLines(File file) throws IOException, SQLException {
        //读取文件
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder(STRING_BUILDER_SIZE);
        String tempString = null;
        //初始化连接池从数据库中获取数据库连接
        PoolManager poolManager = PoolManager.getInstance();
        Connection connection = poolManager.getConnection();

        String tabName = "pv_hour_" + file.getName().substring(file.getName().length() - TIME_LENGTH);
        dropTable(tabName, connection);
        createTable(tabName, connection);
        com.mysql.jdbc.PreparedStatement jdbcPs = createPreparedStatement(tabName, connection);
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
                uploadDataToDatabase(jdbcPs, sb);
                line = 0;
                sb.setLength(0);
            }
        }
        uploadDataToDatabase(jdbcPs, sb);
        poolManager.returnConnection(connection);
//        Runtime.getRuntime().gc();
        return tabName;
    }

    /**
     * 创建数据表
     * @param tabName
     * @param connection
     * @return
     * @throws SQLException
     */
    private boolean createTable(String tabName, Connection connection) throws SQLException {
        return connection.createStatement().execute(SQL_CREATE_TABLE_PREFIX + tabName + SQL_CREATE_TABLE_SUBFIX);
    }

    /**
     * 若存在重复表则删除
     * @param tabName
     * @param connection
     * @return
     * @throws SQLException
     */
    private boolean dropTable(String tabName, Connection connection) throws SQLException {
        return connection.createStatement().execute(SQL_DROP_TABLE + tabName);

    }

    /**
     * 创建预导入声明
     * @param tabName
     * @param connection
     * @return
     * @throws SQLException
     */
    private com.mysql.jdbc.PreparedStatement createPreparedStatement(String tabName, Connection connection) throws SQLException {
        String sqlImport = SQL_IMPORT_TABLE_PREFIEX + tabName + SQL_IMPORT_TABLE_SUFIX;
        PreparedStatement ps = connection.prepareStatement(sqlImport);
        com.mysql.jdbc.PreparedStatement jdbcPs = null;
        if (ps.isWrapperFor(com.mysql.jdbc.Statement.class)) {
            jdbcPs = ps.unwrap(com.mysql.jdbc.PreparedStatement.class);
        }
        return jdbcPs;
    }

    /**
     * 根据String更新数据库
     * @param ps
     * @param sb
     * @return i 导入数量
     */
    private int uploadDataToDatabase(com.mysql.jdbc.PreparedStatement ps, StringBuilder sb) {
        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
        int i = -1;
        try {
            ps.setLocalInfileInputStream(is);
            i = ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("load file into table failed.", e);
        }
        return i;
    }

    /**
     * 为表增加主键
     * @param tableName
     * @param connection
     * @return
     * @throws SQLException
     */
    private boolean addIndexToTable(String tableName, Connection connection) throws SQLException {
        String sql = SQL_ALTER_TABLE_PREFIX + tableName + SQL_ALTER_TABLE_ADD_INDEX_SUFIX;
        Statement statment = connection.createStatement();
        return statment.execute(sql);
    }

    /**
     * 为表增加主键
     * @param tableName
     * @return
     * @throws SQLException
     */
    private void addIndexToTable(String tableName) throws SQLException {
        LOGGER.info(String.format("Table %s add primary key (id) , index key (nas_port_id), index key (page_type)."));
        PoolManager poolManager = PoolManager.getInstance();
        Connection connection = poolManager.getConnection();
        addIndexToTable(tableName, connection);
        poolManager.returnConnection(connection);
    }

    /**
     * 加载日数据
     * @param files
     */
    private void uploadDaliyData(File[] files) {
        Connection connection = PoolManager.getInstance().getConnection();
        Stream stream = Arrays.stream(files).filter(x ->
                x.isFile() && (x.getName().lastIndexOf("portal.log.") > -1)
        );
        String firstName = ((File) stream.findFirst().get()).getName();
        String daliyTableName = "pv_day_" + firstName.substring(firstName.length() - TIME_LENGTH, firstName.length() - 2);
        try {
            dropTable(daliyTableName, connection);
            createTable(daliyTableName, connection);
            Arrays.stream(files).filter(x ->
                    x.isFile() && (x.getName().lastIndexOf("portal.log.") > -1)
            ).forEach(file -> {
                String fileName = file.getName();
                String tableName = "pv_hour_" + fileName.substring(fileName.length() - TIME_LENGTH);
                try {
                    LOGGER.info(String.format("Table copying : %s to %s ", tableName, daliyTableName));
                    copyDataToDaliyTable(tableName, daliyTableName, connection);
                } catch (SQLException e) {
                    LOGGER.error(String.format("copy %s to %s failed", tableName, daliyTableName), e);
                }
            });
        } catch (SQLException e) {
            LOGGER.error(String.format("create %s failed", daliyTableName), e);
        }



    }

    /**
     * 复制表数据
     * @param sourceTableName
     * @param targetTableName
     * @param connection
     * @throws SQLException
     */
    private void copyDataToDaliyTable(String sourceTableName, String targetTableName, Connection connection) throws SQLException {
        String sql = SQL_INSERT_INTO + targetTableName + SQL_SELECT_FROM + sourceTableName;
        Statement statment = connection.createStatement();
        statment.execute(sql);
    }
}
