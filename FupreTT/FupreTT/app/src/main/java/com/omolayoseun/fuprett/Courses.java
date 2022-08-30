package com.omolayoseun.fuprett;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class Courses {
    private static final String TAG = "Courses";

    @SuppressWarnings("unchecked")
    public void getCourse(String coursePath, Courses.OnCourseReceivedListener onCourseReceivedListener){

        ArrayList<String>[] courses = new ArrayList[2];
        courses[0] = new ArrayList<>(0);
        courses[1] = new ArrayList<>(0);

        // Read from the database
        FirebaseDatabase.getInstance().getReference(coursePath).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // this code were not tested
                try {
                    byte i;

                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        if (Objects.equals(d.getKey(), "title")) i = 0;
                        else i = 1;

                        for (DataSnapshot d__ : d.getChildren()){
                            courses[i].add(d__.getValue(String.class));
                        }
                    }

                    onCourseReceivedListener.courseList(courses);
                } catch (NullPointerException nullPointerException){
                    nullPointerException.printStackTrace();
                    Log.w("Null error", "Course list is null");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                onCourseReceivedListener.courseError();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public interface OnCourseReceivedListener{
        void courseList(ArrayList<String>[] arrayLists);
        void courseError();
    }
}
