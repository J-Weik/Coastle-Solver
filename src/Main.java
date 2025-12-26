import java.util.Scanner;

public class Main {
    private static String APIkey;
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

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: API-Key must be first argument");
            System.exit(1);
        }
        APIkey = args[0];


        // ------------------------SETTINGS-------------------------
        final boolean outputRemovedCoasterInfo = false;
        final boolean chooseTopRatedCoasterAsGuess = false;
        boolean removeUnrankedCoasters = false;

        if(chooseTopRatedCoasterAsGuess) removeUnrankedCoasters = true;
        // ---------------------------------------------------------

        final Scanner sc = new Scanner(System.in);
        final String DBPath = "coasters.json";
        CoasterDB db = new CoasterDB(DBPath, outputRemovedCoasterInfo);


        // remove bad coasters
         db.removeUncompleteCoasters();

        if(removeUnrankedCoasters) db.removeUnrankedCoasters();

        System.out.println("Average Height of all Coasters: " + db.getAverageHeight());
        System.out.println("Average Length of all Coasters: " + db.getAverageLength());
        System.out.println("Average Speed of all Coasters: " + db.getAverageSpeed());
        System.out.println("Most Common Manufacturer: " + db.getMostCommonManufacturer());
        System.out.println("Most Common Country: " + db.getMostCommonCountry());
        System.out.println("Most Common Seating: " + db.getMostCommonSeating());

        // Choose Diamondback as first coaster
        Coaster curCoaster = db.findCoaster("Diamonback");

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
            if(chooseTopRatedCoasterAsGuess) curCoaster = db.getTopCoasters(1).getFirst();
            else curCoaster = db.randomCoaster();

            if(db.coasters.size() == 1) {
                System.out.println("Coaster found!");
                System.out.println(curCoaster);
                System.exit(0);
            }
            guess++;
            System.out.println("Reduced Options to: "+ db.coasters.size());
            System.out.println("Guess: " + curCoaster.name + " at " + curCoaster.park);
        }
    }
}
