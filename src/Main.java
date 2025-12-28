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
        final guessedCoasterChooser chooser = guessedCoasterChooser.TOP_RATED;
        final boolean outputRemovedCoasterInfo = true;
        boolean removeUnrankedCoasters = true;
        final boolean removeInclompleteCoasters = true;

        // if(chooser == guessedCoasterChooser.TOP_RATED) removeUnrankedCoasters = true;
        // ---------------------------------------------------------

        final Scanner sc = new Scanner(System.in);
        final String DBPath = "coasters.json";
        CoasterDB db = new CoasterDB(DBPath, outputRemovedCoasterInfo, !removeInclompleteCoasters);


        // remove bad coasters
        if(removeInclompleteCoasters) db.removeUncompleteCoasters();
        if(removeUnrankedCoasters) db.removeUnrankedCoasters();

        // Choose Diamondback as first coaster
        Coaster curCoaster = db.findCoaster("Diamondback");

        CoasterDB.Order countryOrder;
        CoasterDB.Order manufacturerOrder;
        CoasterDB.Order seatingOrder;
        CoasterDB.Order inversionsOrder;
        CoasterDB.Order heightOrder;
        CoasterDB.Order lengthOrder;
        CoasterDB.Order speedOrder;

        String answer = "";
        int guess = 1;
        System.out.println("Type t for correct answer, f for false answer, g for greater than and l for lesser then");
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
            if(guess==3) {
                System.out.print("what is the first letter of the coaster?: ");
                db.keepStartingChar(CoasterDB.Order.EQUAL, sc.nextLine().charAt(0));
            }
            if (chooser == guessedCoasterChooser.TOP_RATED) curCoaster = db.getTopCoasters(1).getFirst();
            else if (chooser == guessedCoasterChooser.RANDOM) curCoaster = db.randomCoaster();
            else if (chooser == guessedCoasterChooser.AVERAGE_COASTER) curCoaster = curCoaster = db.findMostAverageCoaster();
            else curCoaster = db.randomCoaster();

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

    public enum guessedCoasterChooser {
        TOP_RATED,
        RANDOM,
        AVERAGE_COASTER
    }

    public static void updateCoasterDB() {
        APIService service =
                new APIService(APIkey);
        service.saveCoastersToFile(service.getAllCoasters(), "coasters.json");
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
        System.out.println("Most Average Coaster:\n" + db.findMostAverageCoaster().getInfo());
    }
}
