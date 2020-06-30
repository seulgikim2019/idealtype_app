package com.example.sept.Class;

public class MyWannabeSave {


    public String w_pic;
    public String w_gender;
    public int w_region;
    public int w_job;
    public int w_age_min;
    public int w_age_max;
    public String w_smoke;
    public int w_height_min;
    public int w_height_max;

    public MyWannabeSave(String w_pic, String w_gender, int w_region, int w_job, int w_age_min, int w_age_max, String w_smoke, int w_height_min, int w_height_max) {
        this.w_pic = w_pic;
        this.w_gender = w_gender;
        this.w_region = w_region;
        this.w_job = w_job;
        this.w_age_min = w_age_min;
        this.w_age_max = w_age_max;
        this.w_smoke = w_smoke;
        this.w_height_min = w_height_min;
        this.w_height_max = w_height_max;
    }


    public String getW_pic() {
        return w_pic;
    }

    public void setW_pic(String w_pic) {
        this.w_pic = w_pic;
    }

    public String getW_gender() {
        return w_gender;
    }

    public void setW_gender(String w_gender) {
        this.w_gender = w_gender;
    }

    public int getW_region() {
        return w_region;
    }

    public void setW_region(int w_region) {
        this.w_region = w_region;
    }

    public int getW_job() {
        return w_job;
    }

    public void setW_job(int w_job) {
        this.w_job = w_job;
    }

    public int getW_age_min() {
        return w_age_min;
    }

    public void setW_age_min(int w_age_min) {
        this.w_age_min = w_age_min;
    }

    public int getW_age_max() {
        return w_age_max;
    }

    public void setW_age_max(int w_age_max) {
        this.w_age_max = w_age_max;
    }

    public String getW_smoke() {
        return w_smoke;
    }

    public void setW_smoke(String w_smoke) {
        this.w_smoke = w_smoke;
    }

    public int getW_height_min() {
        return w_height_min;
    }

    public void setW_height_min(int w_height_min) {
        this.w_height_min = w_height_min;
    }

    public int getW_height_max() {
        return w_height_max;
    }

    public void setW_height_max(int w_height_max) {
        this.w_height_max = w_height_max;
    }
}

