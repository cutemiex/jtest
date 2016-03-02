package com.tiantiandou.mybatis;

import com.dustpan.common.domain.CommonDomain;

/**
 * Created by tommy on 2015/11/14.
 */
public class MobileBanner extends CommonDomain{
    private Long shopId;

    private Long rewardId;

    private Long rewardTaskId;

    private String taobaoPicUrl;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public Long getRewardTaskId() {
        return rewardTaskId;
    }

    public void setRewardTaskId(Long rewardTaskId) {
        this.rewardTaskId = rewardTaskId;
    }

    public String getTaobaoPicUrl() {
        return taobaoPicUrl;
    }

    public void setTaobaoPicUrl(String taobaoPicUrl) {
        this.taobaoPicUrl = taobaoPicUrl;
    }
}
