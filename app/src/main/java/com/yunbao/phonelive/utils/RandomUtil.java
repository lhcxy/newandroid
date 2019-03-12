package com.yunbao.phonelive.utils;

import java.util.Random;

/**
 * Created by cxf on 2017/8/23.
 * 用来生成一定范围内随机数的方法
 */

public class RandomUtil {

    private static Random sRandom;

    static {
        sRandom = new Random();
    }

    /**
     * 获取 fromIndex  到 toIndex范围内的随机数，包括fromIndex 和 toIndex
     *
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static int getRandom(int fromIndex, int toIndex) {
        if (toIndex < fromIndex) {
            throw new RuntimeException("随机数范围不对");
        }
        return sRandom.nextInt(toIndex - fromIndex + 1) + fromIndex;
    }

}
