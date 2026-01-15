import java.util.Objects;
import java.util.Set;

public class GameSimulator {

    private final CoasterDB originalDB;

    public GameSimulator(CoasterDB db) {
        this.originalDB = db;
    }

    public int simulateGame(Coaster target, Weights weights) {
        CoasterDB db = new CoasterDB(originalDB); // Deep copy!
        int guesses = 0;

        while (db.coasters.size() > 1) {
            Coaster guess = db.findBestSplitCoaster(weights);
            guesses++;

            SimulatedAnswer a = simulateAnswer(guess, target);

            db.keepCountry(a.country, guess.country);
            db.keepManufacturer(a.manufacturer, guess.manufacturer);
            db.keepSeatingType(a.seating, guess.seatingType);
            db.keepInversions(a.inversions, guess.inversionsNumber);
            db.keepHeight(a.height, guess.height);
            db.keepLength(a.length, guess.length);
            db.keepSpeed(a.speed, guess.speed);

            if (guesses > 7) break; // safety
        }

        return guesses;
    }

    public static class SimulatedAnswer {
        CoasterDB.Order country;
        CoasterDB.Order manufacturer;
        CoasterDB.Order seating;
        CoasterDB.Order inversions;
        CoasterDB.Order height;
        CoasterDB.Order length;
        CoasterDB.Order speed;

        @Override
        public String toString() {
            return orderToString(country) + orderToString(manufacturer) + orderToString(seating) + orderToString(inversions) + orderToString(height) + orderToString(length) + orderToString(speed);
        }

        private String orderToString(CoasterDB.Order order) {
            return switch (order) {
                case EQUAL -> "t";
                case NOT_EQUAL -> "f";
                case GREATER_THAN -> "g";
                case LESS_THAN -> "l";
                default -> "ERROR";
            };
        }
    }

    public SimulatedAnswer simulateAnswer(Coaster guess, Coaster target) {
        SimulatedAnswer a = new SimulatedAnswer();

        a.country = Objects.equals(guess.country, target.country)
                ? CoasterDB.Order.EQUAL : CoasterDB.Order.NOT_EQUAL;

        a.manufacturer = Objects.equals(guess.manufacturer, target.manufacturer)
                ? CoasterDB.Order.EQUAL : CoasterDB.Order.NOT_EQUAL;

        a.seating = Objects.equals(guess.seatingType, target.seatingType)
                ? CoasterDB.Order.EQUAL : CoasterDB.Order.NOT_EQUAL;

        a.inversions = (int) target.inversionsNumber == guess.inversionsNumber
                ? CoasterDB.Order.EQUAL
                : target.inversionsNumber > guess.inversionsNumber
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.height = (int) target.height == guess.height
                ? CoasterDB.Order.EQUAL
                : target.height > guess.height
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.length = (int) target.length == guess.length
                ? CoasterDB.Order.EQUAL
                : target.length > guess.length
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.speed = (int) target.speed == guess.speed
                ? CoasterDB.Order.EQUAL
                : target.speed > guess.speed
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        return a;
    }

    public int testSimulateGame(
            Coaster target,
            CoasterDB originalDB,
            Weights weights
    ) {
        CoasterDB db = new CoasterDB(originalDB);
        int guesses = 0;

        while (db.coasters.size() > 1) {
            Coaster guess = db.findBestSplitCoaster(weights);
            System.out.println("Guess: " + guess);
            guesses++;
            GameSimulator.SimulatedAnswer a = simulateAnswer(guess, target);
            System.out.println("Answer: " + a.toString());

            db.keepCountry(a.country, guess.country);
            db.keepManufacturer(a.manufacturer, guess.manufacturer);
            db.keepSeatingType(a.seating, guess.seatingType);
            db.keepInversions(a.inversions, guess.inversionsNumber);
            db.keepHeight(a.height, guess.height);
            db.keepLength(a.length, guess.length);
            db.keepSpeed(a.speed, guess.speed);

            if (guesses > 7) break; // safety
        }
        return guesses;
    }

    public double averageRemainingAfterFirstGuess(
            Coaster spalter,
            CoasterDB oGpool,
            CoasterDB oGallPos
    ) {
        int totalRemaining = 0;

        for (Coaster target : oGpool.coasters) {
            CoasterDB pool = new CoasterDB(oGpool);

            SimulatedAnswer a = simulateAnswer(spalter, target);

            pool.keepCountry(a.country, spalter.country);
            pool.keepManufacturer(a.manufacturer, spalter.manufacturer);
            pool.keepSeatingType(a.seating, spalter.seatingType);
            pool.keepInversions(a.inversions, spalter.inversionsNumber);
            pool.keepHeight(a.height, spalter.height);
            pool.keepLength(a.length, spalter.length);
            pool.keepSpeed(a.speed, spalter.speed);

            totalRemaining += pool.coasters.size();
        }

        return (double) totalRemaining / oGpool.coasters.size();
    }
}
