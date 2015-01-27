package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.google.common.base.Preconditions;
import com.tiantiandou.yasf.message.utils.ByteBufUtils;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class YasfMessageHeader implements YasfSerializable {

    public static final int HEADER_LENGTH = 36; // 5 * 4 + 8*2

    private static final long serialVersionUID = -3623480891650588570L;

    /*** 消息的长度 ***/
    private int length;

    /*** 消息的version ****/
    private int version;

    /*** 消息类型 ***/
    private int type;

    /**** 消息子类型 ***/
    private int subType;

    /*** 请求的处理类型， 同步/异步/无返回等 ***/
    private int requestType;

    /**** 保留字段 ***/
    private long reserverd;

    /*** 校验和 ***/
    private long checksum;

    public void initFrom(ByteBuf buf) {
        Preconditions.checkArgument(buf.readableBytes() >= HEADER_LENGTH,
                "The read buf size is illegal, less than an header size");
        length = ByteBufUtils.readInt(buf);
        version = ByteBufUtils.readInt(buf);
        type = ByteBufUtils.readInt(buf);
        subType = ByteBufUtils.readInt(buf);
        requestType = ByteBufUtils.readInt(buf);
        reserverd = ByteBufUtils.readLong(buf);
        checksum = ByteBufUtils.readLong(buf);
    }

    public int fillTo(ByteBuf buf) {
        Preconditions.checkArgument(buf.writableBytes() >= HEADER_LENGTH,
                "The write buf size is illegal, less than an header size");
        int len = 0;
        len += ByteBufUtils.writeInt(length, buf);
        len += ByteBufUtils.writeInt(version, buf);
        len += ByteBufUtils.writeInt(type, buf);
        len += ByteBufUtils.writeInt(subType, buf);
        len += ByteBufUtils.writeInt(requestType, buf);
        len += ByteBufUtils.writeLong(reserverd, buf);
        len += ByteBufUtils.writeLong(checksum, buf);
        Preconditions.checkArgument(len == HEADER_LENGTH,
                "The write buf size is illegal, length = {}less than an header size {}", len, HEADER_LENGTH);
        return HEADER_LENGTH;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public long getReserverd() {
        return reserverd;
    }

    public void setReserverd(long reserverd) {
        this.reserverd = reserverd;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }
}
