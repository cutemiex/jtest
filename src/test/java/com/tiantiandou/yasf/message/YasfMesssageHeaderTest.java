package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import junit.framework.TestCase;

public class YasfMesssageHeaderTest extends TestCase {

    public void testInitFrom() {
        YasfMessageHeader header = new YasfMessageHeader();
        header.setLength(36);
        header.setChecksum(0);
        header.setRequestType(1);
        header.setReserverd(0);
        header.setSubType(2);
        header.setType(1);
        header.setVersion(2);
        ByteBuf buf = Unpooled.buffer(36);
        header.fillTo(buf);

        YasfMessageHeader newHeader = new YasfMessageHeader();
        newHeader.initFrom(buf);
        assertTrue(newHeader.getLength() == 36);
        assertTrue(newHeader.getVersion() == 2);
    }

    public void testFillTo() {
        YasfMessageHeader header = new YasfMessageHeader();
        header.setLength(36);
        header.setChecksum(0);
        header.setRequestType(1);
        header.setReserverd(0);
        header.setSubType(2);
        header.setType(1);
        header.setVersion(1);
        ByteBuf buf = Unpooled.buffer(36);
        header.fillTo(buf);

        YasfMessageHeader newHeader = new YasfMessageHeader();
        newHeader.initFrom(buf);
        assertTrue(newHeader.getLength() == 36);
        assertTrue(newHeader.getVersion() == 1);
    }

}
