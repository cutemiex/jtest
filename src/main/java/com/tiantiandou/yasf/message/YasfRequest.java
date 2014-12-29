package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.tiantiandou.yasf.YasfServiceEntity;

/**
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class YasfRequest extends AbstractYasfMessage {
    private static final long serialVersionUID = 999483953976788000L;
    private YasfServiceEntity yasfService;
    private byte[][] arguments;
    private String sourceIp;
    private String sourceName;

    @Override
    protected int fillBodyTo(ByteBuf buf) {
        int length = 0;
        length += ByteBufUtil.writeObject(yasfService, buf);
        length += ByteBufUtil.writeByteArray(arguments, buf);
        length += ByteBufUtil.writeString(sourceIp, buf);
        length += ByteBufUtil.writeString(sourceName, buf);
        return length;
    }

    @Override
    protected void initBodyFrom(ByteBuf buf) {
        yasfService = ByteBufUtil.readObject(YasfServiceEntity.class, buf);
        arguments = ByteBufUtil.readByteArray(buf);
        sourceIp = ByteBufUtil.readString(buf);
        sourceName = ByteBufUtil.readString(buf);
    }

    public YasfServiceEntity getYasfService() {
        return yasfService;
    }

    public void setYasfService(YasfServiceEntity yasfService) {
        this.yasfService = yasfService;
    }

    public byte[][] getArguments() {
        return arguments;
    }

    public void setArguments(byte[][] arguments) {
        this.arguments = arguments;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
