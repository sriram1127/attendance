package com.example.sriram.attendance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GiveAttendanceActivity extends AppCompatActivity {

    private TextView nameTV;
    private TextView codeTV;
    private TextView dateTV;
    private TextView dayTV;
    private ImageView imgIV;
    private ImageView laterIV;
    private TextView msg;
    private LinearLayout ll;
    private DatabaseReference mDatabase;
    private Course course = null;
    private String userId = null;
    private String date = null;
    private String passcode = null;
    private String professorId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_attandance);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nameTV = (TextView) findViewById(R.id.name);
        codeTV = (TextView) findViewById(R.id.code);
        dateTV = (TextView) findViewById(R.id.date);
        dayTV = (TextView) findViewById(R.id.day);
        imgIV = (ImageView) findViewById(R.id.give);
        msg = (TextView) findViewById(R.id.msg);
        ll = (LinearLayout) findViewById(R.id.detail);
        laterIV = (ImageView) findViewById(R.id.later);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy EEEE");

        Intent intent = getIntent();
        if (intent != null) {
            String userMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            userId = userMail.substring(0, userMail.indexOf("@"));
            course = (Course) intent.getExtras().get("course");
            nameTV.setText(course.getName());
            codeTV.setText(course.getCode());
            String dateTime = sdf.format(new Date());
            String day = dateTime.substring(11);
            date = dateTime.substring(0, 10);
            dateTV.setText(date);
            dayTV.setText(day);
            Log.d("", "");
            // String classDay = day.equals("Thursday") ? "R" : day.charAt(0) + "";

            mDatabase.child("Course").child(course.getCrn()).child("professorId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    professorId = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String status = dataSnapshot.child("Course").child(course.getCrn()).child("status").getValue(String.class);
                    DataSnapshot ds = dataSnapshot.child("Attendance").child(userId).child("studentCourses").child(course.getCrn())
                            .child("attendance").child(date);
                    if (ds != null) {
                        Boolean present = ds.getValue() != null ? (Boolean) ds.getValue() : null;
                        if ("OPEN".contains(status)) {
                            msg.setVisibility(View.GONE);
                            laterIV.setVisibility(View.GONE);
                            ll.setVisibility(View.VISIBLE);
                            if (present != null) {
                                if (present) {
                                    imgIV.setImageResource(R.drawable.done);
                                    imgIV.setTag("done");
                                } else {
                                    imgIV.setTag("give");
                                }
                            } else {

                            }
                        } else {
                            msg.setVisibility(View.VISIBLE);
                            laterIV.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.GONE);
                        }
                    } else {
                        msg.setVisibility(View.VISIBLE);
                        laterIV.setVisibility(View.VISIBLE);
                        ll.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    public void giveAttendance(View view) {

        if (view.getTag().equals("give")) {


            mDatabase.child("Professor").child(professorId).child("courses").child(course.getCrn()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    passcode = dataSnapshot.getValue(String.class);
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(userId + "@" + passcode, BarcodeFormat.QR_CODE, 512, 512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        imgIV.setImageBitmap(bmp);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
            /*DatabaseReference dr = mDatabase.child("Attendance").child(userId).child("studentCourses").child(course.getCrn())
                    .child("attendance").child(date);
            dr.setValue(true);
            imgIV.setImageResource(R.drawable.done);
            imgIV.setTag("done");*/

                    mDatabase.child("Attendance").child(userId).child("studentCourses").child(course.getCrn())
                            .child("attendance").child(date).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(Boolean.class))
                                imgIV.setImageResource(R.drawable.done);
                            imgIV.setTag("done");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }
}
