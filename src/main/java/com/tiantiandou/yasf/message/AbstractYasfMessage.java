package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public abstract class AbstractYasfMessage implements YasfSerializable {

    private static final long serialVersionUID = -6673351862438633045L;
    private YasfMessageHeader header;

    public AbstractYasfMessage() {
        header = new YasfMessageHeader();
    }

    public YasfMessageHeader getHeader() {
        return header;
    }

    public void setHeader(YasfMessageHeader header) {
        this.header = header;
    }

    public void initFrom(ByteBuf buf) {
        header.initFrom(buf);
        initBodyFrom(buf);
    }

    public int fillTo(ByteBuf buf) {
        return header.fillTo(buf) + fillBodyTo(buf);
    }

    protected abstract int fillBodyTo(ByteBuf buf);

    protected abstract void initBodyFrom(ByteBuf buf);
}
