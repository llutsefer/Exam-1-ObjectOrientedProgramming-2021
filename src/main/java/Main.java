import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Path confirmed_cases = Paths.get("res/confirmed_cases.csv");
        Path resultFile = Paths.get("res/result.csv");
        Path deaths = Paths.get("res/deaths.csv");
        Country.setFiles(confirmed_cases, deaths);
        String dateString = "4/11/20";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        CountryWithProvinces test;
        try {
            test = (CountryWithProvinces) Country.fromCsv("Australia");
        } catch (CountryNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(test.getConfirmedCases(date));
        System.out.println(test.getDeaths(date));
        try {
            Country.saveToDataFile(resultFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}