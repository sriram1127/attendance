package com.example.sriram.attendance;

import java.util.HashMap;

/**
 * Created by SRIRAM on 15-11-2016.
 */

public class Professor {

    private String name;

    private String email;

    private HashMap<String, String> courses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, String> getCourses() {
        return courses;
    }

    public void setCourses(HashMap<String, String> courses) {
        this.courses = courses;
    }
}
