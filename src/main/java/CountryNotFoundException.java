public class CountryNotFoundException extends Exception {
    public CountryNotFoundException(String errorCountry) {
        super("Country not found: " + errorCountry);
    }
    public String getMessage(String errorCountry) {
        return "Country not found: " + errorCountry;
    }
}
