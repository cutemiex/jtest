package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/***
 * 
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月26日
 */
public class ByteBufUtilTest extends TestCase {

    @Test
    public void testWriteBytes() {
        String value = "I like it";
        byte[] bytes = value.getBytes();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer();
        int length = ByteBufUtil.writeBytes(bytes, buf);
        assertTrue((bytes.length + 4) == length);
        assertTrue(length == buf.readableBytes());
        assertTrue(Arrays.equals(bytes, ArrayUtils.subarray(buf.array(), 4, length)));
    }

    @Test
    public void testReadBytes() {
        String value = "I like it";
        byte[] bytes = value.getBytes();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer();
        ByteBufUtil.writeBytes(bytes, buf);

        byte[] newBytes = ByteBufUtil.readBytes(buf);
        assertTrue(Arrays.equals(newBytes, bytes));
    }

    @Test
    public void testWriteString() {
        fail("Not yet implemented");
    }

    @Test
    public void testReadString() {
        fail("Not yet implemented");
    }

    @Test
    public void testWriteByteArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testReadByteArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testWriteInt() {
        fail("Not yet implemented");
    }

    @Test
    public void testReadInt() {
        fail("Not yet implemented");
    }

}
