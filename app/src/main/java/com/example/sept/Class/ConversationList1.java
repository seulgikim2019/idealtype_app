package com.example.sept.Class;

public class ConversationList1 {

    public String id;
    public String nickname;
    public String c_roomNum;
    public String c_img_uri;
    public String result;
    public String content;
    public String date;

    public ConversationList1(String id, String nickname, String c_roomNum, String c_img_uri, String content) {
        this.nickname=nickname;
        this.id = id;
        this.c_roomNum = c_roomNum;
        this.c_img_uri = c_img_uri;
        this.content=content;
    }



    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getResult() {
        return result;
    }

    public String getC_img_uri() {
        return c_img_uri;
    }

    public String getC_roomNum() {
        return c_roomNum;
    }


    public void setResult(String result) {
        this.result = result;
    }

    public void setC_img_uri(String c_img_uri) {
        this.c_img_uri = c_img_uri;
    }

    public void setC_roomNum(String c_roomNum) {
        this.c_roomNum = c_roomNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

