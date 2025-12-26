import java.nio.file.Path;

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
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: API-Key must be first argument");
            System.exit(1);
        }
        APIkey = args[0];
        String DBPath = "coasters.json";
        CoasterDB db = new CoasterDB(DBPath);

        testAPI();

//        db.removeUncompleteCoasters();
//        db.removeUnrankedCoasters();
//
//        db.keepCountry(CoasterDB.Order.NOT_EQUAL, "usa");
//        db.keepManufacturer(CoasterDB.Order.NOT_EQUAL, "Bolliger & Mabillard");
//        db.keepSeatingType(CoasterDB.Order.EQUAL, "sit down");
//        db.keepInversions(CoasterDB.Order.EQUAL, 0);
//        db.keepHeight(CoasterDB.Order.GREATER_THAN, 70);
//        db.keepLength(CoasterDB.Order.LESS_THAN, 1610);
//        db.keepSpeed(CoasterDB.Order.GREATER_THAN, 129);
//
//        System.out.println("Reduced Options to: "+ db.coasters.size());
//
//        for (Coaster coaster : db.coasters) {
//            System.out.println(coaster.toString()+"\n");
//        }
    }
}
