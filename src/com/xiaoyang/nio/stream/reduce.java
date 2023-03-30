package com.xiaoyang.nio.stream;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author WXY
 * @date 2018/9/11 16:16
 */

public class reduce {
    public static void main(String[] args) {
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        Optional<String> longest = stream.reduce((s1, s2) -> s1.length() >= s2.length() ? s1 : s2);
        System.out.println(longest.get());
    }
}
