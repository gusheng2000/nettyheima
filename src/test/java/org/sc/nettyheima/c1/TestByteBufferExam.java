package org.sc.nettyheima.c1;

import java.nio.ByteBuffer;

import static org.sc.nettyheima.c1.ByteBufferUtil.debugAll;




/**
 * 处理 粘包,半包练习
 */
public class TestByteBufferExam {


    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello World\nI`m zhangsan\nHo".getBytes());
        //处理
        split(source);
        source.put("w are you\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        //切换读模式
        source.flip();

        //1.遍历所有位置是不是换行符
        for (int i = 0; i < source.limit(); i++) {
            //1.1是换行符 开始写
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                //获取到第一个包的长度
                ByteBuffer target = ByteBuffer.allocate(length);
                //1.2 写入新的buffer
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }

        //切换写模式 ,把半包的数据向前移动,压缩
        source.compact();

    }
}
