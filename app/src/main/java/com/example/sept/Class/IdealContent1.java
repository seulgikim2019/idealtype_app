package com.example.sept.Class;

public class IdealContent1 {

    public String ideal_num;
    public String ideal_contents_name;
    public String ideal_contents_pic;
    public String result;


    public IdealContent1(String ideal_num,String ideal_contents_name,String ideal_contents_pic){
        this.ideal_num=ideal_num;
        this.ideal_contents_name=ideal_contents_name;
        this.ideal_contents_pic=ideal_contents_pic;
    }

    public String getResult() {
        return result;
    }

    public String getIdeal_contents_name() {
        return ideal_contents_name;
    }

    public String getIdeal_num() {
        return ideal_num;
    }

    public String getIdeal_contents_pic() {
        return ideal_contents_pic;
    }
}

