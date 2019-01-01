package com.example.demo.utils.snowflake;

/**
 * @author lijun
 * @Title: testSnowflake
 * @ProjectName java8
 * @Description: 测试Snowflake
 * @date 19-1-1下午3:19
 */
public class testSnowflake {
    //==============================Test=============================================
    /** 测试 */
    public static void main(String[] args) {
        SnowflakeCreate snowflakeCreate = new SnowflakeCreate(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = snowflakeCreate.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
