package com.yxz.jdbc;

import com.yxz.jdbc.entity.Customer;
import com.yxz.jdbc.util.JDBCUtils_V1;
import com.yxz.jdbc.util.JDBCUtils_V2;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * commons-dbutils 是 Apache 组织提供的一个开源 JDBC工具类库,封装了针对于数据库的增删改查操作
 */
public class DbUtilsTest {

    //测试插入
    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "insert into customer(name,email,birth)values(?,?,?)";
            int insertCount = runner.update(conn, sql, "ccccc", "ccccc@126.com", "1997-09-08");
            System.out.println("添加了" + insertCount + "条记录");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }

    }

    //测试查询
    /*
     * BeanHandler:是ResultSetHandler接口的实现类，用于封装表中的一条记录。
     */
    @Test
    public void testQuery_One() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select id,name,email,birth from customer where id = ?";
            BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);
            Customer customer = runner.query(conn, sql, handler, 23);
            System.out.println(customer);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }

    }

    /*
     * BeanListHandler:是ResultSetHandler接口的实现类，用于封装表中的多条记录构成的集合。
     */
    @Test
    public void testQuery_list() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select id,name,email,birth from customer where id < ?";
            BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);

            List<Customer> list = runner.query(conn, sql, handler, 23);
            list.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }

    }

    /*
     * MapHandler:是ResultSetHandler接口的实现类，对应表中的一条记录。
     * 将字段及相应字段的值作为map中的key和value
     */
    @Test
    public void testQuery_map() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select id,name,email,birth from customer where id = ?";
            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(conn, sql, handler, 23);
            System.out.println(map);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }
    }

    /*
     * MapListHandler:是ResultSetHandler接口的实现类，对应表中的多条记录。
     * 将字段及相应字段的值作为map中的key和value。将这些map添加到List中
     */
    @Test
    public void testQuery_mapList() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select id,name,email,birth from customer where id < ?";

            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> list = runner.query(conn, sql, handler, 23);
            list.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }
    }

    /*
     * ScalarHandler:用于查询特殊值
     */
    @Test
    public void testQuery_scalar_count() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select count(*) from customer";

            ScalarHandler handler = new ScalarHandler();

            Long count = (Long) runner.query(conn, sql, handler);
            System.out.println(count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }
    }

    @Test
    public void testQuery_scalar_max() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select max(birth) from customer";

            ScalarHandler handler = new ScalarHandler();
            Date maxBirth = (Date) runner.query(conn, sql, handler);
            System.out.println(maxBirth);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }
    }

    /*
     * 自定义ResultSetHandler的实现类
     */
    @Test
    public void testQuery_customHandler() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils_V2.getConnection_druid();
            String sql = "select id,name,email,birth from customer where id = ?";
            ResultSetHandler<Customer> handler = new ResultSetHandler<Customer>() {

                @Override
                public Customer handle(ResultSet rs) throws SQLException {
//					return new Customer(12, "成龙", "Jacky@126.com", new Date(234324234324L));

                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        Date birth = rs.getDate("birth");
                        Customer customer = new Customer(id, name, email, birth);
                        return customer;
                    }
                    return null;
                }
            };
            Customer customer = runner.query(conn, sql, handler, 23);
            System.out.println(customer);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils_V1.closeResource(conn, null);
        }

    }

}
