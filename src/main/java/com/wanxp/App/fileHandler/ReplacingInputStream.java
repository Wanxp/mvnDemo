package com.wanxp.App.fileHandler;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ReplacingInputStream extends FilterInputStream {
    private final Byte[][] replaceByte;
    private int shortSize = 0;
    private int longSize  = 1;


    public ReplacingInputStream(InputStream in, Byte[][] replaceByte) {
        super(in);
        this.replaceByte = replaceByte;
        for (Byte[] b : replaceByte) {
            if (b.length < shortSize)
                shortSize = b.length;
            if (b.length > longSize)
                longSize = b.length;
        }
    }

    @Override
    public int read() throws IOException {
        int val = super.read();

    }




}
