package com.omolayoseun.fuprett;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DocxToHtml {

    public static String convert(String filePath){
        return unzipFile(filePath);
    }

    private static String unzipFile(String filePath) {
        ZipEntry zEntry;
        StringBuilder xmlText = new StringBuilder();
        try {
            ZipFile zipFile = new ZipFile(new File(filePath));
            zEntry = zipFile.getEntry("word/document.xml");

            InputStream in = zipFile.getInputStream(zEntry);
            Scanner s = new Scanner(in);

            while (s.hasNext()){
                xmlText.append(s.nextLine().replace(">", ">\n"));
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return formatToHtml(xmlText.toString());
    }

    public static String formatToHtml(String input) {

        StringBuilder text = new StringBuilder("<!DOCTYPE html>\n" +
                "<head><title></title></head>\n" +
                "<body>");
        Scanner read = new Scanner(input);
        boolean thereIs = false;

        while (read.hasNext()){
            String temp = read.nextLine();

            //text.append(temp.startsWith("<w:tbl>")? "" : temp.startsWith("<w:tb>")? "": "a");

            if (temp.startsWith("<w:tbl>"))
                text.append("<table>");

            else if (temp.startsWith("<w:tr ") || temp.startsWith("<w:tr>"))
                text.append("<tr>");

            else if (temp.startsWith("<w:tc>") || temp.startsWith("<w:tc ")) {
                thereIs = true;
            }

            else if (temp.startsWith("<w:gridSpan w:val=\"2\"")) {
                text.append("<td colspan=\"2\">");
                thereIs = false;
            }
            else if (temp.startsWith("<w:gridSpan w:val=\"3\"")){
                text.append("<td colspan=\"3\">");
                thereIs = false;
            }
            else if (temp.startsWith("<w:gridSpan w:val=\"4\"")){
                text.append("<td colspan=\"4\">");
                thereIs = false;
            }
            else if (temp.startsWith("<w:gridSpan w:val=\"5\"")){
                text.append("<td colspan=\"5\">");
                thereIs = false;
            }
            else if (temp.startsWith("<w:p") && !temp.startsWith("<w:pPr>")
                        && !temp.startsWith("<w:pict")
                        && !(temp.startsWith("<w:p") && temp.endsWith("/>"))) {
                if (thereIs) {
                    text.append("<td>");
                    thereIs = false;
                }
                text.append("<p>");
            }
            else if (temp.endsWith("</w:t>"))
                text.append(temp.replace("</w:t>", ""));

            else if (temp.contains("</w:p>"))
                text.append("</p>\n");

            else if (temp.startsWith("</w:tc>"))
                text.append("</td>");

            else if (temp.startsWith("</w:tr>"))
                text.append("</tr>\n");

            else if (temp.startsWith("</w:tbl>"))
                text.append("</table>\n");
        }
        text.append("</body>\n</html>");

        return text.toString();
    }

}
