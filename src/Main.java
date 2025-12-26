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

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: API-Key must be first argument");
            System.exit(1);
        }
        APIkey = args[0];
        final Scanner sc = new Scanner(System.in);
        final String DBPath = "coasters.json";
        CoasterDB db = new CoasterDB(DBPath, true);

        // remove bad coasters
        db.removeUncompleteCoasters();
        db.removeUnrankedCoasters();

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
        System.out.println("Guess: " + curCoaster.name);
        while(db.coasters.size() > 1){
            answer = sc.nextLine().trim().toLowerCase();
            if(answer.length()!=7) {
                while(answer.length()!=7) {
                    System.out.println("Wrong amount of arguments!");
                    answer = sc.nextLine().trim().toLowerCase();
                }

            } else {
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
                curCoaster = db.randomCoaster();
                if(db.coasters.size() == 1) {
                    System.out.println("Coaster found!");
                    System.out.println(curCoaster);
                    System.exit(0);
                }
                guess++;
                System.out.println("Reduced Options to: "+ db.coasters.size());
                System.out.println("Guess: " + curCoaster.name);
            }
        }
    }
}
