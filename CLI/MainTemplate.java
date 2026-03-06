package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MainTemplate {

    public static void main(String[] args) throws IOException {
        List<Prop> props = CSVParser.parse("propaganda.csv");

        Prop legnagyobbPlakat = props.stream()
                .max(Comparator.comparingDouble(MainTemplate::terulet))
                .orElseThrow();

        props.stream()
                .filter(prop -> Objects.equals(prop.getPart(), "FIDESZ"))
                .sorted(Comparator.comparing(Prop::getKihelyezesDatuma))
                .forEach(MainTemplate::printSorban);

        List<String> egyediPartok = props.stream()
                .map(Prop::getPart)
                .distinct()
                .collect(Collectors.toList());

        String randomPart = egyediPartok.get(ThreadLocalRandom.current().nextInt(egyediPartok.size()));
        long plakatok = props.stream()
                .filter(prop -> Objects.equals(prop.getPart(), randomPart))
                .count();

        Prop legtobbSzavas = props.stream()
                .max(Comparator.comparingInt(prop -> szoSzamlalo(prop.getPlakatSzoveg())))
                .orElseThrow();

        Map<String, String> partokEsDatumok = props.stream()
                .sorted(Comparator.comparing(Prop::getKihelyezesDatuma))
                .collect(Collectors.toMap(
                        Prop::getPart,
                        prop -> ev(prop.getKihelyezesDatuma()),
                        (elozo, uj) -> elozo + "," + uj,
                        LinkedHashMap::new));

        partokEsDatumok.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getValue().split(",")[0].trim())))
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        List<Prop> csorok = props.stream()
                .collect(Collectors.groupingBy(Prop::getPart))
                .entrySet().stream()
                .filter(e -> e.getValue().size() == 1)
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toList());

    }

    private static double terulet(Prop prop) {
        return parseDouble(prop.getPlakatSzelessegM()) * parseDouble(prop.getPlakatHosszusagM());
    }

    public static int szoSzamlalo(String szo) {
        return (szo == null || szo.isBlank()) ? 0 : szo.trim().split(" ").length;
    }

    private static double parseDouble(String value) {
        return Double.parseDouble(value.replace(",", "."));
    }

    private static String ev(String datum) {
        return datum.split("\\.")[0];
    }
}
