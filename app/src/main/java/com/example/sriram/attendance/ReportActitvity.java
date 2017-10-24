package com.example.sriram.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ReportActitvity extends AppCompatActivity {

    private Course course;
    private String reportDate;
    private TextView reportdayTV;
    private TextView courseNameTV;
    private ListView studentLV;
    private DatabaseReference mDatabase;
    private List<StudentAttendance> students;
    private StudentAttendance studentAttendance = null;
    private ReportAdapter reportAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_actitvity);
        setTitle("Attendance Report");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        reportdayTV = (TextView) findViewById(R.id.reportDay);
        courseNameTV = (TextView) findViewById(R.id.courseName);
        studentLV = (ListView) findViewById(R.id.student);
        students = new ArrayList<StudentAttendance>();
        reportAdapter = new ReportAdapter(ReportActitvity.this, R.layout.student, students);
        Intent intent = getIntent();
        if (intent != null) {
            course = (Course) intent.getExtras().get("course");
            reportDate = intent.getExtras().getString("reportDate");
            reportdayTV.setText(reportDate);
            courseNameTV.setText(course.getName());
            Log.d("", "");
        }

        mDatabase.child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> d = dataSnapshot.getChildren().iterator();
                while (d.hasNext()) {
                    Student student = d.next().getValue(Student.class);
                    StudentCourse sc = student.getStudentCourses().get(course.getCrn());
                    if (sc != null) {
                        HashMap<String, Boolean> attendances = sc.getAttendance();
                        studentAttendance = new StudentAttendance();
                        studentAttendance.setStudentName(student.getName());
                        studentAttendance.setStudentId(student.getId());
                        studentAttendance.setAttendancePercentage(sc.getAttendancePercentage());
                        studentAttendance.setPresent(attendances.get(reportDate));
                        students.add(studentAttendance);
                        Collections.sort(students, new Comparator<StudentAttendance>() {
                            @Override
                            public int compare(StudentAttendance lhs, StudentAttendance rhs) {
                                return Boolean.compare(lhs.isPresent(),rhs.isPresent());
                            }
                        });
                        reportAdapter.notifyDataSetChanged();
                    }
                    Log.d("", "");
                }
                Log.d("", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        studentLV.setAdapter(reportAdapter);
        reportAdapter.notifyDataSetChanged();
    }
}
