package com.tiantiandou.yasf.message;

import io.netty.buffer.ByteBuf;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

/***
 * 类说明 读写ByteBuf的一些工具类
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class ByteBufUtil {
    private static final int INT_BYTE_SIZE = 4;
    private static final int LONG_BYTE_SIZE = 8;
    private static final int NULL_OBJECT_SIZE = -1;

    private ByteBufUtil() {

    }

    public static int writeBytes(byte[] bytes, ByteBuf buf) {
        if (bytes == null) {
            buf.writeInt(NULL_OBJECT_SIZE);
            return INT_BYTE_SIZE;
        }
        if (bytes.length == 0) {
            buf.writeInt(0);
            return INT_BYTE_SIZE;
        }
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return bytes.length + INT_BYTE_SIZE;
    }

    public static byte[] readBytes(ByteBuf buf) {
        int length = buf.readInt();
        if (length == 0) {
            return new byte[0];
        } else if (length == NULL_OBJECT_SIZE) {
            return null;
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes, 0, length);
        return bytes;
    }

    public static int writeString(String data, ByteBuf buf) {
        if (data == null) {
            return writeBytes(null, buf);
        }
        byte[] bytes = data.getBytes();
        return writeBytes(bytes, buf);
    }

    public static String readString(ByteBuf buf) {
        byte[] bytes = readBytes(buf);
        if (bytes.length == 0) {
            return "";
        }
        if (bytes.length == NULL_OBJECT_SIZE) {
            return null;
        }
        return new String(bytes, Charsets.UTF_8);
    }

    public static int writeByteArray(byte[][] objs, ByteBuf buf) {
        if (objs == null) {
            buf.writeInt(NULL_OBJECT_SIZE);
            return INT_BYTE_SIZE;
        }
        int total = INT_BYTE_SIZE;
        buf.writeInt(objs.length);
        if (objs.length > 0) {
            for (byte[] t : objs) {
                total += writeBytes(t, buf);
            }
        }
        return total;
    }

    public static byte[][] readByteArray(ByteBuf buf) {
        int num = buf.readInt();
        if (num == NULL_OBJECT_SIZE) {
            return null;
        }
        byte[][] bytesArray = new byte[num][];
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                bytesArray[i] = readBytes(buf);
            }
        }
        return bytesArray;
    }

    public static int writeInt(int value, ByteBuf buf) {
        buf.writeInt(value);
        return INT_BYTE_SIZE;
    }

    public static int readInt(ByteBuf buf) {
        return buf.readInt();
    }

    public static long writeLong(long value, ByteBuf buf) {
        buf.writeLong(value);
        return LONG_BYTE_SIZE;
    }

    public static long readLong(ByteBuf buf) {
        return buf.readLong();
    }

    public static <T extends YasfSerializable> T readObject(Class<T> clazz, ByteBuf buf) {
        int count = buf.readInt();
        if (count == 0) {
            return null;
        }
        try {
            T obj = clazz.newInstance();
            obj.initFrom(buf);
            return obj;
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        return null;
    }

    public static int writeObject(YasfSerializable obj, ByteBuf buf) {
        if (obj == null) {
            buf.writeInt(0);
            return INT_BYTE_SIZE;
        } else {
            buf.writeInt(1);
            return obj.fillTo(buf) + INT_BYTE_SIZE;
        }
    }
}
