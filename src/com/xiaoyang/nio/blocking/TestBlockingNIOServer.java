package com.xiaoyang.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author WXY
 * 使用nio实现网络通信（堵塞式）
 */
public class TestBlockingNIOServer {

    public static void main(String[] args) {

        try {
            //1获取通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            FileChannel inChannel = FileChannel.open(Paths.get("test3"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            //2绑定连接
            serverSocketChannel.bind(new InetSocketAddress(8080));
            //3获取客户端连接的通道
            SocketChannel socketChannel = serverSocketChannel.accept();
            //4
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while (socketChannel.read(buf) != -1) {
                buf.flip();
                inChannel.write(buf);
                buf.clear();
            }
            socketChannel.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
