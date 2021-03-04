package cn.focusmedia.consumer.tools;

import cn.focusmedia.consumer.config.GroupConsumerConfigur;
import cn.focusmedia.consumer.constant.ConsumerConstant;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionUtil {
    private static ComboPooledDataSource dataSource = null;
    private static GroupConsumerConfigur configur = GroupConsumerConfigur.getInstance();
    private final static Logger LOGGER = Logger.getLogger(ConnectionUtil.class);

    //加载配置
    static {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(configur.getStringValue(ConsumerConstant.C3P0_DRIVER_CLASS));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        dataSource.setJdbcUrl(configur.getStringValue(ConsumerConstant.C3P0_JDBC_URL));
        dataSource.setUser(configur.getStringValue(ConsumerConstant.C3P0_USER));
        dataSource.setPassword(configur.getStringValue(ConsumerConstant.C3P0_PASSWORD));
        dataSource.setCheckoutTimeout(configur.getIntegerValue(ConsumerConstant.C3P0_CHECK_TIMEOUT));
        dataSource.setInitialPoolSize(configur.getIntegerValue(ConsumerConstant.C3P0_INITIAL_POOL_SIZE));
        dataSource.setMaxPoolSize(configur.getIntegerValue(ConsumerConstant.C3P0_MAX_POOL_SIZE));
        dataSource.setMinPoolSize(configur.getIntegerValue(ConsumerConstant.C3P0_MIN_POOL_SIZE));
        dataSource.setMaxIdleTime(configur.getIntegerValue(ConsumerConstant.C3P0_MAX_IDLETIME));
        dataSource.setMaxStatements(configur.getIntegerValue(ConsumerConstant.C3P0_MAX_STATEMENT));
        dataSource.setIdleConnectionTestPeriod(configur.getIntegerValue(ConsumerConstant.C3P0_IDLE_TEST_PERIOD));

        LOGGER.info(dataSource);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    //获取连接
    public static synchronized Connection getConn() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static void close(PreparedStatement pstate) throws SQLException {
        if (pstate != null) {
            pstate.close();
        }
    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }
}
