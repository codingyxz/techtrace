package com.yxz.jdbc.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 使用数据库连接池技术、以及dbutils工具包
 */
public class JDBCUtils_V2 {

    public static void main(String[] args) throws Exception {
        System.out.println(getConnection_c3p0());
        System.out.println(getConnection_dbcp());
        System.out.println(getConnection_druid());
    }

    //数据库连接池只需提供一个即可。
    private static DataSource ds_c3p0;

    static {
        ds_c3p0 = new ComboPooledDataSource("hellc3p0");
    }

    public static Connection getConnection_c3p0() throws SQLException {
        Connection conn = ds_c3p0.getConnection();
        return conn;
    }

    //创建一个DBCP数据库连接池
    private static DataSource ds_dbp;

    static {
        try {
            Properties pros = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
            pros.load(is);
            ds_dbp = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection_dbcp() throws Exception {
        Connection conn = ds_dbp.getConnection();
        return conn;
    }

    /**
     * 使用Druid数据库连接池技术
     */
    private static DataSource ds_druid;

    static {
        try {
            Properties pros = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pros.load(is);
            ds_druid = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection_druid() throws SQLException {
        Connection conn = ds_druid.getConnection();
        return conn;
    }


    /**
     * 使用dbutils.jar中提供的DbUtils工具类，实现资源的关闭
     */
    public static void closeResource_dbutils(Connection conn, Statement ps, ResultSet rs) {
        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);
    }

}
