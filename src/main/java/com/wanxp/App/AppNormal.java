package com.wanxp.App;

/**
 * Hello world!
 *
 */
public class AppNormal
{
    public static void main( String[] args )
    {
        MySQLConnecttor mySQLConnecttor = MySQLConnecttor.getIntstance();
        mySQLConnecttor.execute("SHOW TABLES");
        mySQLConnecttor.execute("create table `test` (`id` int(11) default 0, `name` varchar(64) default '');");

    }
}
