package com.doudou.tian.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GcLogic {
	private static final int _1_Mb = 1024 * 1024;
	private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException{
    	//gcEdenPromotion();
    	//gcBigObject();
    	gcMaxTenure();
    	reader.readLine();
    }
    
    public static void gcEdenPromotion() throws IOException{
    	byte[] alloc1 = new byte[2 * _1_Mb];
    	byte[] alloc2 = new byte[2 * _1_Mb];
    	reader.readLine();
    	
    	byte[] alloc3 = new byte[2 * _1_Mb];
    	byte[] alloc4 = new byte[4 * _1_Mb];
    	reader.readLine();
    	
    	byte[] alloc5 = new byte[2 * _1_Mb];
    	reader.readLine();

    	
    	System.out.println(alloc1[0]);
    	System.out.println(alloc2[0]);
    	System.out.println(alloc3[0]);
    	System.out.println(alloc4[0]);
    	System.out.println(alloc5[0]);
//    	alloc1=null;
//    	alloc2=null;
//    	alloc3=null;
//    	alloc5=null;
//    	byte[] alloc6 = new byte[2 * _1_Mb];
//    	byte[] alloc7 = new byte[2 * _1_Mb];
//    	byte[] alloc8 = new byte[2 * _1_Mb];
//    	System.out.println(alloc6.length);
//     	System.out.println(alloc7.length);
//    	System.out.println(alloc8.length); 
    }
    
    public static void gcBigObject() throws IOException{
    	byte[] alloc1 = new byte[4 * _1_Mb];
    	reader.readLine();

    	byte[] alloc2 = new byte[4 * _1_Mb];
    	System.out.println(alloc1.length);
     	System.out.println(alloc2.length);
    }
    
    public static void gcMaxTenure() throws IOException{
    	byte[] alloc1 = new byte[ _1_Mb /4];
    	reader.readLine();

    	byte[] alloc2 = new byte[4 * _1_Mb];
    	reader.readLine();
    	byte[] alloc3 = new byte[4 * _1_Mb];
    	reader.readLine();
    	byte[] alloc4 = new byte[4 * _1_Mb];
    	reader.readLine();
    	System.out.println(alloc1.length);
     	System.out.println(alloc2.length);
    	System.out.println(alloc3.length);
     	System.out.println(alloc4.length);
    }
}
