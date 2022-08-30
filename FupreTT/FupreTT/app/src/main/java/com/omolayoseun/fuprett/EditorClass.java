package com.omolayoseun.fuprett;

import java.util.ArrayList;
import java.util.Scanner;

public class EditorClass {

    public static String removeExternalTag(String text){
        Scanner read = new Scanner(text);

        StringBuilder output = new StringBuilder();

        boolean inTable = false;
        while (read.hasNext()){
            String str = read.nextLine();

            if(str.contains("<table>"))
                inTable = true;

            if (inTable)
                output.append(str);
                output.append("\n");

            if (str.contains("</table>"))
                inTable = false;
        }
        return output.toString();
    }


    public static String spaceTheHtmlDocument(String document){
        StringBuilder newDocument = new StringBuilder();
        int length = document.length();

        for (int i = 0; i < length; i++){
            String s = newDocument.append(document.charAt(i)).toString();

            newDocument.append(
                    s.endsWith("<table>") ||
                            s.endsWith("</table>") ||
                                    s.endsWith("</tr>") ||
                                            s.endsWith("</td>") ||
                                                    s.endsWith("<tr>") ||
                                                            s.endsWith("</p>") && document.substring(i).startsWith("><table>") ? "\n": ""
            );

            /*
            *if (s.endsWith("<table>"))
                newDocument.append("\n");
            else if (s.endsWith("</table>"))
                newDocument.append("\n");
            else if (s.endsWith("</tr>"))
                newDocument.append("\n");
            else if (s.endsWith("</td>"))
                newDocument.append("\n");
            else if (s.endsWith("<tr>"))
                newDocument.append("\n");
            else if (s.endsWith("</p>") && document.substring(i).startsWith("><table>"))
                newDocument.append("\n");*/
        }
        return newDocument.toString().replace("]", "]</p><p>").replace(")", ")</p><p>");
    }

    public static String unwantedText(ArrayList<String>[] courses, String document){

        // code should be able to exclude the days and the time head from the list
        boolean inFirstColumn = false;
        boolean inFirstRow = false;

        Scanner read = new Scanner(document);
        StringBuilder output = new StringBuilder();

        while (read.hasNext()){

            String str = read.nextLine();

             if (str.contains("<table>")) {
                 inFirstRow = true;
             }
            if (str.contains("<tr>"))
                inFirstColumn = true;

            if (inFirstRow){
                output.append(str);
            }
            else if (inFirstColumn){
                output.append(str);
            }
            else {
                // Here you can manipulate the output properties of the html document
                str = reFormatString(str);
                output.append(removeUnwantedSubject(courses, str));
            }

            if (str.contains("</tr>")) {
                inFirstRow = false;
            }
            if (str.contains("</td>")) {
                inFirstColumn  = false;
            }
        }
        return output.toString();
    }

    public static String neatUpMyHtml(String str){

        str = str.replace("<table>", "<table>\n");
        str  = str.replace("<tr>", "<tr>\n");
        str = str.replace("</table>", "\n</table>\n");
        str = str.replace("</td>", "</td>\n");
        str = str.replace("<p>", "\n<p>");
        str = str.replace("</p>", "</p>\n");
        str = str.replace("\n\n", "\n");

        boolean inFirstColumn = false;
        boolean inFirstRow = false;
        int sum = 0;

        Scanner read = new Scanner(str);
        StringBuilder output = new StringBuilder();

        StringBuilder preOutput = new StringBuilder();
        while (read.hasNext()){

            String str1 = read.nextLine();

            if (str1.contains("<table>")) {
                inFirstRow = true;
            }
            if (str1.contains("<tr>"))
                inFirstColumn = true;

            if(!inFirstRow && !inFirstColumn){
                if (str1.contains("<p>") || str1.contains("</p>")){
                    if (!str1.contains("<p></p>"))
                        sum ++;
                }
            }

            preOutput.append(str1);

            if (str1.contains("</table>")){
                if (sum != 0){
                    output.append(preOutput);
                    output.append("\n");
                }

                sum = 0;
                preOutput = new StringBuilder();
            }
            if (str1.contains("</tr>")) inFirstRow = false;
            if (str1.contains("</td>")) inFirstColumn  = false;
        }
        output = new StringBuilder(output.toString().replace("</table>", "</table><p><br/></p>"));
        return output.toString();
    }

    private static String reFormatString(String str){

        StringBuilder out = new StringBuilder();
        int length = str.length();

        for(int i = 0; i < length; i++){
            out.append(str.charAt(i));

            if (out.toString().endsWith(">"))
                out.append("\n");
            else if (str.substring(i).startsWith("</")) {
                out = new StringBuilder(out.substring(0, out.length() - 1));
                out.append("\n<");
            }
        }

        return out.toString().replace("\n\n", "\n");
    }

    private static String removeUnwantedSubject(ArrayList<String>[] courses, String str){

        StringBuilder out = new StringBuilder();
        Scanner read = new Scanner(str);

        boolean inPTag = false;
        int length = courses[0].size();

        while (read.hasNext()){
            String str1 = read.nextLine();

            if (str1.equals("</p>")) inPTag = false;

            if (inPTag){
                for (int i = 0; i < length; i++){

                    if (str1.contains(courses[0].get(i)) && str1.contains(courses[1].get(i))){
                        out.append(str1);
                        break;
                    }
                }
            }
            else out.append(str1);

            if (str1.equals("<p>")) inPTag = true;
        }
        return out.toString();
    }
}
