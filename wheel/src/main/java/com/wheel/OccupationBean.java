package com.wheel;

import java.util.List;

public class OccupationBean {
    private int code;
    private String name;
    private List<Job> occupation;

    public OccupationBean(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Job> getOccupation() {
        return occupation;
    }

    public void setOccupation(List<Job> occupation) {
        this.occupation = occupation;
    }

    public int getCode() {
        return code;
    }


    public static class Job{
        private int code;
        private String name;

        public Job(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

}
