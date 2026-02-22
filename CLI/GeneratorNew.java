package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GeneratorNew {
    public static void main(String[] args) {
        String csvPath = "xxx.csv"; //csv fájl neve (madarak.csv)
        String delimiter = ";"; // ; vagy ,
        String className = "Xxx"; //osztály neve (Madar.java)
        String packageName = "main"; //package ami legfelül van a mainbe (main)

        try {
            List<String> lines = readAll(csvPath);
            if (lines.isEmpty()) {
                System.out.println("CSV empty or unreadable: " + csvPath);
                return;
            }

            String headerLine = lines.get(0);
            String[] headersArray = headerLine.split(delimiter);
            ArrayList<String> headers = normalizeHeaders(headersArray);

            List<String[]> rows = parseRows(lines, delimiter, true);
            Map<String, FieldType> types = inferTypes(headers, rows);

            writeDataClass(packageName, className, headers, types);
            writeParser(packageName, className, headers, types, delimiter);

            System.out.println("Generated " + className + ".java and CSVParser.java");
        } catch (Exception e) {
            System.out.println("Generation failed: " + e.getMessage());
        }
    }

    private static List<String> readAll(String path) throws Exception {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty())
                    lines.add(line);
            }
        }
        return lines;
    }

    private static List<String[]> parseRows(List<String> lines, String delimiter, boolean hasHeader) {
        ArrayList<String[]> rows = new ArrayList<>();
        int start = hasHeader ? 1 : 0;
        for (int i = start; i < lines.size(); i++) {
            rows.add(lines.get(i).split(delimiter));
        }
        return rows;
    }

    private static String deriveClassName(String path) {
        String fileName = Path.of(path).getFileName().toString().replaceAll("\\..*", "");
        String clean = toCamel(fileName);
        return Character.toUpperCase(clean.charAt(0)) + clean.substring(1);
    }

    private static ArrayList<String> normalizeHeaders(String[] headersArray) {
        ArrayList<String> headers = new ArrayList<>();
        for (String header : headersArray) {
            String clean = header.trim();
            clean = Normalizer.normalize(clean, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
            clean = clean.replaceAll("[^A-Za-z0-9 ]", " ");
            clean = clean.toLowerCase();
            clean = toCamel(clean);
            headers.add(clean);
        }
        return headers;
    }

    private static String toCamel(String text) {
        String[] parts = text.trim().split(" +");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty())
                continue;
            if (i == 0)
                b.append(parts[i].toLowerCase());
            else
                b.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return b.length() == 0 ? "field" : b.toString();
    }

    private enum FieldType {
        STRING("String"),
        INTEGER("Integer"),
        DOUBLE("Double"),
        BOOLEAN("Boolean"),
        LOCAL_DATE("LocalDate"),
        LOCAL_DATE_TIME("LocalDateTime");

        private final String typeName;

        FieldType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    private static Map<String, FieldType> inferTypes(ArrayList<String> headers, List<String[]> rows) {
        Map<String, FieldType> map = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            FieldType type = FieldType.STRING; // default safe
            for (String[] row : rows) {
                if (i >= row.length)
                    continue;
                String raw = row[i].trim();
                if (raw.isEmpty())
                    continue;
                FieldType detected = detectType(raw);
                type = pickTighter(type, detected);
            }
            map.put(headers.get(i), type);
        }
        return map;
    }

    private static FieldType pickTighter(FieldType current, FieldType candidate) {
        if (current == FieldType.STRING)
            return candidate; // any improvement
        if (current == FieldType.DOUBLE && candidate == FieldType.INTEGER)
            return current; // keep double
        if (current == FieldType.LOCAL_DATE_TIME && candidate == FieldType.LOCAL_DATE)
            return current;
        return candidate;
    }

    private static FieldType detectType(String raw) {
        String compact = raw.replace(" ", "");
        try {
            Integer.parseInt(compact);
            return FieldType.INTEGER;
        } catch (Exception ignored) {
        }
        try {
            Double.parseDouble(compact);
            return FieldType.DOUBLE;
        } catch (Exception ignored) {
        }
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false"))
            return FieldType.BOOLEAN;
        try {
            LocalDate.parse(compact);
            return FieldType.LOCAL_DATE;
        } catch (Exception ignored) {
        }
        try {
            LocalDateTime.parse(compact);
            return FieldType.LOCAL_DATE_TIME;
        } catch (Exception ignored) {
        }
        // handle common spaced date format: 2024- 03 - 30
        try {
            String cleaned = compact.replace("-", "-").replace("T", "T");
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime.parse(cleaned, fmt);
            return FieldType.LOCAL_DATE_TIME;
        } catch (Exception ignored) {
        }
        return FieldType.STRING;
    }

    private static void writeDataClass(String packageName, String className, ArrayList<String> headers,
                                       Map<String, FieldType> types) throws Exception {
        try (FileWriter writer = new FileWriter(className + ".java")) {
            StringBuilder b = new StringBuilder();
            if (!packageName.isEmpty())
                b.append("package ").append(packageName).append(";\n\n");

            boolean usesDate = types.containsValue(FieldType.LOCAL_DATE);
            boolean usesDateTime = types.containsValue(FieldType.LOCAL_DATE_TIME);
            if (usesDate)
                b.append("import java.time.LocalDate;\n");
            if (usesDateTime)
                b.append("import java.time.LocalDateTime;\n");
            if (usesDate || usesDateTime)
                b.append("import java.time.temporal.ChronoUnit;\n");
            if (usesDate || usesDateTime)
                b.append("\n");

            b.append("public class ").append(className).append(" {\n");
            for (String header : headers) {
                b.append("    private final ").append(types.get(header).getTypeName()).append(" ").append(header)
                        .append(";\n");
            }
            b.append("\n");

            // ctor
            b.append("    public ").append(className).append("(");
            for (int i = 0; i < headers.size(); i++) {
                String h = headers.get(i);
                b.append(types.get(h).getTypeName()).append(" ").append(h);
                if (i < headers.size() - 1)
                    b.append(", ");
            }
            b.append(") {\n");
            for (String h : headers) {
                b.append("        this.").append(h).append(" = ").append(h).append(";\n");
            }
            b.append("    }\n\n");

            // getters
            for (String h : headers) {
                String cap = Character.toUpperCase(h.charAt(0)) + h.substring(1);
                b.append("    public ").append(types.get(h).getTypeName()).append(" get").append(cap)
                        .append("() { return ").append(h).append("; }\n");
            }
            b.append("\n");

            // span helper if possible
            if (headers.contains("startDate") && headers.contains("endDate")) {
                FieldType start = types.get("startDate");
                FieldType end = types.get("endDate");
                if ((start == FieldType.LOCAL_DATE || start == FieldType.LOCAL_DATE_TIME)
                        && (end == FieldType.LOCAL_DATE || end == FieldType.LOCAL_DATE_TIME)) {
                    b.append(
                            "    public long getSpanDays() { return ChronoUnit.DAYS.between(startDate, endDate); }\n\n");
                }
            }

            b.append("}\n");
            writer.write(b.toString());
        }
    }

    private static void writeParser(String packageName, String className, ArrayList<String> headers,
                                    Map<String, FieldType> types, String delimiter) throws Exception {
        try (FileWriter writer = new FileWriter("CSVParser.java")) {
            StringBuilder b = new StringBuilder();
            if (!packageName.isEmpty())
                b.append("package ").append(packageName).append(";\n\n");

            b.append("import java.io.BufferedReader;\n");
            b.append("import java.io.FileReader;\n");
            b.append("import java.util.ArrayList;\n");
            b.append("import java.util.List;\n");
            if (types.containsValue(FieldType.LOCAL_DATE))
                b.append("import java.time.LocalDate;\n");
            if (types.containsValue(FieldType.LOCAL_DATE_TIME))
                b.append("import java.time.LocalDateTime;\n");
            b.append("\n");

            b.append("public class CSVParser {\n");
            b.append("    public static List<").append(className).append("> parse(String filename) {\n");
            b.append("        ArrayList<").append(className).append("> list = new ArrayList<>();\n");
            b.append("        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {\n");
            b.append("            String line;\n");
            b.append("            br.readLine(); // skip header\n");
            b.append("            while ((line = br.readLine()) != null) {\n");
            b.append("                String[] parts = line.split(\"").append(delimiter).append("\");\n");
            b.append("                if (parts.length < ").append(headers.size()).append(") continue;\n");
            b.append("                ").append(className).append(" rec = new ").append(className).append("(");
            for (int i = 0; i < headers.size(); i++) {
                String h = headers.get(i);
                FieldType t = types.get(h);
                String expr = parseExpression(t, "parts[" + i + "]");
                b.append(expr);
                if (i < headers.size() - 1)
                    b.append(", ");
            }
            b.append(");\n");
            b.append("                list.add(rec);\n");
            b.append("            }\n");
            b.append("        } catch (Exception e) { System.out.println(\"Parse error: \" + e.getMessage()); }\n");
            b.append("        return list;\n");
            b.append("    }\n");
            b.append("}\n");

            writer.write(b.toString());
        }
    }

    private static String parseExpression(FieldType type, String variable) {
        String v = variable + ".trim()";
        return switch (type) {
            case INTEGER -> "Integer.parseInt(" + v + ")";
            case DOUBLE -> "Double.parseDouble(" + v + ")";
            case BOOLEAN -> "Boolean.parseBoolean(" + v + ")";
            case LOCAL_DATE -> "LocalDate.parse(" + v + ".replace(\" \", \"\"))";
            case LOCAL_DATE_TIME -> "LocalDateTime.parse(" + v + ".replace(\" \", \"\"))";
            case STRING -> v;
        };
    }
}
