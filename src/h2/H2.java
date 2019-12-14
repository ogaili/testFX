package h2;


import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class H2 {
    //数据库连接URL
    //private static final String JDBC_URL = "jdbc:h2:E:/Test/h2/bin/test";
    private static final String JDBC_URL = "jdbc:h2:~/test";
    //连接数据库时使用的用户名
    private static final String USER = "sa";
    //连接数据库时使用的密码
    private static final String PASSWORD = "sa";
    //连接H2数据库时使用的驱动类，org.h2.Driver这个类是由H2数据库自己提供的，在H2数据库的jar包中可以找到
    private static final String DRIVER_CLASS="org.h2.Driver";

    private static JdbcTemplate jdbcTemplate;

    private static final String dbName = "test6";

    static {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setURL(JDBC_URL);
        jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            jdbcTemplate.queryForMap("select * from "+dbName+" limit 1");
        } catch (DataAccessException e) {
            try {
                System.out.println("第一次创建数据库");
                jdbcTemplate.execute("CREATE TABLE "+dbName+"(id int(20) PRIMARY KEY AUTO_INCREMENT,phone VARCHAR(20))");
                jdbcTemplate.execute("INSERT INTO "+dbName+" VALUES('1','手机号')");
            } catch (DataAccessException e1) {
                System.out.println("有表但是没数据");
            }
        }

    }

    public void save(Set<String> set){
        String sql = "INSERT INTO "+dbName+" (phone) values(?)";
        jdbcTemplate.batchUpdate(sql, set, set.size(), new ParameterizedPreparedStatementSetter<String>() {
            @Override
            public void setValues(PreparedStatement preparedStatement, String s) throws SQLException {
                preparedStatement.setString(1,s);
            }
        });

       /* List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM "+dbName);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }*/
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
        list.add("一共"+list.size()+"条数据");
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
