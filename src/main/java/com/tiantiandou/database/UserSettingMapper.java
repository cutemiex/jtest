package com.tiantiandou.database;

import java.util.List;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public interface UserSettingMapper {
    List<UserSetting> selectUserSettingByKey(UserSetting userSetting);
}
