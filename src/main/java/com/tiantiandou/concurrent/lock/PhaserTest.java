package com.tiantiandou.concurrent.lock;

import java.util.concurrent.Phaser;

public class PhaserTest {
    
    public static Phaser ph = new Phaser(1);
    
    public static void main(String args[]){
	ph.arrive();
	ph.arrive();
	System.out.println("finished");
    }

}
