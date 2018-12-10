package com.example.demo.collection.ObjectToMap;

import javafx.beans.DefaultProperty;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by lijun on 18-12-9.
 */
public class tea {
    public tea() {
    }
    public tea(int weiht, String color, String homeland) {
        this.weiht = weiht;
        this.color = color;
        this.homeland = homeland;
    }

    private  int weiht ;
    private String color;
    private String homeland;

    public int getWeiht() {
        return weiht;
    }

    public void setWeiht(int weiht) {
        this.weiht = weiht;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHomeland() {
        return homeland;
    }

    public void setHomeland(String homeland) {
        this.homeland = homeland;
    }
}
