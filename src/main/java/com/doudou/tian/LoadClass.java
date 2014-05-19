package com.doudou.tian;

public class LoadClass {
    public static void main(String args[]){
    	System.out.println("B.s=" + Test.s);
    	System.out.println(Thread.currentThread().getContextClassLoader());
    }
}

class Test {
	final static String s = "Tommy";
	
	static{
		System.out.println("Test init");
	}
}
