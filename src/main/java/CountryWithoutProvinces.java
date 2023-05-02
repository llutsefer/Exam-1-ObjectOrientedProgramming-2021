import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CountryWithoutProvinces extends Country{
    Map<LocalDate, ArrayList<Integer>> dailyStatistic = new HashMap<>();
    protected CountryWithoutProvinces(String name) {
        super(name);
    }

    public void addDailyStatistic(LocalDate date, int illness, int death){
        ArrayList<Integer> data = new ArrayList<>();
        data.add(illness);
        data.add(death);
        dailyStatistic.put(date, data);
    }

    public int getConfirmedCases(LocalDate date){
        return dailyStatistic.get(date).get(0);
    }
    public int getDeath(LocalDate date){
        return dailyStatistic.get(date).get(1);
    }

}
