import java.util.Objects;

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

    public class SimulatedAnswer {
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

        a.inversions = Integer.compare(target.inversionsNumber, guess.inversionsNumber) == 0
                ? CoasterDB.Order.EQUAL
                : target.inversionsNumber > guess.inversionsNumber
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.height = Integer.compare(target.height, guess.height) == 0
                ? CoasterDB.Order.EQUAL
                : target.height > guess.height
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.length = Integer.compare(target.length, guess.length) == 0
                ? CoasterDB.Order.EQUAL
                : target.length > guess.length
                ? CoasterDB.Order.GREATER_THAN
                : CoasterDB.Order.LESS_THAN;

        a.speed = Integer.compare(target.speed, guess.speed) == 0
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
}
