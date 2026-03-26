import java.util.Map;

public class HourlyRateStrategy implements RateStrategy {
    private final Map<SpotType, Integer> priceMap;

    public HourlyRateStrategy(Map<SpotType, Integer> priceMap) {
        this.priceMap = priceMap;
    }

    @Override
    public int getHourlyRate(SpotType spotType) {
        Integer rate = priceMap.get(spotType);
        if (rate == null) throw new IllegalArgumentException("No rate configured for spot type: " + spotType);
        return rate;
    }
}
