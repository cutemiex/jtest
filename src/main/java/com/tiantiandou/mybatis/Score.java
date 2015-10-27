package com.tiantiandou.mybatis;

import com.dustpan.common.domain.CommonDomain;

/**
 * Created by tommy on 2015/9/17.
 */
public class Score  extends CommonDomain{

    private Long shopId;

    private Long trafficScore;  //流量积分

    private Long bannerScore;   //宣传/担保积分?

    private Long inviteScore;   //推广积分

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getTrafficScore() {
        return trafficScore;
    }

    public void setTrafficScore(Long trafficScore) {
        this.trafficScore = trafficScore;
    }

    public Long getBannerScore() {
        return bannerScore;
    }

    public void setBannerScore(Long bannerScore) {
        this.bannerScore = bannerScore;
    }

    public Long getInviteScore() {
        return inviteScore;
    }

    public void setInviteScore(Long inviteScore) {
        this.inviteScore = inviteScore;
    }
}
