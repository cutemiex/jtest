package com.tiantiandou.mybatis;

import com.dustpan.common.domain.CommonDomain;

import java.util.Date;

/**
 * Created by tommy on 2015/9/17.
 */
public class ScoreDetail extends CommonDomain{

    private Long ShopId;

    private Long score;

    private String type;   //COMMON/

    private String  source;  //详情页/店铺装修的/担保/

    private Date time;

    public Long getShopId() {
        return ShopId;
    }

    public Long getScore() {
        return score;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public Date getTime() {
        return time;
    }
}
