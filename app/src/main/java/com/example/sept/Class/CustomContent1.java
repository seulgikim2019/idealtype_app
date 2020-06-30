package com.example.sept.Class;

public class CustomContent1 {

    public String cus_contents_email;
    public String cus_contents_nickname;
    public String cus_contents_pic;
    public String cus_contents_age_gender;
    public String cus_contents_region;
    public String result;

    public CustomContent1(String cus_contents_email, String cus_contents_nickname, String cus_contents_pic, String cus_contents_age_gender, String cus_contents_region){
        this.cus_contents_nickname=cus_contents_nickname;
        this.cus_contents_email=cus_contents_email;
        this.cus_contents_pic=cus_contents_pic;
        this.cus_contents_age_gender=cus_contents_age_gender;
        this.cus_contents_region=cus_contents_region;

    }




    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCus_contents_email() {
        return cus_contents_email;
    }


    public void setCus_contents_email(String cus_contents_email) {
        this.cus_contents_email = cus_contents_email;
    }

    public void setCus_contents_age_gender(String cus_contents_age_gender) {
        this.cus_contents_age_gender = cus_contents_age_gender;
    }

    public void setCus_contents_nickname(String cus_contents_nickname) {
        this.cus_contents_nickname = cus_contents_nickname;
    }

    public void setCus_contents_pic(String cus_contents_pic) {
        this.cus_contents_pic = cus_contents_pic;
    }

    public void setCus_contents_region(String cus_contents_region) {
        this.cus_contents_region = cus_contents_region;
    }

    public String getCus_contents_age_gender() {
        return cus_contents_age_gender;
    }

    public String getCus_contents_nickname() {
        return cus_contents_nickname;
    }

    public String getCus_contents_pic() {
        return cus_contents_pic;
    }

    public String getCus_contents_region() {
        return cus_contents_region;
    }


}

