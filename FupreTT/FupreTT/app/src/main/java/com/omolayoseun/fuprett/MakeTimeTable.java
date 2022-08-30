package com.omolayoseun.fuprett;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.util.TimingLogger;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class MakeTimeTable {


    public static void startTimetableOperation(MyService.HandleMessage handleMessage, MyService.HandleError handleError, Intent receivedIntent){
        String courseFilePath = null, timeTableFilePath;
        byte dept_num = 0, level_num = 0, semester_num = 0;
        boolean useDep = false;



        if (receivedIntent.getExtras().getBoolean(IntentKey.OK.name())) {
            courseFilePath = receivedIntent.getExtras().getString(IntentKey.COURSE_PATH.name());
        }
        else {
            dept_num = receivedIntent.getExtras().getByte(IntentKey.DEPT.name());
            level_num = receivedIntent.getExtras().getByte(IntentKey.LEVEL.name());
            semester_num = receivedIntent.getExtras().getByte(IntentKey.SEMESTER.name());
            useDep = true;
            handleMessage.getMessage(10);
        }
        timeTableFilePath = receivedIntent.getExtras().getString(IntentKey.TIME_TABLE_PATH.getValue());

        @SuppressWarnings("unchecked")
        final ArrayList<String>[][] courses = new ArrayList[][]{new ArrayList[0]};
        String text;
        String timeTable;

        TimingLogger timingLogger = new TimingLogger("MakeTimeTable", "Process 1");
        if(!useDep){
            try {
                // sub operation: get the directory
                assert courseFilePath != null;
                courseFilePath = getDirectory(courseFilePath);
                handleMessage.getMessage(4);
            } catch (Exception e) {
                e.printStackTrace();
                handleError.getError("Invalid Course-form Directory");
                return;
            }

            try {
                // Step 1: Extracting the text from the course form
                text = extractTextFromPdf(courseFilePath);
                //Log.w("Working", "Process done 1...");
                handleMessage.getMessage(17);
            } catch (Exception e) {
                e.printStackTrace();
                handleError.getError("Invalid Course-form Directory");
                return;
            }

            timingLogger.addSplit("Process 2");
            try {
                // Step 2: Getting the courses
                courses[0] = getCourses(text);
                //Log.w("Working", "Process done 2...");
                handleMessage.getMessage(36);
            } catch (Exception e) {
                handleError.getError("Failed operation on course form, ensure it is the correct file");
                return;
            }
        }
        else {
            final boolean[] pass = {false};

            new Courses().getCourse(
                    Department.getStr("dep", dept_num) + "/" +
                    Department.getStr("lev", level_num) + "/" +
                    Department.getStr("sem", semester_num)
                    , new Courses.OnCourseReceivedListener() {
                        @Override
                        public void courseList(ArrayList<String>[] arrayLists) {
                            courses[0] = arrayLists;

                            if(courses[0][0].size() == 0) {
                                handleError.getError("Level not available for the selected department");
                                return;
                            }

                            /*
                            *for(ArrayList a : courses[0])
                                    Log.w("Received course", a.toString());
*/
                            Log.w("Thread error checking", "pass set to true");
                            pass[0] = true;
                        }

                        @Override
                        public void courseError() {
                            Log.w("courseError", "Error getting the courses");
                        }
                    }
            );
            handleMessage.getMessage(30);
            byte repeatTimes = 0;

            while (!pass[0]){
                try {
                    //noinspection BusyWait
                    sleep(100);
                    Log.w("Thread error checking", "after sleep of thread");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (repeatTimes == 20) {
                    Log.w("courseError", "Unable to get courses");
                    return;
                }
                else repeatTimes++;
            }
            handleMessage.getMessage(37);
        }

        timingLogger.addSplit("Process 3");
        try {
            // Step 3: Converting the Time Table to html
            timeTableFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + getDirectory(timeTableFilePath);
            timeTable = DocxToHtml.convert(timeTableFilePath);
            //Log.w("Working", "Process done 3...");
            handleMessage.getMessage(63);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Failed to convert time table");
            return;
        }

        timingLogger.addSplit("Process 4");
        try {
            // Step 4: Spacing the html of the Time Table
            timeTable = EditorClass.spaceTheHtmlDocument(timeTable.replace("\n", ""));
            //Log.w("Working", "Process done 4...");
            handleMessage.getMessage(70);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Editing time table failed");
            return;
        }

        timingLogger.addSplit("Process 5");
        try {
            // Step 5: Removing the unwanted outside text
            timeTable = EditorClass.removeExternalTag(timeTable);
            // Log.w("Working", "Process done 5...");
            handleMessage.getMessage(78);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("cropping error");
            return;
        }

        timingLogger.addSplit("Process 6");
        try {
            // Step 6: Removing the unwanted inner text
            timeTable = EditorClass.unwantedText(courses[0], timeTable);
            //Log.w("Working", "Process done 6...");
            handleMessage.getMessage(80);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Can,t extract courses");
            return;
        }

        timingLogger.addSplit("Process 7");
        try {
            // Step 7: Removing the empty tables
            timeTable = EditorClass.neatUpMyHtml(timeTable);
            //Log.w("Working", "Process done 7...");
            handleMessage.getMessage(88);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Clearing error");
            return;
        }

        timingLogger.addSplit("Process 8");
        try {
            // Step 8: Adding borders to the html file
            timeTable = timeTable.replace("<table>", "<table border=\"1\">");
            //Log.w("Working", "Process done 8...");
            handleMessage.getMessage(93);
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Error in operation");
            return;
        }

        timingLogger.addSplit("Process 9");
        try {
            // Step 9: Covert the html back to pdf document
            if (CreatePDFClass.convertToPdf(timeTable)) {
                handleMessage.getMessage(100);
                //Log.w("Working", "Process done all");
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError.getError("Unable to save file");
        }
        timingLogger.dumpToLog();
    }

    private static String getDirectory(String string){
        int startIndex = string.indexOf(":") + 1;
        return  "/" + string.substring(startIndex);
    }

    private static String extractTextFromPdf(String courseFilePath){
        PdfReader reader = null;
        StringBuilder text = new StringBuilder();

        try {
            reader = new PdfReader(Environment.getExternalStorageDirectory().getAbsolutePath() + courseFilePath);
            int numOfPages = reader.getNumberOfPages();

            for(int i = 1; i <= numOfPages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert reader != null;
        reader.close();

        return text.toString();
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<String>[] getCourses(String text){
        ArrayList<String>[] courses = new ArrayList[2];
        courses[0] = new ArrayList<>(0);
        courses[1] = new ArrayList<>(0);

        Scanner read = new Scanner(text);

        boolean startGetting = false;

        while (read.hasNext()){
            String str = read.nextLine();

            if (startGetting){
                str = str.replace(" ", "").replace("\u00A0", "").trim();

                if (str.length() > 6 && (str = str.substring(0, 6)).matches("^\\w\\w\\w\\d\\d\\d")){
                    courses[0].add(str.substring(0, 3));
                    courses[1].add(str.substring(3));
                }
            }
            if (str.contains("code") || str.contains("CODE") || str.contains("Code"))
                startGetting = true;
        }
        return courses;
    }
}
