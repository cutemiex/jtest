package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.tiantiandou.yasf.message.utils.ByteBufUtils;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月31日
 */
public class YasfArgument implements YasfSerializable {

    private static final long serialVersionUID = 8140451565648328142L;

    private String type;

    private byte[] value;

    public void initFrom(ByteBuf buf) {
        type = ByteBufUtils.readString(buf);
        value = ByteBufUtils.readBytes(buf);
    }

    public int fillTo(ByteBuf buf) {
        int length = ByteBufUtils.writeString(type, buf);
        length += ByteBufUtils.writeBytes(value, buf);
        return length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
