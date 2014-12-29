package com.tiantiandou.database;

import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.tddl.jdbc.group.TGroupDataSource;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class MybatisTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("MybatisTest");

    private MybatisTest() {

    }

    public static void main(String[] args) throws Exception {
        TGroupDataSource datasource = new TGroupDataSource();
        datasource.setAppName("CDO_BASE_APP");
        datasource.setDbGroupKey("CDO_BASE_GROUP");
        datasource.init();

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, datasource);
        // Configuration configuration = new Configuration(environment);

        UserSetting userSetting = new UserSetting();
        userSetting.setUserId("064460");
        // configuration.addMapper(UserSettingMapper.class);

        String resource = "com/tiantiandou/database/mybatis-config.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        sqlSessionFactory.getConfiguration().setEnvironment(environment);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserSettingMapper userSettingMapper = sqlSession.getMapper(UserSettingMapper.class);
            List<UserSetting> uss = userSettingMapper.selectUserSettingByKey(userSetting);
            LOGGER.debug("value : " + uss.get(0).getSettingValue());
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}
