package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public interface YasfSerializable extends Serializable {
    /****
     * 读出数据
     * 
     * @param data
     * @return
     */
    void initFrom(ByteBuf buf);

    /***
     * 数据写入到buffer
     * 
     * @param buf
     * @return
     */
    int fillTo(ByteBuf buf);
}
