package com.xiaoyang.nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author WXY
 * 使用nio实现网络通信（堵塞式）
 * <p>
 * 使用NIO完成网络通信的三大核心
 * 1：通道（channel）-->负责连接
 * java.nio.channels.Channel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * |--Pipe.SinkChannel
 * |--Pipe.SourceChannel
 * 2：缓冲区（buffer）:负责数据的存取
 * 3：选择器（selecter）:SelectableChannel的多路复用器，
 * 用与监控SelectableChannel的IO状况
 */
public class TestBlockingNIOClient {

    public static void main(String[] args) {

        try {
            //1.获取通道
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
            //读取文件到通道
            FileChannel inChannel = FileChannel.open(Paths.get("test2"), StandardOpenOption.READ);
            ByteBuffer buf = ByteBuffer.allocate(1024);

            while (inChannel.read(buf) != -1) {
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }
            //关闭通道
            inChannel.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
