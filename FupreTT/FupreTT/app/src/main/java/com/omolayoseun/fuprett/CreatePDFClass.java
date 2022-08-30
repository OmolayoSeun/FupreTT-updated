package com.omolayoseun.fuprett;

import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

public class CreatePDFClass {
    public static boolean convertToPdf(String timeTableText) {

        //Environment.getExternalStorageDirectory();
        File dir = new File(Environment.getExternalStorageDirectory() + "/FupreTT/");

        if (!dir.exists())
            if(dir.mkdirs()) Log.w("Created ", "Created directory successful");

        try{
            Document document = new Document(PageSize.LETTER);
            PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory() + "/FupreTT/My time table.pdf"));
            document.open();
            document.addAuthor("Omolayo Seun");
            document.addCreator("Omolayo Seun");
            document.addSubject("Time Table");
            document.addCreationDate();
            document.addTitle("Time Table");
            //document.setPageCount(2);

            //Log.w("text", timeTableText);
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(timeTableText));
            document.close();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
