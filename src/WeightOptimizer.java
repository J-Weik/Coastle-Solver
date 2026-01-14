import java.util.Random;

public class WeightOptimizer {

    private static GameSimulator simulator;
    private final Random random = new Random();

    public WeightOptimizer(GameSimulator simulator) {
        this.simulator = simulator;
    }

    public static double evaluateWeights(CoasterDB db, Weights w) {
        int totalGuesses = 0;

        for (Coaster target : db.coasters) {
            totalGuesses += simulator.simulateGame(target, w);
        }
        return (double) totalGuesses / db.coasters.size();
    }

    public Weights optimizeWeights(CoasterDB db) {
        Random r = new Random();

        Weights best = new Weights(7.63067057117832,2.1315017107523193,2.238006451422379,4.478439068601903,0.003187387962721118,0.5690990135185374,0.0935383565769079);

        double bestScore = evaluateWeights(db, best);

        for (int gen = 0; gen < 30; gen++) {
            System.out.println("Gen: " + gen);
            for (int i = 0; i < 30; i++) {
                Weights candidate = mutate(best, r);
                double score = evaluateWeights(db, candidate);

                if (score < bestScore) {
                    best = candidate;
                    bestScore = score;
                    System.out.println(
                            "Gen " + gen +
                                    " new best: " + bestScore
                    );
                }
            }
        }
        System.out.println("Best score: " + bestScore);
        return best;
    }

    public static Weights mutate(Weights base, Random r) {
        Weights w = base.copy();

        int pick = r.nextInt(7);
        double delta = r.nextGaussian() * 1.3;

        switch (pick) {
            case 0 -> w.country += delta;
            case 1 -> w.manufacturer += delta;
            case 2 -> w.seating += delta;
            case 3 -> w.speed += delta;
            case 4 -> w.height += delta;
            case 5 -> w.length += delta;
            case 6 -> w.inversions += delta;
        }

        clamp(w);
        return w;
    }

    private static void clamp(Weights w) {
        w.country      = clamp(w.country);
        w.manufacturer = clamp(w.manufacturer);
        w.seating      = clamp(w.seating);
        w.speed        = clamp(w.speed);
        w.height       = clamp(w.height);
        w.length       = clamp(w.length);
        w.inversions   = clamp(w.inversions);
    }

    private static double clamp(double v) {
        return Math.max(0.0001, Math.min(10.0, v));
    }
}

// Best score: 1.9626168224299065 easy
//4.862045679222069,2.1315017107523193,3.5917418806495247,1.943243644291043,0.2851287747378798,0.0935383565769079,0.003187387962721118

// Best score: 2.466666666666667 hard
// 7.63067057117832,2.1315017107523193,2.238006451422379,4.478439068601903,0.003187387962721118,0.5690990135185374,0.0935383565769079