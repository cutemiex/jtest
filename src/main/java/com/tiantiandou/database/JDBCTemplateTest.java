package com.tiantiandou.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.taobao.tddl.jdbc.group.TGroupDataSource;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class JDBCTemplateTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("JDBCTemplateTest");
    private static final int TEST_USER_ID = 24006;

    private JDBCTemplateTest() {

    }

    public static void main(String[] args) {
        String sql = "select * from user_setting where id = ?";
        TGroupDataSource datasource = new TGroupDataSource();
        datasource.setAppName("CDO_BASE_APP");
        datasource.setDbGroupKey("CDO_BASE_GROUP");
        datasource.init();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        UserSetting us = (UserSetting) jdbcTemplate.queryForObject(sql, new Object[] { TEST_USER_ID },
                new BeanPropertyRowMapper<UserSetting>(UserSetting.class));
        LOGGER.debug("User setting  value is : " + us.getSettingValue());
    }
}
