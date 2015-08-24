package com.tiantiandou.base;

import java.util.Date;

/****
 * 
 * 类说明:
 * Apache工具类的测试和学习
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月22日
 */
public class ApacheCommonsTest {
    public static void main(String[] args){
        System.out.println(new Date(1423715637L*1000)); //1422945713169  //1423715623
        
        System.out.println(new Date(1423715623L*1000));
    }
}
