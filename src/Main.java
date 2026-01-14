import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static String APIkey;
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: API-Key must be first argument");
            System.exit(1);
        }
        APIkey = args[0];

        // ------------------------SETTINGS-------------------------

        Properties p = new Properties();

        try (var in = Main.class.getResourceAsStream("/default.properties")) {
            if(in!=null) {
                p.load(in);
            }
        } catch (IOException _) {}

        try (var in = new FileInputStream("config.properties")) {
            p.load(in);
        } catch (IOException _) {}

        ConfigOptions cfg = ConfigOptions.from(p);


        final GuessedCoasterChooser chooser = cfg.chooser();
        final Dataset data = cfg.dataset();
        final boolean outputRemovedCoasterInfo = cfg.outputRemovedCoasterInfo();
        boolean removeIncompleteCoasters = cfg.removeIncompleteCoasters();
        boolean startWithSpecificCoaster = cfg.startWithSpecificCoaster();
        String coasterName = cfg.coasterName();

        // ---------------------------------------------------------
        final String BASE_PATH = "src/datasets/";
        final Scanner sc = new Scanner(System.in);
        String DBPath;
        switch (data) {
            case EASY -> DBPath = BASE_PATH + "easy.json";
            case HARD -> DBPath = BASE_PATH + "hard.json";
            default -> DBPath = BASE_PATH + "all.json";
        }
        CoasterDB db = new CoasterDB(DBPath, outputRemovedCoasterInfo, !removeIncompleteCoasters);
        CoasterDB posGuesses = new CoasterDB(BASE_PATH + "all.json", false, false);
        posGuesses.removeUncompleteCoasters();
        Weights hardWeights = new Weights(7.63067057117832,2.1315017107523193,2.238006451422379,4.478439068601903,0.003187387962721118,0.5690990135185374,0.0935383565769079);
        Weights easyWeights = new Weights(4.862045679222069,2.1315017107523193,3.5917418806495247,1.943243644291043,0.2851287747378798,0.0935383565769079,0.003187387962721118);

        // remove bad coasters if they have a chance to be chosen as a guess
        if (chooser == GuessedCoasterChooser.AVERAGE_COASTER
                || chooser == GuessedCoasterChooser.BEST_SPLITTER
                || chooser == GuessedCoasterChooser.RANDOM) removeIncompleteCoasters = true;

        // remove bad coasters
        if(removeIncompleteCoasters) db.removeUncompleteCoasters();

//        WeightOptimizer wo = new WeightOptimizer(new GameSimulator(db));
//        System.out.println(wo.optimizeWeights(db));

        // Choose Diamondback as first coaster
        Coaster curCoaster;
        if (startWithSpecificCoaster) curCoaster = db.findCoaster(coasterName);
        else {
            curCoaster = getCoaster(chooser, data, db, posGuesses, hardWeights, easyWeights);
        }

        CoasterDB.Order countryOrder;
        CoasterDB.Order manufacturerOrder;
        CoasterDB.Order seatingOrder;
        CoasterDB.Order inversionsOrder;
        CoasterDB.Order heightOrder;
        CoasterDB.Order lengthOrder;
        CoasterDB.Order speedOrder;

        String answer;
        int guess = 1;
        System.out.println("Type t for correct answer, f for false answer, g for greater than and l for lesser then");
        System.out.println("Possible Coasters: " + db.coasters.size());
        System.out.println("Guess: " + curCoaster);
        while(db.coasters.size() > 1){
            answer = readValidAnswer(sc);

            countryOrder = getOrderFromChar(answer.charAt(0));
            manufacturerOrder = getOrderFromChar(answer.charAt(1));
            seatingOrder = getOrderFromChar(answer.charAt(2));
            inversionsOrder = getOrderFromChar(answer.charAt(3));
            heightOrder = getOrderFromChar(answer.charAt(4));
            lengthOrder = getOrderFromChar(answer.charAt(5));
            speedOrder = getOrderFromChar(answer.charAt(6));

            db.keepCountry(countryOrder, curCoaster.country);
            db.keepManufacturer(manufacturerOrder, curCoaster.manufacturer);
            db.keepSeatingType(seatingOrder, curCoaster.seatingType);
            db.keepInversions(inversionsOrder, curCoaster.inversionsNumber);
            db.keepHeight(heightOrder, curCoaster.height);
            db.keepLength(lengthOrder, curCoaster.length);
            db.keepSpeed(speedOrder, curCoaster.speed);
            if(guess==3 && db.coasters.size() > 1) {
                System.out.print("what is the first letter of the coaster?: ");
                db.keepStartingChar(CoasterDB.Order.EQUAL, sc.nextLine().charAt(0));
            }
            curCoaster = getCoaster(chooser, data, db, db, hardWeights, easyWeights);

            if(db.coasters.size() == 1) {
                System.out.println("Coaster found with "+ guess + " Guesses!");
                System.out.println(curCoaster);
                System.exit(0);
            }
            guess++;
            System.out.println("Reduced Options to: "+ db.coasters.size());
            System.out.println("Guess: " + curCoaster.name + " at " + curCoaster.park);
        }
    }

    private static Coaster getCoaster(GuessedCoasterChooser chooser, Dataset data, CoasterDB db, CoasterDB posGuesses, Weights hardWeights, Weights easyWeights) {
        Coaster curCoaster;
        if (chooser == GuessedCoasterChooser.TOP_RATED) curCoaster = db.getTopCoasters(1).getFirst();
        else if (chooser == GuessedCoasterChooser.RANDOM) curCoaster = db.randomCoaster();
        else if (chooser == GuessedCoasterChooser.AVERAGE_COASTER) curCoaster = posGuesses.findMostAverageCoaster();
        else if (chooser == GuessedCoasterChooser.BEST_SPLITTER && data == Dataset.HARD) curCoaster = posGuesses.findBestSplitCoaster(hardWeights);
        else if (chooser == GuessedCoasterChooser.BEST_SPLITTER && data == Dataset.EASY) curCoaster = posGuesses.findBestSplitCoaster(easyWeights);
        else curCoaster = db.randomCoaster();
        return curCoaster;
    }

    public enum GuessedCoasterChooser {
        TOP_RATED,
        RANDOM,
        AVERAGE_COASTER,
        BEST_SPLITTER;

        public static GuessedCoasterChooser fromString(String s) {
            return switch (s.toLowerCase()) {
                case "top_rated", "top-rated", "top" -> TOP_RATED;
                case "random" -> RANDOM;
                case "average", "average_coaster" -> AVERAGE_COASTER;
                case "best_splitter", "best-splitter", "best" -> BEST_SPLITTER;
                default -> throw new IllegalArgumentException("Unknown chooser: " + s);
            };
        }
    }

    public enum Dataset {
        EASY,
        HARD,
        ALL;

        public static Dataset fromString(String s) {
            return switch (s.toLowerCase()) {
                case "easy" -> EASY;
                case "hard" -> HARD;
                case "all" -> ALL;
                default -> throw new IllegalArgumentException("Unknown dataset: " + s);
            };
        }
    }

    public record ConfigOptions(
            GuessedCoasterChooser chooser,
            Dataset dataset,
            boolean outputRemovedCoasterInfo,
            boolean removeIncompleteCoasters,
            boolean startWithSpecificCoaster,
            String coasterName
    ) {
        public static ConfigOptions from(Properties p) {
            String chooserStr = p.getProperty("chooser", "best_splitter");
            String datasetStr = p.getProperty("dataset", "easy");

            GuessedCoasterChooser chooser = GuessedCoasterChooser.fromString(chooserStr);
            Dataset dataset = Dataset.fromString(datasetStr);

            boolean outputRemoved = Boolean.parseBoolean(
                    p.getProperty("outputRemovedCoasterInfo", "false")
            );
            boolean removeIncomplete = Boolean.parseBoolean(
                    p.getProperty("removeIncompleteCoasters", "true")
            );
            boolean startWithSpecific = Boolean.parseBoolean(
                    p.getProperty("startWithSpecificCoaster", "false")
            );
            String coasterName = p.getProperty("coasterName", "Mako");

            return new ConfigOptions(
                    chooser,
                    dataset,
                    outputRemoved,
                    removeIncomplete,
                    startWithSpecific,
                    coasterName
            );
        }
    }

    public static void updateCoasterDB() {
        APIService service =
                new APIService(APIkey);
        service.saveCoastersToFile(service.getAllCoasters(), "all.json");
    }

    public static void testAPI(){
        APIService service =
                new APIService(APIkey);
        service.getFirstCoasters();
    }

    public static CoasterDB.Order getOrderFromChar(char c) {
        CoasterDB.Order o;
        switch(c) {
            case 't' -> o = CoasterDB.Order.EQUAL;
            case 'f' -> o = CoasterDB.Order.NOT_EQUAL;
            case 'g' -> o = CoasterDB.Order.GREATER_THAN;
            case 'l' -> o = CoasterDB.Order.LESS_THAN;
            default -> throw new RuntimeException("Unhandled order: " + c);
        }
        return o;
    }

    private static String readValidAnswer(Scanner sc) {
        while (true) {
            System.out.print("Coastle Response: ");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.length() != 7) {
                System.out.println("Input must have exactly 7 characters!");
                continue;
            }

            boolean valid = true;
            for (char c : input.toCharArray()) {
                if (c != 't' && c != 'f' && c != 'g' && c != 'l') {
                    valid = false;
                    break;
                }
            }

            if (!valid) {
                System.out.println("Only t, f, g and l are allowed!");
                continue;
            }

            return input;
        }
    }

    private static  void randomStuff(CoasterDB db) {
        System.out.println("Average Height of all Coasters: " + db.getAverageHeight());
        System.out.println("Average Length of all Coasters: " + db.getAverageLength());
        System.out.println("Average Speed of all Coasters: " + db.getAverageSpeed());
        System.out.println("Average Inversions of all Coasters: " + db.getAverageInversions());
        System.out.println("Most Common Manufacturer: " + db.getMostCommonManufacturer());
        System.out.println("Most Common Country: " + db.getMostCommonCountry());
        System.out.println("Most Common Seating: " + db.getMostCommonSeating());
        System.out.println("Most Average Coaster:\n" + db.findMostAverageCoaster().printInfo());
        System.out.println("AmountOfSeatingTypes: " + db.countBy(coaster -> coaster.seatingType));
        System.out.println("AmountOfManufactures: " + db.countBy(coaster -> coaster.manufacturer));
        System.out.println("AmountOfCountry: " + db.countBy(coaster -> coaster.country));
        System.out.println("AmountOfInversions: " + db.countBy(coaster -> coaster.inversionsNumber));
        System.out.println("AmountOfCountry: " + db.countBy(coaster -> coaster.country));
    }
}
