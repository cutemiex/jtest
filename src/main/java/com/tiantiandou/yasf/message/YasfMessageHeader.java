package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.google.common.base.Preconditions;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class YasfMessageHeader implements YasfSerializable {

    private static final long serialVersionUID = -3623480891650588570L;

    private static final int HEADER_LENGTH = 36; // 5 * 4 + 8*2

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
        length = ByteBufUtil.readInt(buf);
        version = ByteBufUtil.readInt(buf);
        type = ByteBufUtil.readInt(buf);
        subType = ByteBufUtil.readInt(buf);
        requestType = ByteBufUtil.readInt(buf);
        reserverd = ByteBufUtil.readLong(buf);
        checksum = ByteBufUtil.readLong(buf);
    }

    public int fillTo(ByteBuf buf) {
        Preconditions.checkArgument(buf.writableBytes() >= HEADER_LENGTH,
                "The write buf size is illegal, less than an header size");
        int len = 0;
        len += ByteBufUtil.writeInt(length, buf);
        len += ByteBufUtil.writeInt(version, buf);
        len += ByteBufUtil.writeInt(type, buf);
        len += ByteBufUtil.writeInt(subType, buf);
        len += ByteBufUtil.writeInt(requestType, buf);
        len += ByteBufUtil.writeLong(reserverd, buf);
        len += ByteBufUtil.writeLong(checksum, buf);
        Preconditions.checkArgument(length == HEADER_LENGTH,
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
