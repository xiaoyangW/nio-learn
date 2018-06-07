package com.xiaoyang.nio.blocking2;

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
 *  使用nio实现网络通信（堵塞式）
 */
public class TestBlockingNIOServer2 {

    public static void main(String[] args){

        try {
            //1获取通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //2绑定连接
            serverSocketChannel.bind(new InetSocketAddress(8080));
            //3获取客户端连接的通道
            SocketChannel socketChannel = serverSocketChannel.accept();
            FileChannel outChannel = FileChannel.open(Paths.get("test3"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
            //4
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while (socketChannel.read(buf)!=-1){
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
            buf.put("服务端接收数据成功".getBytes());
            buf.flip();
            socketChannel.write(buf);
            socketChannel.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
