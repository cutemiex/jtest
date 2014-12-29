package com.tiantiandou.jmeter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import org.apache.jmeter.protocol.tcp.sampler.TCPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月27日
 */
public class JmeterNettyClientHandler implements TCPClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmeterNettyClientHandler.class);

    private static final int INT_BYTE_SIZE = 4;

    public String getCharset() {
        return "UTF-8";
    }

    public byte getEolByte() {
        return 0;
    }

    public String read(InputStream arg0) throws ReadException {
        byte[] bytes = new byte[INT_BYTE_SIZE];
        try {
            arg0.read(bytes);
        } catch (IOException e) {
            LOGGER.debug("read ioexception: ", e);
            throw new ReadException(e.getMessage(), e, "");
        }
        try {
            return new String(bytes, getCharset());
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("Decode to string exception : ", e);
            throw new ReadException(e.getMessage(), e, "");
        }
    }

    public void setEolByte(int arg0) {

    }

    public void setupTest() {

    }

    public void teardownTest() {

    }

    public void write(OutputStream arg0, InputStream arg1) throws IOException {
        byte[] bytes = new byte[INT_BYTE_SIZE];
        while (0 != arg1.read(bytes)) {
            arg0.write(bytes);
        }
    }

    public void write(OutputStream arg0, String arg1) throws IOException {
        byte[] bytes = arg1.getBytes();
        arg0.write(bytes);
    }

}
