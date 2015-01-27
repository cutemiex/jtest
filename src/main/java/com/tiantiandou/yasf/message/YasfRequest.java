package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.tiantiandou.yasf.message.utils.ByteBufUtils;
import com.tiantiandou.yasf.service.YasfServiceEntity;

/**
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class YasfRequest extends AbstractYasfMessage {
    private static final long serialVersionUID = 999483953976788000L;
    private YasfServiceEntity serviceEntity;
    private String methodName;
    private YasfArgument[] arguments;
    private String sourceIp;
    private String sourceName;

    @Override
    public int fillBodyTo(ByteBuf buf) {
        int length = 0;
        length += ByteBufUtils.writeObject(serviceEntity, buf);
        length += ByteBufUtils.writeString(methodName, buf);
        length += ByteBufUtils.writeObjectArray(arguments, buf);
        length += ByteBufUtils.writeString(sourceIp, buf);
        length += ByteBufUtils.writeString(sourceName, buf);
        return length;
    }

    @Override
    public void initBodyFrom(ByteBuf buf) {
        serviceEntity = ByteBufUtils.readObject(YasfServiceEntity.class, buf);
        methodName = ByteBufUtils.readString(buf);
        arguments = ByteBufUtils.readObjectArray(YasfArgument.class, buf);
        sourceIp = ByteBufUtils.readString(buf);
        sourceName = ByteBufUtils.readString(buf);
    }

    public YasfServiceEntity getYasfServiceEntity() {
        return serviceEntity;
    }

    public void setYasfService(YasfServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public YasfArgument[] getArguments() {
        return arguments;
    }

    public void setArguments(YasfArgument[] arguments) {
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
