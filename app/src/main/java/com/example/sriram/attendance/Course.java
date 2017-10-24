package com.example.sriram.attendance;

import java.io.Serializable;

/**
 * Created by SRIRAM on 08-11-2016.
 */
public class Course implements Serializable {

    private String name;

    public Course() {
    }

    private String code;
    private String professorName;
    private String day;
    private String time;
    private String crn;

    private Long studentCount;

    public Long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Long studentCount) {
        this.studentCount = studentCount;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getSessionAndYear() {
        return sessionAndYear;
    }

    public void setSessionAndYear(String sessionAndYear) {
        this.sessionAndYear = sessionAndYear;
    }

    private String sessionAndYear;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
