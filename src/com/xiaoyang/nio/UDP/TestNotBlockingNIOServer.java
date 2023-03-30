package com.xiaoyang.nio.UDP;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author WXY
 */
public class TestNotBlockingNIOServer {

    public static void main(String[] args) {
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.bind(new InetSocketAddress(8080));
            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    if (selectionKey.isReadable()) {
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        datagramChannel.receive(buf);
                        System.out.println(new String(buf.array(), 0, buf.position()));
                        buf.clear();
                    }
                }
                iterator.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
