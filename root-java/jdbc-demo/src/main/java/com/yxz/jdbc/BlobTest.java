package com.yxz.jdbc;

import com.yxz.jdbc.entity.Customer;
import com.yxz.jdbc.util.JDBCUtils_V1;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.*;

/**
 * 测试使用PreparedStatement操作Blob类型的数据
 */
public class BlobTest {

    public static void main(String[] args) {
        System.out.println(BlobTest.class.getResource("").getFile());
        System.out.println(BlobTest.class.getClassLoader().getResource("").getFile());
        System.out.println(BlobTest.class.getResource("/").getFile());
    }

    @Test
    public void testFile() {

        System.out.println(new File("").getAbsoluteFile());
        System.out.println(BlobTest.class.getResource("").getFile());
        System.out.println(BlobTest.class.getClassLoader().getResource("").getFile());
        System.out.println(BlobTest.class.getResource("/").getFile());
    }

    //向数据表customers中插入Blob类型的字段
    @Test
    public void testInsert() throws Exception {
        Connection conn = JDBCUtils_V1.getConnection();
        String sql = "insert into customer(name,email,birth,photo)values(?,?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setObject(1, "袁浩");
        ps.setObject(2, "yuan@qq.com");
        ps.setObject(3, "1992-09-08");
        FileInputStream is = new FileInputStream(new File("girl.jpg"));
        ps.setBlob(4, is);

        ps.execute();

        JDBCUtils_V1.closeResource(conn, ps);

    }

    //查询数据表customer中Blob类型的字段
    @Test
    public void testQuery() {
        Connection conn = null;
        PreparedStatement ps = null;
        InputStream is = null;
        FileOutputStream fos = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils_V1.getConnection();
            String sql = "select id,name,email,birth,photo from customer where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 21);
            rs = ps.executeQuery();
            if (rs.next()) {
                //			方式一：
                //			int id = rs.getInt(1);
                //			String name = rs.getString(2);
                //			String email = rs.getString(3);
                //			Date birth = rs.getDate(4);
                //方式二：
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");

                Customer cust = new Customer(id, name, email, birth);
                System.out.println(cust);

                //将Blob类型的字段下载下来，以文件的方式保存在本地
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("beauty.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JDBCUtils_V1.closeResource(conn, ps, rs);
        }


    }

}
