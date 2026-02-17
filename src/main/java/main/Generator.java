package main;

import java.io.File;
import java.io.FileWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Generator {
    public static void create(String filename, String splitCharacter, String dataClassName) {
        try{
            Scanner fileReader =  new Scanner(new File(filename));

            String[] headersArray  = fileReader.nextLine().split(splitCharacter);

            ArrayList<String> headers = trimHeader(headersArray);

            HashMap<String, String> headersMap = getHeaderType(headers);

            createDataClass(headersMap, dataClassName);
            createParserClass(headers, headersMap, dataClassName, splitCharacter);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static ArrayList<String> trimHeader(String[] headersArray){
        ArrayList<String> headers = new ArrayList<>();

        for (String header : headersArray) {
            header = header.trim();
            header = header.toLowerCase();
            header = Normalizer.normalize(header, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

            boolean containsBracket = header.contains("(");

            if (containsBracket) {
                header = header.substring(0, header.indexOf("("));
            }

            boolean containsSpace = header.contains(" ");

            if (containsSpace) {
                String[] headerParts = header.split(" ");

                header = "";
                for (int j = 0; j < headerParts.length; j++) {
                    if (j == 0) {
                        header += String.valueOf(headerParts[j].charAt(0)).toLowerCase() + headerParts[j].substring(1);
                    } else {
                        header += String.valueOf(headerParts[j].charAt(0)).toUpperCase() + headerParts[j].substring(1);
                    }
                }
            }

            headers.add(header);
        }

        return headers;
    }

    public static HashMap<String, String> getHeaderType(ArrayList<String> headers){
        Scanner input  = new Scanner(System.in);
        HashMap<String, String> headersMap = new HashMap<>();

        for(String header : headers){
            System.out.println();
            System.out.println("Típusa a " + header + " vátozónak: ");
            System.out.println("1. String");
            System.out.println("2. Integer");
            System.out.println("3. Boolean");
            System.out.println("4. Double");
            System.out.println("5. Character");
            System.out.println("6. LocalDate");
            System.out.println("7. LocalDateTime");

            System.out.print("Add meg a típus számát: ");

            int number = input.nextInt();

            switch (number){
                case 1 -> {headersMap.put(header, "String");}
                case 2 -> {headersMap.put(header, "Integer");}
                case 3 -> {headersMap.put(header, "Boolean");}
                case 4 -> {headersMap.put(header, "Double");}
                case 5 -> {headersMap.put(header, "Character");}
                case 6 -> {headersMap.put(header, "LocalDate");}
                case 7 -> {headersMap.put(header, "LocalDateTime");}
            }
        }

        input.close();

        return  headersMap;
    }

    public static void createDataClass(HashMap<String,String> headersMap, String dataClassName) {
        try{
            FileWriter writer = new FileWriter(dataClassName + ".java");

            StringBuilder builder = new StringBuilder();

            builder.append("public class " + dataClassName + " {\n");

            for(String header : headersMap.keySet()){
                builder.append("\t" + headersMap.get(header) + " " + header + ";\n");
            }

            /*
                public void setMagyarNev(String magyarNev) {
                    this.magyarNev = magyarNev;
                }

                public String getLatinNev() {
                    return latinNev;
                }
             */

            builder.append("\n");

            //getters
            for(String header : headersMap.keySet()){
                builder.append("\tpublic " + headersMap.get(header) + " get" + String.valueOf(header.charAt(0)).toUpperCase() + header.substring(1) + "() {\n");
                builder.append("\t\treturn " + header + ";\n");
                builder.append("\t}\n");
            }

            builder.append("\n");

            //setters
            for(String header : headersMap.keySet()){
                builder.append("\tpublic void set" + String.valueOf(header.charAt(0)).toUpperCase() + header.substring(1) +
                        "(" + headersMap.get(header) + " " + header + ") {\n");
                builder.append("\t\tthis." + header + " = " + header + ";\n");
                builder.append("\t}\n");
            }

            builder.append("\n");

            //constructor

            //constructor header
            builder.append("\tpublic " + dataClassName + "(");

            for(String header : headersMap.keySet()){
                builder.append(headersMap.get(header) + " " + header + ", ");
            }

            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") {\n");

            for(String header : headersMap.keySet()){
                builder.append("\t\tthis.set" + String.valueOf(header.charAt(0)).toUpperCase() + header.substring(1) +
                        "(" + header + ");\n");
            }

            builder.append("\t}\n");
            builder.append("}");

            writer.write(builder.toString());
            writer.close();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void createParserClass(ArrayList<String> headers, HashMap<String,String> headersMap, String dataClassName, String splitCharacter) {
        try{
            FileWriter writer = new FileWriter("CSVParser.java");

            StringBuilder builder = new StringBuilder();

            builder.append("import java.io.File;");
            builder.append("import java.time.LocalDate;");
            builder.append("import java.time.LocalDateTime;");
            builder.append("import java.util.ArrayList;");
            builder.append("import java.util.Scanner;");

            builder.append("\n");

            builder.append("public class CSVParser {\n");
            builder.append("\tpublic static ArrayList<");
            builder.append(dataClassName);
            builder.append("> parse(String filename) {\n");

            builder.append("\t\tArrayList<" + dataClassName + "> list = new ArrayList<>();\n");

            builder.append("\t\ttry {\n");
            builder.append("\t\t\tScanner scanner = new Scanner(new File(filename));\n\n");
            builder.append("\t\t\tscanner.nextLine(); //skip header\n\n");
            builder.append("\t\t\twhile (scanner.hasNextLine()) {\n");
            builder.append("\t\t\t\tString[] parts = scanner.nextLine().split(" + '"' + splitCharacter + '"' + ");\n\n");
            builder.append("\t\t\t\t" + dataClassName + " " + dataClassName.toLowerCase()  + " = new "+ dataClassName +"(\n");

            //if not string parse it
//            for(String header : headers){
//                builder.append("\t\t\t\t\t");
//                String line = parsedLine(headersMap.get(header), header);
//            }

            builder.append("\t\treturn list;\n");

            writer.write(builder.toString());
            writer.close();

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static String parsedLine(String type, String name, String variable){
        switch(type){
            case "String" -> {
                return "";
            }
        }

        return null;
    }
}

