import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public abstract class Country {
    private final String name;
    static private Path confirmedCase;
    static private Path deaths;

    protected Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void setFiles(Path confirmedCase, Path deaths) throws FileNotFoundException {
        if(Files.exists(confirmedCase) && Files.exists(deaths) && Files.isReadable(confirmedCase) && Files.isReadable(deaths)){
            Country.confirmedCase = confirmedCase;
            Country.deaths = deaths;
        }else if(Files.exists(confirmedCase) && Files.isReadable(confirmedCase)){
            throw new FileNotFoundException(deaths.toString());
        }else if(Files.exists(deaths) && Files.isReadable(deaths)){
            throw new FileNotFoundException(confirmedCase.toString());
        }else{
            throw new FileNotFoundException(confirmedCase + "\n" + deaths);
        }
    }

    public static Country fromCsv(String countryName) throws CountryNotFoundException {
        Scanner confirmedCasesScanner;
        Scanner deathScanner;
        try {
            confirmedCasesScanner = new Scanner(new FileReader(confirmedCase.toFile()));
            deathScanner = new Scanner(new FileReader(deaths.toFile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String firstLineOfConfirmedCases = confirmedCasesScanner.nextLine();
        CountryColumns countryColumnsOfConfirmedCases = getCountryColumns(firstLineOfConfirmedCases, countryName);
        deathScanner.nextLine();
        deathScanner.nextLine();
        String lineOfConfirmedCases;
        String lineOfDeath;
            if(countryColumnsOfConfirmedCases.columnCount == 1){
                confirmedCasesScanner.nextLine();
                CountryWithoutProvinces result = new CountryWithoutProvinces(countryName);
                while (confirmedCasesScanner.hasNextLine() && deathScanner.hasNextLine()) {
                    lineOfConfirmedCases = confirmedCasesScanner.nextLine();
                    lineOfDeath = deathScanner.nextLine();
                    String[] dividedLineOfConfirmedCases = Arrays.stream(lineOfConfirmedCases.split(";")).toArray(String[]::new);
                    String[] dividedLineOfDeath = Arrays.stream(lineOfDeath.split(";")).toArray(String[]::new);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
                    LocalDate date = LocalDate.parse(dividedLineOfDeath[0], formatter);
                    int confirmedCasesInThisDate = Integer.parseInt(dividedLineOfConfirmedCases[countryColumnsOfConfirmedCases.firstColumnIndex]);
                    int deathsInThisDay = Integer.parseInt(dividedLineOfDeath[countryColumnsOfConfirmedCases.firstColumnIndex]);
                    result.addDailyStatistic(date, confirmedCasesInThisDate, deathsInThisDay);
                }
                confirmedCasesScanner.close();
                deathScanner.close();
                return result;
            }else{
                ArrayList<CountryWithoutProvinces> regionsArray = new ArrayList<>();
                lineOfConfirmedCases = confirmedCasesScanner.nextLine();
                String[] dividedLineOfConfirmedCases = Arrays.stream(lineOfConfirmedCases.split(";")).toArray(String[]::new);
                for(int i = 0;i< countryColumnsOfConfirmedCases.columnCount;i++){
                    regionsArray.add(new CountryWithoutProvinces (dividedLineOfConfirmedCases[countryColumnsOfConfirmedCases.firstColumnIndex+i]));
                }
                CountryWithProvinces result = new CountryWithProvinces(countryName, regionsArray);
                while (confirmedCasesScanner.hasNextLine() && deathScanner.hasNextLine()) {
                    lineOfConfirmedCases = confirmedCasesScanner.nextLine();
                    lineOfDeath = deathScanner.nextLine();
                    dividedLineOfConfirmedCases = Arrays.stream(lineOfConfirmedCases.split(";")).toArray(String[]::new);
                    String[] dividedLineOfDeath = Arrays.stream(lineOfDeath.split(";")).toArray(String[]::new);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
                    LocalDate date = LocalDate.parse(dividedLineOfDeath[0], formatter);
                    for(int i = 0;i<countryColumnsOfConfirmedCases.columnCount;i++){
                        int confirmedCasesInThisDate = Integer.parseInt(dividedLineOfConfirmedCases[countryColumnsOfConfirmedCases.firstColumnIndex+i]);
                        int deathsInThisDay = Integer.parseInt(dividedLineOfDeath[countryColumnsOfConfirmedCases.firstColumnIndex+i]);
                        result.regionsArray.get(i).addDailyStatistic(date, confirmedCasesInThisDate, deathsInThisDay);
                    }
                }
                confirmedCasesScanner.close();
                deathScanner.close();
                return result;
            }
    }

    public static ArrayList<Country> fromCsv(ArrayList<String> countriesName)  {
        ArrayList<Country> result = new ArrayList<>();
        for (String countryName: countriesName
             ) {
            try {
                result.add(fromCsv(countryName));
            }catch (CountryNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
        return result;
    }


    private static CountryColumns getCountryColumns(String firstLine, String countryToFind) throws CountryNotFoundException{
        String[] columnNames = firstLine.split(";");
        int firstColumnIndex = -1;
        int columnCount = 0;
        boolean foundCountry = false;
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(countryToFind)) {
                if (!foundCountry) {
                    firstColumnIndex = i;
                    foundCountry = true;
                }
                columnCount++;
            } else if (foundCountry) {
                break;
            }
        }
        if(firstColumnIndex == -1){
            throw new CountryNotFoundException(countryToFind);
        }else{
            return new CountryColumns(firstColumnIndex, columnCount);
        }
    }

    public static int getConfirmedCasesForAllCountries(LocalDate date) throws FileNotFoundException {
        Scanner confirmedCasesScanner = new Scanner(new FileReader(confirmedCase.toFile()));
        int result = 0;
        confirmedCasesScanner.nextLine();
        confirmedCasesScanner.nextLine();
        while (confirmedCasesScanner.hasNextLine()) {
            String lineOfConfirmedCases = confirmedCasesScanner.nextLine();
            String[] dividedLineOfConfirmedCases = Arrays.stream(lineOfConfirmedCases.split(";")).toArray(String[]::new);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate dateFromFile = LocalDate.parse(dividedLineOfConfirmedCases[0], formatter);
            dividedLineOfConfirmedCases[0] = "0";
            if(dateFromFile.equals(date)){
                for (String x: dividedLineOfConfirmedCases
                     ) {
                    result += Integer.parseInt(x);
                }
            }
        }
        confirmedCasesScanner.close();
        return result;
    }
    public static int getDeathForAllCountries(LocalDate date) throws FileNotFoundException {
        Scanner deathScanner = new Scanner(new FileReader(deaths.toFile()));
        int result = 0;
        deathScanner.nextLine();
        deathScanner.nextLine();
        while (deathScanner.hasNextLine()) {
            String lineOfConfirmedCases = deathScanner.nextLine();
            String[] dividedLineOfDeath = Arrays.stream(lineOfConfirmedCases.split(";")).toArray(String[]::new);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate dateFromFile = LocalDate.parse(dividedLineOfDeath[0], formatter);
            dividedLineOfDeath[0] = "0";
            if(dateFromFile.equals(date)){
                for (String x: dividedLineOfDeath
                ) {
                    result += Integer.parseInt(x);
                }
            }
        }
        deathScanner.close();
        return result;
    }

    private static class CountryColumns{
        public final int firstColumnIndex;
        public final int columnCount;

        public CountryColumns(int firstColumnIndex, int columnCount) {
            this.firstColumnIndex = firstColumnIndex;
            this.columnCount = columnCount;
        }

    }

}
