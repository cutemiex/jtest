package com.dustpan.image;

import com.dustpan.common.util.DateUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tommy on 2015/11/12.
 */
public class TextToImage {
    private static void printFont(){
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for ( int i = 0; i < fonts.length; i++ )
        {
            System.out.println(fonts[i]);
        }
    }
    private BufferedImage getBufferImage(){
        int width = 500;
        int lineInterval = 3;
        int widthMargin = 10;
        int heightMargin = 15;

        int bannerWidth = 120;
        int bannerHeight = 80;

        String name="双11大促";
        Date startDate = new Date();

        List<String> strings = new ArrayList<String>();
        strings.add("优惠详细信息:");
        strings.add("* 单笔订单满 100 元 减 10 元 包邮 (不包括 港澳台及海外)");
//        strings.add("* 单笔订单满 100 元 减 10 元 包邮 (不包括 港澳台及海外) 送礼物:刘伟1");
//        strings.add("* 单笔订单满 200 元 打 9 折 包邮 (不包括 港澳台及海外) 送礼物:送一下2");
//        strings.add("* 单笔订单满 300 元 减 10 元 包邮 (包邮地区：北京,天津,河北,山东,内蒙古,辽宁,吉林,黑龙江,上海,江苏,浙江,江西,河南,湖北,湖南)送礼物:礼物名称");
//

        Font font = new Font("宋体", Font.PLAIN, 16);
        FontRenderContext frc = new FontRenderContext(null, true, true);

        List<String> lines = new ArrayList<String>();
        double maxLineHeight = 0;
        for(String s : strings){
            int start = 0;
            int end = 0;
            System.out.println("s.length=" + s.length());
            boolean isSplitted = false;
            while(start < s.length()){
                end++;
                String subString = s.substring(start, end);
                Rectangle2D r2d = font.getStringBounds(subString, frc);
                if(r2d.getHeight() - maxLineHeight > 0){
                    maxLineHeight = r2d.getHeight();
                }
                if(end >= s.length()){
                    String subStr = s.substring(start, end);
                    if(isSplitted){
                        subStr = "  " + subStr;
                    }
                    lines.add(subStr);
                    break;
                }

                if(r2d.getWidth() > width){
                    String subStr = s.substring(start, end-1);
                    if(isSplitted){
                        subStr = "  " + subStr;
                    }
                    lines.add(subStr);
                    start = end -1;
                    isSplitted = true;
                }
            }
        }
        maxLineHeight = maxLineHeight + 3;
        for(String s: lines){
            System.out.println(s);
        }

        BufferedImage buffRenderImage = new BufferedImage(width + 2 * widthMargin, bannerHeight + ((int)Math.ceil(maxLineHeight) + lineInterval) * lines.size() + 2 * heightMargin, BufferedImage.TYPE_INT_RGB);
        Graphics2D flatGraphic = buffRenderImage.createGraphics();
        flatGraphic.setColor(Color.WHITE);
        flatGraphic.fillRect(0, 0, buffRenderImage.getWidth(), buffRenderImage.getHeight());

        flatGraphic.setColor(Color.orange);
        flatGraphic.fillRect(0, 0, buffRenderImage.getWidth(), heightMargin);
        flatGraphic.fillRect(0, 0, widthMargin, buffRenderImage.getHeight());
        flatGraphic.fillRect(bannerWidth + widthMargin, heightMargin, buffRenderImage.getWidth() - bannerWidth - widthMargin, heightMargin + bannerHeight);
        flatGraphic.fillRect(0, heightMargin + bannerHeight, buffRenderImage.getWidth(), buffRenderImage.getHeight() - bannerHeight - heightMargin);

        try {
            Image image = ImageIO.read(new File("D:/mobile_banner.png"));
            flatGraphic.drawImage(image, widthMargin, heightMargin, bannerWidth, bannerHeight, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        flatGraphic.setFont(font);
        flatGraphic.setColor(Color.GREEN);
        flatGraphic.drawString("活动名称: ", widthMargin + bannerWidth + 10, heightMargin + (int) Math.ceil(maxLineHeight) + 10);
        flatGraphic.setColor(Color.RED);
        flatGraphic.drawString(name, widthMargin + bannerWidth + 90, heightMargin + (int) Math.ceil(maxLineHeight) + 10);
        flatGraphic.setColor(Color.GREEN);
        flatGraphic.drawString("活动日期: ", widthMargin + bannerWidth + 10, heightMargin + 2 * (int) Math.ceil(maxLineHeight) + 25);
        flatGraphic.setColor(Color.RED);
        flatGraphic.drawString(DateUtil.format(startDate, "yyyy/MM/dd HH:mm") + " - " + DateUtil.format(startDate, "yyyy/MM/dd HH:mm"), widthMargin + bannerWidth + 90, heightMargin + 2 * (int) Math.ceil(maxLineHeight) + 25);

        flatGraphic.setColor(Color.BLACK);
        int posY = widthMargin + bannerHeight + 10;
        for(int i =0; i< lines.size(); i++){
            if(i == 0){
                flatGraphic.setColor(Color.BLUE);
            }else{
                flatGraphic.setColor(Color.BLACK);
            }
            String s = lines.get(i);
            posY += Math.ceil(maxLineHeight);
            flatGraphic.drawString(s, widthMargin, posY);
        }

        // don't use drawn graphic anymore.
        flatGraphic.dispose();
        return buffRenderImage;
    }

    private void makeImage(String filePathName) {
            boolean bImage = false ;
            BufferedImage bufImg=this.getBufferImage();
            if(bufImg==null){
                System.out.println("bufImg is null");
            }
            try{

                FileOutputStream fileOut= new FileOutputStream(filePathName);
                BufferedOutputStream bufOut=new BufferedOutputStream(fileOut);
                System.out.println("filePathName is "+filePathName);
                bImage = ImageIO.write(bufImg, "png", fileOut);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

    public static void main(String[] args){
        printFont();
        //new TextToImage().makeImage("d:/image.png");
    }
}
