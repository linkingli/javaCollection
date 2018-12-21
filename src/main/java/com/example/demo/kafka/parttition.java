package com.example.demo.kafka;

import java.util.Random;

/**
 * Created by lijun on 18-12-21.
 */
public class parttition {
    public static void main(String[] args) {

        Random random=new Random();
        int nextInt = random.nextInt(2147483647);
/*        System.out.println(System.currentTimeMillis());
        for (int i=0;i<10000000;i++)
        {
            toPositive(nextInt);
        }
        System.out.println(System.currentTimeMillis());
        for (int i=0;i<10000000;i++)
        {
            compare( nextInt);
        }

        System.out.println(System.currentTimeMillis());*/
        System.out.println(toPositive(nextInt));
    }

    private static int compare(int nextInt) {
        int i=2147483647;
        if (nextInt>i) {
            return i;
        }
        return  nextInt;
    }

    public static int toPositive(int number) {
        //return number & 2147483647;
        return  2147483647 | number;
    }
}
