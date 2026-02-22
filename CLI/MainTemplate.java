import java.util.*;

public class MainTemplate {
    public static void main(String[] args) {

        ArrayList<DataRecord> records = new ArrayList<>(CSVParser.parse("input.csv"));

        System.out.println("list size: " + records.size());

    }
}
