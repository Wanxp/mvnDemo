package com.wanxp.App.connecter.file;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author wanxp
 * @date Oct 27 2018 at 16:55
 */
public class ReplacingFilterInputStream extends FilterInputStream {
    LinkedList<Integer> inQueue = new LinkedList<Integer>();
    LinkedList<Integer> outQueue = new LinkedList<Integer>();
    final byte[][] repleaseFilterBytes;

    public ReplacingFilterInputStream(InputStream in,
                                      byte[][] repleaseFilterBytes) {
        super(in);
        this.repleaseFilterBytes = repleaseFilterBytes;
    }
//
//    private int isMatchFound() {
//        Iterator<Integer> inIter = inQueue.iterator();
//        for (int i = 0;i < repleaseFilterBytes.length;i++) {
//            for (int j = 0; j < repleaseFilterBytes[i].length; j++)
//                if (!inIter.hasNext() || repleaseFilterBytes[i][j] != inIter.next())
//                    break;
//
//        }
//        return -1;
//    }
//
//    private void readAhead() throws IOException {
//        // Work up some look-ahead.
//        while (inQueue.size() < search.length) {
//            int next = super.read();
//            inQueue.offer(next);
//            if (next == -1)
//                break;
//        }
//    }
//
//    @Override
//    public int read() throws IOException {
//        // Next byte already determined.
//        if (outQueue.isEmpty()) {
//            readAhead();
//            if (isMatchFound()) {
//                for (int i = 0; i < search.length; i++)
//                    inQueue.remove();
//
//                for (byte b : replacement)
//                    outQueue.offer((int) b);
//            } else
//                outQueue.add(inQueue.remove());
//        }
//        return outQueue.remove();
//    }

}
