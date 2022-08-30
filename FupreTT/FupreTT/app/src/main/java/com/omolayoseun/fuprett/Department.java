package com.omolayoseun.fuprett;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Department {

    private static String[] dept;

    private final static String[] semester = {
            "first semester",
            "second semester"
    };
    private final static String[] level = {
            /*"100 level",*/
            "200 level",
            "300 level",
            "400 level",
            "500 level"
    };

    public static String getStr(String s, byte index){
        switch (s){
            case "dep":
                return dept[index];
            case "sem":
                return semester[index];
            case "lev":
                return level[index];
            default:
                return null;
        }
    }

    public static String[] getArr(String s){
        switch (s){
            case "dep":
                return dept;
            case "sem":
                return semester;
            case "lev":
                return level;
            default:
                return null;
        }
    }

    public static void initialiseDept(Department.setData setData) {

        FirebaseDatabase.getInstance().getReference("departments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    ArrayList<String> arr = new ArrayList<>(0);

                    for (DataSnapshot s : snapshot.getChildren()){
                        arr.add(s.getValue(String.class));
                    }
                    dept = arr.toArray(new String[0]);
                }
                setData.onReceivedUpdate((dept != null));

                Log.w("Department Debugging", "On data Changed ran");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setData.onReceivedUpdate(false);
                Log.w("Department Debugging", "On cancelled calling ran");
            }
        });
    }

    public interface setData{
        void onReceivedUpdate(boolean check);
    }
}
