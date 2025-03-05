package com.yxz.jdbc.pool;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yxz.jdbc.util.JDBCUtils_V1;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class C3P0Test {

    //方式一：
    @Test
    public void testGetConnection_1() throws Exception {
        //获取c3p0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass(JDBCUtils_V1.DRIVER_CLASS);
        cpds.setJdbcUrl(JDBCUtils_V1.URL);
        cpds.setUser(JDBCUtils_V1.USER);
        cpds.setPassword(JDBCUtils_V1.PASSWORD);
        //通过设置相关的参数，对数据库连接池进行管理：
        //设置初始时数据库连接池中的连接数
        cpds.setInitialPoolSize(10);

        Connection conn = cpds.getConnection();
        System.out.println(conn);

        //销毁c3p0数据库连接池
//		DataSources.destroy( cpds );
    }

    //方式二：使用配置文件
    @Test
    public void testGetConnection_2() throws SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");
        Connection conn = cpds.getConnection();
        System.out.println(conn);
    }

}
