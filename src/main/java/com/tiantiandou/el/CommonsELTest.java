package com.tiantiandou.el;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class CommonsELTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("CommonsELTest");

    private static final int BUFFER_SIZE = 5 * 1024;

    private CommonsELTest() {

    }

    public static void main(String[] args) {
        ExpressionFactory factory = new de.odysseus.el.ExpressionFactoryImpl();
        de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();
        context.setVariable("id", factory.createValueExpression("wewewe", String.class));
        // context.setVariable("list", list)
        String str = getFileAsString();
        ValueExpression ve = factory.createValueExpression(context, str, Object.class);
        LOGGER.debug(ve.getValue(context).toString());
    }

    private static String getFileAsString() {
        BufferedInputStream s = new BufferedInputStream(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("test-el.txt"));
        byte[] b = new byte[BUFFER_SIZE];
        try {
            s.read(b);
        } catch (IOException e) {
            LOGGER.warn("IO exception", e);
        }
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("UnsupportedEncodingException exception", e);
        }
        return null;
    }
}
