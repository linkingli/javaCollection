package com.example.demo.lang;

/**
 * Created by lijun on 18-12-20.
 */
public class ObjectAboutStrings {
    public static void main(String[] args) {

        //BasicUse();

        test1();
        //test2();
        //test3();

    }

    private static void test3() {
        String s1 = new String("hello");
        String s2 = "hello";
        String intern1 = s1.intern();
        System.out.println(s1 == s2);
        String s3 = new String("hello") + new String("hello");
        String s4 = "hellohello";
        String intern3 = s3.intern();
        System.out.println(s3 == s4);
    }

    private static void test2() {
        String s1 = new String("hello");
        String intern1 = s1.intern();
        String s2 = "hello";
        System.out.println(s1 == s2);
        String s3 = new String("hello") + new String("hello");
        String intern3 = s3.intern();
        String s4 = "hellohello";
        System.out.println(s3 == s4);
    }

    private static void test1() {
        String s1 = "hello";
        String s2 = "hello";
        String s3 = "he" + "llo";
        String s4 = "hel" + new String("lo");
        String s5 = new String("hello");
        String s6 = s5.intern();
        String s7 = "h";
        String s8 = "ello";
        String s9 = s7 + s8;
        System.out.println(s1==s2);//true
        System.out.println(s1==s3);//true
        System.out.println(s1==s4);//false
        System.out.println(s1==s9);//false
        System.out.println(s4==s5);//false
        System.out.println(s1==s6);//true
    }

    private static void BasicUse() {
        StringBuffer stringBuffer=new StringBuffer().append("sda").insert(2,"pihjiu");

        String strByBuilder  = new
                StringBuilder().append("aa").append("bb").append("cc").append
                ("dd").toString();

        String strByConcat = "aa" + "bb" + "cc" + "dd";
        System.out.println(stringBuffer+"  "+strByBuilder+"  "+strByConcat);
    }

}
