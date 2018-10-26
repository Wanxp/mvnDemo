package com.wanxp;

import junit.framework.TestCase;

public class TreadTest extends TestCase {

    public void testMutilThread() {
        for (int i = 0;i < 10;i++) {
            new Thread(() -> {
                System.out.println("Thread-" + 1 + " is running !");
            }).start();
        }
    }
}
