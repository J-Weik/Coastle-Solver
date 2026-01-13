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

    public Weights(double speed, double height, double length, double inversions, double manufacturer, double seating, double country) {
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
        return "Country: " + this.country +"\n Manufacturer: " + this.manufacturer +"\n Seating: " + this.seating +"\n Inversioons: " + this.inversions +"\n Speed: " + this.speed +"\n Height: " + this.height +"\n Length: " + this.length ;
    }
}