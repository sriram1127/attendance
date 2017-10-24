package com.example.sriram.attendance;

import java.util.HashMap;

/**
 * Created by SRIRAM on 08-11-2016.
 */
public class Student {

    private String name;
    private String id;
    private String email;
    private String phoneNumber;

    private HashMap<String,StudentCourse> studentCourses;

    public String getName() {
        return name;
    }

    public HashMap<String, StudentCourse> getStudentCourses() {
        return studentCourses;
    }

    public void setStudentCourses(HashMap<String, StudentCourse> studentCourses) {
        this.studentCourses = studentCourses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
