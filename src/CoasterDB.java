import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

public class CoasterDB {

    HashSet<Coaster> coasters;
    List<Coaster> tempList;
    Random random;

    public CoasterDB(String source) {
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
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnknownHeight() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.height == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnknownLength() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.length == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnknownInversions() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.inversionsNumber == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnknownSpeed() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.speed == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnknownWeight() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.seatingType == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUnrankedCoasters() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.rank == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public void removeUncompleteCoasters() {
        int size = this.coasters.size();
        this.coasters.removeIf(c -> c.name == null || c.height == null || c.inversionsNumber == null || c.speed == null || c.seatingType == null || c.length == null
        || c.manufacturer == null);
        System.out.println(size - this.coasters.size() + " coasters removed");
    }

    public Coaster findCoaster(String name) {
        for (Coaster c : this.coasters) {
            if (c.name.equals(name)) return c;
        }
        return null;
    }

    public Coaster randomCoaster() {
        int index = this.random.nextInt(0, this.coasters.size())-1;
        Coaster[] coasterArray = coasters.toArray(new Coaster[0]);
        return coasterArray[index];
    }

    public void keepHeight(Order order,int height) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> c.height != height);
            case LESS_THAN -> coasters.removeIf(c -> c.height >= height);
            case GREATER_THAN -> coasters.removeIf(c -> c.height <= height);
            case LESS_EQUAL_THAN -> coasters.removeIf(c -> c.height < height);
            case GREATER_EQUAL_THAN -> coasters.removeIf(c -> c.height > height);
            case NOT_EQUAL -> coasters.removeIf(c -> c.height == height);
            default -> throw new RuntimeException("Unhandled order: " + order);
        }
    }

    public void keepLength(Order order, int length) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> c.length != length);
            case LESS_THAN -> coasters.removeIf(c -> c.length >= length);
            case GREATER_THAN -> coasters.removeIf(c -> c.length <= length);
            case LESS_EQUAL_THAN -> coasters.removeIf(c -> c.length < length);
            case GREATER_EQUAL_THAN -> coasters.removeIf(c -> c.length > length);
            case NOT_EQUAL -> coasters.removeIf(c -> c.length == length);
            default -> throw new RuntimeException("Unhandled order: " + order);
        }
    }

    public void keepSpeed(Order order, int speed) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> c.speed != speed);
            case LESS_THAN -> coasters.removeIf(c -> c.speed >= speed);
            case GREATER_THAN -> coasters.removeIf(c -> c.speed <= speed);
            case LESS_EQUAL_THAN -> coasters.removeIf(c -> c.speed < speed);
            case GREATER_EQUAL_THAN -> coasters.removeIf(c -> c.speed > speed);
            case NOT_EQUAL -> coasters.removeIf(c -> c.speed == speed);
            default -> throw new RuntimeException("Unhandled order: " + order);
        }
    }

    public void keepInversions(Order order, int inversions) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> c.inversionsNumber != inversions);
            case LESS_THAN -> coasters.removeIf(c -> c.inversionsNumber >= inversions);
            case GREATER_THAN -> coasters.removeIf(c -> c.inversionsNumber <= inversions);
            case LESS_EQUAL_THAN -> coasters.removeIf(c -> c.inversionsNumber < inversions);
            case GREATER_EQUAL_THAN -> coasters.removeIf(c -> c.inversionsNumber > inversions);
            case NOT_EQUAL -> coasters.removeIf(c -> c.inversionsNumber == inversions);
            default -> throw new RuntimeException("Unhandled order: " + order);
        }
    }

    public void keepSeatingType(Order order, String seatingType) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> !c.seatingType.equalsIgnoreCase(seatingType));
            case NOT_EQUAL -> coasters.removeIf(c -> c.seatingType.equalsIgnoreCase(seatingType));
            default -> throw new RuntimeException("Wrong order: " + order);
        }
    }

    public void keepManufacturer(Order order, String manufacturer) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> !c.manufacturer.equalsIgnoreCase(manufacturer));
            case NOT_EQUAL -> coasters.removeIf(c -> c.manufacturer.equalsIgnoreCase(manufacturer));
            default -> throw new RuntimeException("Wrong order: " + order);
        }
    }

    public void keepStartingChar(Order order, char startingChar) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> !c.name.startsWith(String.valueOf(startingChar)));
            case NOT_EQUAL -> coasters.removeIf(c -> c.name.startsWith(String.valueOf(startingChar)));
            default -> throw new RuntimeException("Wrong order: " + order);
        }
    }

    public void keepCountry(Order order, String country) {
        switch (order) {
            case EQUAL -> coasters.removeIf(c -> !c.country.equalsIgnoreCase(country));
            case NOT_EQUAL -> coasters.removeIf(c -> c.country.equalsIgnoreCase(country));
            default -> throw new RuntimeException("Wrong order: " + order);
        }
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
