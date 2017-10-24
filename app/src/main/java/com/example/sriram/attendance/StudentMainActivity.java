package com.example.sriram.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StudentMainActivity extends AppCompatActivity {

    private String username;
    private DatabaseReference mDatabase;
    private List<Course> courses = null;
    private ListView studentcoursesLV = null;
    private TextView studentName = null;
    private TextView date = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    TakingCourseAdapter takingCourseAdapter = null;

    public void moveToDetails(Course course
    ) {
        Intent intent = new Intent(StudentMainActivity.this, GiveAttendanceActivity.class);
        intent.putExtra("course", course);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        studentcoursesLV = (ListView) findViewById(R.id.studentcourses);
        studentcoursesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveToDetails(courses.get(position));
            }
        });
        studentName = (TextView) findViewById(R.id.name);
        date = (TextView) findViewById(R.id.date);
        date.setText(sdf.format(new Date()));
        courses = new ArrayList<Course>();
        takingCourseAdapter = new TakingCourseAdapter(StudentMainActivity.this, R.layout.course, courses);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            username = intent.getExtras().getString("username");
        }
        mDatabase.child("Attendance").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                studentName.setText("Welcome " + student.getName());
                HashMap<String, StudentCourse> studentCourseHashMap = student.getStudentCourses();
                Set<String> crns = studentCourseHashMap.keySet();

                for (String crn : crns) {
                    mDatabase.child("Course").child(crn).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            courses.add(dataSnapshot.getValue(Course.class));
                            takingCourseAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                Log.d("", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        studentcoursesLV.setAdapter(takingCourseAdapter);
        takingCourseAdapter.notifyDataSetChanged();

    }
}
