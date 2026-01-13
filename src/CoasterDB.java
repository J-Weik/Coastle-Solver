import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

public class CoasterDB {

    HashSet<Coaster> coasters;
    Random random;
    boolean log;
    boolean nullSafe;

    public CoasterDB(String source, boolean log, boolean nullSafe) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Coaster> tempList = mapper.readValue(
                    new File(source),
                    new TypeReference<List<Coaster>>() {}
            );
            coasters = new HashSet<Coaster>(tempList);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Einlesen der Coaster-JSON", e);
        }
        this.log = log;
        this.nullSafe = nullSafe;
        random = new Random(System.currentTimeMillis());
    }

    public void insertCoaster(Coaster coaster) {
        coasters.add(coaster);
    }

    public void removeCoaster(Coaster coaster) {
        coasters.remove(coaster);
    }

    public void removeUnknownCoasters() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.name.equals("Unknown"));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a name known");
    }

    public void removeUnknownHeight() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.height == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a height");
    }

    public void removeUnknownLength() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.length == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a length");
    }

    public void removeUnknownInversions() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.inversionsNumber == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have inversions known");
    }

    public void removeUnknownSpeed() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.speed == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have speed known");
    }

    public void removeUnknownSeating() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.seatingType == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have seating known");
    }

    public void removeUnrankedCoasters() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.rank == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that aren't ranked");
    }

    public void removeUncompleteCoasters() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.name == null || c.height == null
            || c.inversionsNumber == null || c.speed == null || c.seatingType == null
            || c.length == null || c.manufacturer == null);
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that are missing info");
    }

    public Coaster findCoaster(String name) {
        for (Coaster c : this.coasters) {
            if (c.name.equals(name)) return c;
        }
        return null;
    }

    public Coaster randomCoaster() {
        int index = this.random.nextInt(0, this.coasters.size());
        Coaster[] coasterArray = coasters.toArray(new Coaster[0]);
        return coasterArray[index];
    }

    public List<Coaster> getTopCoasters(int amount) {
        List<Coaster> coasterList = new ArrayList<>(coasters);
        coasterList.sort(Comparator.comparingInt(c -> c.rank != null ? c.rank : Integer.MAX_VALUE));
        int limit = Math.min(amount, coasterList.size());
        return coasterList.subList(0, limit);
    }

    public void keepHeight(Order order,int height) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.height, height, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a height +" + order.name() + " " + height);
    }

    public void keepLength(Order order, int length) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.length, length, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a length +" + order.name() + " " + length);
    }

    public void keepSpeed(Order order, int speed) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.speed, speed, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have a speed +" + order.name() + " " + speed);
    }

    public void keepInversions(Order order, int inversions) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.inversionsNumber, inversions, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have inversions +" + order.name() + " " + inversions);
    }

    public void keepSeatingType(Order order, String seatingType) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.seatingType, seatingType, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't have seating Type +" + order.name() + " " + seatingType);
    }

    public void keepManufacturer(Order order, String manufacturer) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.manufacturer, manufacturer, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that aren't manufactured by +" + order.name() + " " + manufacturer);
    }

    public void keepStartingChar(Order order, char startingChar) {
        int size = this.coasters.size();
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> c.name != null && !c.name.startsWith(String.valueOf(startingChar).toUpperCase()));
            case NOT_EQUAL -> coasters.removeIf(c -> c.name != null && c.name.startsWith(String.valueOf(startingChar).toUpperCase()));
            default -> throw new RuntimeException("Wrong order: " + order);
        }
        if(log) System.out.println(size - this.coasters.size() + " coasters removed, that aren't " + order.name() + " starting with " + startingChar);
    }

    public void keepCountry(Order order, String country) {
        int size = this.coasters.size();
        coasters.removeIf(c -> shouldRemove(c.country, country, order));
        if(log) System.out.println(size - this.coasters.size() + " coasters removed that don't stand in country +" + order.name() + " " + country);
    }

    public double getAverageHeight() {
        int size = this.coasters.size();
        int sum = 0;
        for(Coaster c : this.coasters) {
            if (c.height == null) {
                size--;
                continue;
            }
            sum += c.height;
        }
        return (double) sum /size;
    }

    public double getAverageLength() {
        int size = this.coasters.size();
        int sum = 0;
        for(Coaster c : this.coasters) {
            if (c.length == null) {
                size--;
                continue;
            }
            sum += c.length;
        }
        return (double) sum /size;
    }

    public double getAverageSpeed() {
        int size = this.coasters.size();
        int sum = 0;
        for(Coaster c : this.coasters) {
            if (c.speed == null) {
                size--;
                continue;
            }
            sum += c.speed;
        }
        return (double) sum /size;
    }

    public double getAverageInversions() {
        int size = this.coasters.size();
        int sum = 0;
        for(Coaster c : this.coasters) {
            if (c.inversionsNumber == null) {
                size--;
                continue;
            }
            sum += c.inversionsNumber;
        }
        return (double) sum /size;
    }

    public String getMostCommonManufacturer() {
        return mostFrequent(this.coasters, coaster -> coaster.manufacturer);
    }

    public String getMostCommonSeating() {
        return mostFrequent(this.coasters, coaster -> coaster.seatingType);
    }

    public <T> Map<T, Integer> countBy(Function<Coaster, T> classifier){
        Map<T ,Integer> counts = new HashMap<>();
        for(Coaster c : this.coasters) {
            T key = classifier.apply(c);
            counts.merge(key, 1, Integer::sum);
        }
        return counts;
    }

    private double rarityPenalty(String value, Map<String, Integer> freq) {
        if (value == null) return 0;
        int total = freq.values().stream().mapToInt(i -> i).sum();
        int f = freq.getOrDefault(value, 1);
        return Math.log((double) total / f);
    }

    private double splitScore(Coaster c, CoasterProfile p,
                              Map<String,Integer> seatingFreq,
                              Map<String,Integer> manufacturerFreq,
                              Map<String,Integer> countryFreq) {

        double score = 0;

        // annährung an median
        if (c.speed != null)      score += Math.abs(c.speed - p.avgSpeed);
        if (c.height != null)     score += Math.abs(c.height - p.avgHeight);
        if (c.length != null)     score += Math.abs(c.length - p.avgLength);
        if (c.inversionsNumber != null)
            score += Math.abs(c.inversionsNumber - p.avgInversions) * 2;

        // seltene typen sind schlechte spalter
        score += rarityPenalty(c.seatingType, seatingFreq) * 3.0;
        score += rarityPenalty(c.manufacturer, manufacturerFreq) * 2.0;
        score += rarityPenalty(c.country, countryFreq) * 4.0;

        return score;
    }

    public String getMostCommonCountry() {
        return mostFrequent(this.coasters, coaster -> coaster.country);
    }

    private static <T> T mostFrequent(
            Collection<Coaster> coasters,
            Function<Coaster, T> extractor
    ) {
        return coasters.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private class CoasterProfile {
        public double avgSpeed;
        public double avgHeight;
        public double avgLength;
        public double avgInversions;

        public String manufacturer;
        public String seatingType;
        public String country;
    }

    private CoasterProfile buildAverageCoasterProfile() {
        CoasterProfile p = new CoasterProfile();

        p.avgSpeed  = this.getAverageSpeed();
        p.avgHeight = getAverageHeight();
        p.avgLength = getAverageLength();
        p.avgInversions = getAverageInversions();

        p.manufacturer = getMostCommonManufacturer();
        p.seatingType  = getMostCommonSeating();
        p.country      = getMostCommonCountry();

        return p;
    }

    private double distance(Coaster c, CoasterProfile p) {
        double dist = 0;
        if (c.speed != null) dist += Math.pow(c.speed - p.avgSpeed, 2);
        if (c.height != null) dist += Math.pow(c.height - p.avgHeight, 2);
        if (c.length != null) dist += Math.pow(c.length - p.avgLength, 2);
        if (c.inversionsNumber != null) dist += Math.pow(c.inversionsNumber - p.avgInversions, 200);
        if (Objects.equals(c.manufacturer, p.manufacturer)) dist -= 1000;
        if (Objects.equals(c.seatingType, p.seatingType)) dist -= 500;
        if (Objects.equals(c.country, p.country)) dist -= 300;
        return dist;
    }

    public Coaster findMostAverageCoaster() {
        CoasterProfile profile = buildAverageCoasterProfile();

        return coasters.stream()
                .min(Comparator.comparingDouble(c -> distance(c, profile)))
                .orElse(null);
    }

    public Coaster findBestSplitCoaster() {
        CoasterProfile p = buildAverageCoasterProfile();
        Map<String, Integer> seatingFreq = countBy(c->c.seatingType);
        Map<String, Integer> manufacturerFreq = countBy(c->c.manufacturer);
        Map<String, Integer> countryFreq = countBy(c->c.country);

        return coasters.stream().min(Comparator.comparingDouble(
                c->splitScore(c, p, seatingFreq, manufacturerFreq, countryFreq)
        )).orElse(null);
    }

    private boolean shouldRemove(Integer value, int target, Order order) {
        if (value == null) return !nullSafe; // true -> entfernen, false -> behalten
        return switch (order) {
            case EQUAL -> value != target;
            case NOT_EQUAL -> value == target;
            case LESS_THAN -> value >= target;
            case GREATER_THAN -> value <= target;
            case LESS_EQUAL_THAN -> value > target;
            case GREATER_EQUAL_THAN -> value < target;
        };
    }

    private boolean shouldRemove(String value, String target, Order order) {
        if (value == null) return !nullSafe; // true -> entfernen, false -> behalten
        return switch (order) {
            case EQUAL -> !value.equalsIgnoreCase(target);
            case NOT_EQUAL -> value.equalsIgnoreCase(target);
            default -> throw new RuntimeException("Wrong order for String: " + order);
        };
    }

    public enum Order {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        LESS_THAN,
        GREATER_EQUAL_THAN,
        LESS_EQUAL_THAN
    }
}
