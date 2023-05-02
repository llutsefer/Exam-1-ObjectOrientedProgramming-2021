import java.time.LocalDate;
import java.util.ArrayList;

public class CountryWithProvinces extends Country{
    ArrayList<CountryWithoutProvinces> regionsArray = new ArrayList<>();
    protected CountryWithProvinces(String name, ArrayList<CountryWithoutProvinces> regionsArray) {
        super(name);
        this.regionsArray.addAll(regionsArray);
    }
    public int getConfirmedCases(LocalDate date){
        int result = 0;
        for (CountryWithoutProvinces x:regionsArray
             ) {
            result += x.dailyStatistic.get(date).get(0);
        }
      return result;
    }
    public int getDeath(LocalDate date){
        int result = 0;
        for (CountryWithoutProvinces x:regionsArray
        ) {
            result += x.dailyStatistic.get(date).get(1);
        }
        return result;
    }

}
