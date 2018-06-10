package com.xiaoyang.nio.UDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author WXY
 *  NIO中DatagramChannel是一个能收发UDP包的通道
 */
public class TestNotBlockingNIOClient {

    public static void main(String[] args){
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String msg = scanner.next();
                buffer.put(msg.getBytes());
                buffer.flip();
                datagramChannel.send(buffer,new InetSocketAddress("127.0.0.1",8080));
                buffer.clear();
            }
            datagramChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
