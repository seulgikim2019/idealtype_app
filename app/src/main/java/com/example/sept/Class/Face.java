package com.example.sept.Class;

public class Face {
    public Celebrity celebrity;
    public Emotion emotion;
    public Age age;
    public Gender gender;
    public Pose pose;

    public Pose getPose() {
        return pose;
    }

    public Gender getGender() {
        return gender;
    }

    public Age getAge() {
        return age;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public Celebrity getCelebrity() {
        return celebrity;
    }

    public class Celebrity {
        private String value;
        private double confidence;

        public String getValue() {
            return value;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public class Emotion {
        public String value;
        public double confidence;

        public String getValue() {
            return value;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public class Age {
        public String value;
        public double confidence;

        public String getValue() {
            return value;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public class Gender {
        public String value;
        public double confidence;

        public String getValue() {
            return value;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public class Pose {
        public String value;
        public double confidence;

        public String getValue() {
            return value;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}