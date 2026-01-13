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

        Weights best = new Weights();
        best.country = 1.0;
        best.manufacturer = 1.0;
        best.seating = 0.8;
        best.speed = 1.2;
        best.height = 1.0;
        best.length = 0.7;
        best.inversions = 0.5;

        double bestScore = evaluateWeights(db, best);

        for (int gen = 0; gen < 200; gen++) {
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

        return best;
    }

    public static Weights mutate(Weights base, Random r) {
        Weights w = base.copy();

        w.country      += r.nextGaussian() * 0.15;
        w.manufacturer += r.nextGaussian() * 0.15;
        w.seating      += r.nextGaussian() * 0.15;
        w.speed        += r.nextGaussian() * 0.15;
        w.height       += r.nextGaussian() * 0.15;
        w.length       += r.nextGaussian() * 0.15;
        w.inversions   += r.nextGaussian() * 0.15;

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
        return Math.max(0.1, Math.min(5.0, v));
    }
}