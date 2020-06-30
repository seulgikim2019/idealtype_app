package com.example.sept.Class;

public class Info {
    public Integer faceCount;
    public Size size;

    public Integer getFacecount() {
        return faceCount;
    }

    public Size getSize() {
        return size;
    }

    public class Size {
        Integer width;
        Integer height;

        public Integer getHeight() {
            return height;
        }

        public Integer getWidth() {
            return width;
        }
    }

}

