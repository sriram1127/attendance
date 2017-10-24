package com.example.sriram.attendance;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ProfessorMainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String username = null;
    private DatabaseReference mDatabase;
    private List<Course> courses = null;
    private RecyclerView professorCoursesRV = null;
    private TextView professorNameTV = null;
    private TextView date = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    GivingCourseAdapter givingCourseAdapter = null;
    private RecyclerView.LayoutManager mLayoutManager;
    private String reportDate = null;
    private int position;
    String passcode = null;

    public void moveToTakeAttendanceActivity(int position) {
        this.position = position;
        askPasscode();
    }

    public void askPasscode()
    {

        final EditText txtUrl = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("PASSCODE IS REQUIRED")
                .setMessage("Passcode")
                .setView(txtUrl)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        passcode = txtUrl.getText().toString();
                        Intent intent = new Intent(ProfessorMainActivity.this, TakeAttendanceActivity.class);
                        intent.putExtra("course", courses.get(position));
                        intent.putExtra("passcode", passcode);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    public void pickDate() {
        Log.d("calender", "click");


        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(ProfessorMainActivity.this,
                this, mYear, mMonth, mDay);
        dialog.show();
    }

    public void moveToReportActivity(int position) {
        pickDate();
        this.position = position;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Course course = courses.get(position);
        Integer d = null;
        reportDate = (new StringBuilder()
                .append(monthOfYear + 1).append("-").append(dayOfMonth).append("-")
                .append(year)).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
            d = sdf.parse(reportDate).getDay();
            Log.d("", "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (course.getDay().contains(StudentUtils.days.get(d))) {
            if (reportDate != null) {
                Intent intent = new Intent(ProfessorMainActivity.this, ReportActitvity.class);
                intent.putExtra("course", courses.get(position));
                intent.putExtra("reportDate", reportDate);
                startActivity(intent);
            }
            Log.d("", "");
        } else {
            Toast.makeText(ProfessorMainActivity.this, "No Attendance Available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);
        setTitle("CLASSES");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        professorCoursesRV = (RecyclerView) findViewById(R.id.professorcourses);
        professorCoursesRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        professorCoursesRV.setLayoutManager(mLayoutManager);

        professorNameTV = (TextView) findViewById(R.id.name);
        date = (TextView) findViewById(R.id.date);
        date.setText(sdf.format(new Date()));
        courses = new ArrayList<Course>();
        givingCourseAdapter = new GivingCourseAdapter(ProfessorMainActivity.this, courses);
        Intent intent = getIntent();
        username = intent.getExtras().getString("username");

        mDatabase.child("Professor").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Professor professor = dataSnapshot.getValue(Professor.class);
                professorNameTV.setText("Welcome " + professor.getName());
                HashMap<String, String> professorCourses = professor.getCourses();
                Set<String> crns = professorCourses.keySet();

                for (String crn : crns) {
                    mDatabase.child("Course").child(crn).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            courses.add(dataSnapshot.getValue(Course.class));
                            givingCourseAdapter.notifyDataSetChanged();
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
        professorCoursesRV.setAdapter(givingCourseAdapter);
    }
}