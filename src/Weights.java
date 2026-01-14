import java.util.Objects;

public class Weights {
    public double speed;
    public double height;
    public double length;
    public double inversions;

    public double manufacturer;
    public double seating;
    public double country;

    public Weights copy() {
        Weights w = new Weights();
        w.country = country;
        w.manufacturer = manufacturer;
        w.seating = seating;
        w.speed = speed;
        w.height = height;
        w.length = length;
        w.inversions = inversions;
        return w;
    }

    public Weights(double country, double manufacturer, double seating, double inversions, double speed, double height, double length) {
        this.speed = speed;
        this.height = height;
        this.length = length;
        this.inversions = inversions;
        this.manufacturer = manufacturer;
        this.seating = seating;
        this.country = country;
    }

    public Weights() {}

    public String toString() {
        return this.country +"," + this.manufacturer +"," + this.seating +"," + this.inversions +"," + this.speed +"," + this.height +"," + this.length ;
    }
}