package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月20日
 */
public class YasfResponse extends AbstractYasfMessage {

    private static final long serialVersionUID = 4914948387475852615L;

    private byte[] data;

    private int resultCode;

    private String resultMessage;

    @Override
    protected int fillBodyTo(ByteBuf buf) {
        int length = 0;
        length += ByteBufUtil.writeBytes(data, buf);
        length += ByteBufUtil.writeInt(resultCode, buf);
        length += ByteBufUtil.writeString(resultMessage, buf);
        return length;
    }

    @Override
    protected void initBodyFrom(ByteBuf buf) {
        data = ByteBufUtil.readBytes(buf);
        resultCode = ByteBufUtil.readInt(buf);
        resultMessage = ByteBufUtil.readString(buf);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
