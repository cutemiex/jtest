package com.dustpan.image;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by tommy on 2015/11/16.
 */
public class HtmlToImage {
    public static BufferedImage convertToBufferImage(String data) throws Exception {
        //data = StringEscapeUtils.unescapeHtml4(data);  // 调用者自己处理？

        data = tidy(data);

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(data));

        Document doc = db.parse(is);

        int width = 1024;
        int height = 1024;
        Java2DRenderer renderer = new Java2DRenderer(doc, width, height);
        return renderer.getImage();
    }

    private static String tidy(String data) throws IOException {
        //每次需要进行html代码格式化和处理都new出来
        Tidy tidy = new Tidy();

        InputStream is = new ByteArrayInputStream( data.getBytes("UTF-8"));

        //打印配置
        //tidy.getConfiguration().printConfigOptions(new PrintWriter(System.out), true);

        //是否缩进
        tidy.setIndentContent(true);

        //设置输出错误与警告信息
        StringWriter stringWriter = new StringWriter();
        PrintWriter errorWriter = new PrintWriter(stringWriter);
        tidy.setErrout(errorWriter);

        //是否XHTML,若是: <br> -> <br/> ; <img src=""> -> <img src=""> ....
        tidy.setXHTML(true);

        //是否隐藏注释
        tidy.setHideComments(false);

        //是否br在一行中显示
        tidy.setBreakBeforeBR(false);

        //不知道是啥
        //tidy.setBurstSlides(false);

        //是否删除空的<p></p>
        tidy.setDropEmptyParas(false);

        //是否用p标签包括文字,如测试html的: plz save me
        tidy.setEncloseBlockText(false);

        //url中的 \ -> /
        tidy.setFixBackslash(true);

        //属性也换行,真疯狂
        tidy.setIndentAttributes(false);

        //不知道是啥
        tidy.setJoinStyles(false);

        //有中文,没效果
        tidy.setOutputEncoding("utf-8");

        //是否只有body内容
        tidy.setPrintBodyOnly(true);

        //移除空元素如:<div></div>
        tidy.setTrimEmptyElements(false);

        //是否节点结束后另起一行
        tidy.setSmartIndent(true);

        //是否用em替代i，strong替代b
        tidy.setLogicalEmphasis(false);

        //是否把大小的标记转换成小写
        tidy.setUpperCaseTags(false);

        //一行有多长
        tidy.setWraplen(1000);

        //正确显示中文
        tidy.setInputEncoding("utf-8");

        //格式化打印
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        tidy.parse(is, out);

        String tidied = new String(out.toByteArray());
        System.out.println("\n\n tidied: \n" + tidied);
        return tidied;
    }
}
