package com.xiaoyang.nio;

import java.nio.ByteBuffer;

/**
 * @author WXY
 * 1.缓冲区（buffer）:java nio中负责数据存取，其实缓冲区就是数组，用于存取不同数据类型的数据
 *
 * java 为不同的数据类型提供了相对应的缓冲区 如：
 *  ByteBuffer
 *  CharBuffer
 *  ShortBuffer
 *  IntBuffer
 *  LongBuffer
 *  FloatBuffer
 *  DoubleBuffer
 *  缓冲区的管理方式基本一致，通过allocate()方法获取缓冲区（非直接缓冲区），allocateDirect方法获取直接缓冲区
 *
 *
 * 2.缓冲区存取数据的两核心方法：
 *  put():存入数据到缓冲区
 *  get():获取缓冲区中的数据
 *
 * 3.缓冲区的四个核心属性：
 *  capacity:容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变。
 *  limit:界限，表示缓冲区中可以操作数据的大小。（limit后数据不能进行读写）
 *  position:位置，表示缓冲区中正在操作数据的位置
 *  mark: 标记，表示记录当前position的位置，可通过reset()恢复到mark的位置
 *
 *   mark <= position <= limit <= capacity
 *
 *
 *   4.直接缓冲区与非直接缓冲区
 *      非直接缓冲区：通过allocate()分配缓冲区，缓冲区建立在jvm中
 *      直接缓冲区：通过allocateDirect()方法创建缓冲区，缓冲区建立在系统物理内存中，可以提高效率
 */
public class TestBuffer {
    public static void main(String[] args){

        String temp = "abcdefg";
        //1.创建固定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println("-----------allocate-----------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //2.使用put()存入数据到缓冲区
        System.out.println("------------put()------------");
        byteBuffer.put(temp.getBytes());
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //3.切换读取数据模式
        System.out.println("----------flip()-----------");
        byteBuffer.flip();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        byteBuffer.mark();

        //4.获取缓冲区数据
        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst);
        System.out.println("----------get()-----------");
        System.out.println(new String(dst,0,dst.length));
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        byteBuffer.reset();
        System.out.println("----------reset()-----------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //5.rewind(),可重复读
        byteBuffer.rewind();
        System.out.println("----------rewind()-----------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println("-----------------------------");
        //判断缓冲区是否还有剩余数据
        if (byteBuffer.hasRemaining()){
            //获取缓冲区中可以操作的数量
            System.out.println(byteBuffer.remaining());
        }

        //6.clear() 清空缓冲区，但是缓冲区里的数据依然存在，自是处于“被遗忘”状态
        byteBuffer.clear();
        System.out.println("----------clear()-----------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println((char) byteBuffer.get());

        /*--------------------------------*/
        //直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        System.out.println(buf.isDirect());
    }
}
