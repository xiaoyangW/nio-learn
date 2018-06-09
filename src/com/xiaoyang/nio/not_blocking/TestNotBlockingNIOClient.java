package com.xiaoyang.nio.not_blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

/**
 * @author WXY
 *  使用nio实现网络通信（堵塞式）
 *
 *  使用NIO完成网络通信的三大核心
 *  1：通道（channel）-->负责连接
 *      java.nio.channels.Channel
 *          |--SocketChannel
 *          |--ServerSocketChannel
 *          |--DatagramChannel
 *
 *          |--Pipe.SinkChannel
 *          |--Pipe.SourceChannel
 *  2：缓冲区（buffer）:负责数据的存取
 *  3：选择器（selecter）:SelectableChannel的多路复用器，
 *      用与监控SelectableChannel的IO状况
 */
public class TestNotBlockingNIOClient {

    public static void main(String[] args){
        try {
            //1.获取通道
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
            //2.设置为非堵塞模式
            socketChannel.configureBlocking(false);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //3.发送数据给服务端
            //控制台输入数据
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String msg = scanner.next();
                buf.put(msg.getBytes());
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }
            //4.关闭连接
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
