package com.inntechy.a11039.cdcall;

import java.io.Serializable;

/**
 * Created by 11039 on 2017/7/17.
 */

public class Calls implements Serializable {

    private String photoid;
    private byte[] photo;
    private String name; // 名称
    private String number; // 号码
    private String date; // 日期
    private int type; // 来电:1，拨出:2,未接:3
    private int count; // 通话次数
    private String time;//通话时间

    //来电
    private int incomingCount;
    public int getIncomingCount() {
        return incomingCount;
    }
    public void setIncomingCount(int incomingCount) {
        this.incomingCount = incomingCount;
    }

    private String incomingTime;
    public String getIncomingTime() {
        return incomingTime;
    }
    public void setIncomingTime(String incomingTime) {
        this.incomingTime = incomingTime;
    }

    //拨出
    private int outcomingCount;
    public int getOutcomingCount() {
        return outcomingCount;
    }
    public void setOutcomingCount(int outcomingCount) {
        this.outcomingCount = outcomingCount;
    }

    private String outcomingTime;
    public String getOutcomingTime() {
        return outcomingTime;
    }
    public void setOutcomingTime(String outcomingTime) {
        this.outcomingTime = outcomingTime;
    }

    //未接
    private int missedCount;
    public int getMissedCount() {
        return missedCount;
    }
    public void setMissedCount(int missedCount) {
        this.missedCount = missedCount;
    }

    //拒接
    private int refuesdCount;
    public int getRefuesdCount() {
        return refuesdCount;
    }
    public void setRefuesdCount(int refuesdCount) {
        this.refuesdCount = refuesdCount;
    }

    public byte[] getPhoto() {return photo;}

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String id) {
        this.photoid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
