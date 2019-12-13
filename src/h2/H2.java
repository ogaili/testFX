package h2;


import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class H2 {

    @Autowired
    private static JdbcTemplate jdbcTemplate;

    private static final String dbName = "test6";

    static {


        try {
            jdbcTemplate.queryForMap("select * from "+dbName+" limit 1");
        } catch (DataAccessException e) {
            try {
                System.out.println("第一次创建数据库");
                jdbcTemplate.execute("CREATE TABLE "+dbName+"(id int(50) PRIMARY KEY AUTO_INCREMENT,phone VARCHAR(20))");
                jdbcTemplate.execute("INSERT INTO "+dbName+" VALUES('1','')");
            } catch (DataAccessException e1) {
                System.out.println("有表但是没数据");
            }
        }

    }

    public static void main(String[] args) throws Exception {
       /* // 加载H2数据库驱动
        Class.forName(DRIVER_CLASS);
        // 根据连接URL，用户名，密码获取数据库连接
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        //如果存在USER_INFO表就先删除USER_INFO表
        stmt.execute("DROP TABLE IF EXISTS USER_INFO");
        //创建USER_INFO表
        stmt.execute("CREATE TABLE USER_INFO(id VARCHAR(36) PRIMARY KEY,name VARCHAR(100),sex VARCHAR(4))");
        //新增
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三1','男')");
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三2','男')");
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三3','男')");
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三4','女')");
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三5','男')");
        stmt.executeUpdate("INSERT INTO USER_INFO VALUES('" + UUID.randomUUID()+ "','张三6','男')");
        //删除
        stmt.executeUpdate("DELETE FROM USER_INFO WHERE name='张三6'");
        //修改
        stmt.executeUpdate("UPDATE USER_INFO SET name='张三55' WHERE name='张三5'");
        //查询
        ResultSet rs = stmt.executeQuery("SELECT * FROM USER_INFO");
        //遍历结果集
        while (rs.next()) {
            System.out.println(rs.getString("id") + "," + rs.getString("name")+ "," + rs.getString("sex"));
        }
        //释放资源
        stmt.close();
        //关闭连接
        conn.close();*/



    }

    public void save(Set<String> set){
        for (String s : set) {
            jdbcTemplate.update("INSERT INTO "+dbName+" values(?,?)",null,s);
        }

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM "+dbName);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
    }

    /**
     * 查询所有
     * @return
     */
    public List<String> findAll(){
        List<String> list = jdbcTemplate.query("SELECT phone FROM "+dbName, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });
        return list;
    }

    /**
     * 清空表数据
     */
    public void deleteAll(){
        jdbcTemplate.update("TRUNCATE TABLE "+dbName);
    }

    /**
     * 按手机号精确查询
     */
    public List<String> findList(String phone){
        List<String> list = jdbcTemplate.query("SELECT phone FROM "+dbName+" where phone = ? ", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        },phone);
        return list;
    }

}
