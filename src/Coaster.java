import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coaster {

    public int id;
    public String name;

    public String park;
    public Integer parkId;
    public String country;

    public String status;
    public String material;
    public String seatingType;
    public String model;

    public Integer speed;
    public Integer length;
    public Integer height;
    public Integer inversionsNumber;

    public String manufacturer;
    public String restraint;
    public String launch;

    public Integer openingYear;
    public Double score;
    public Integer rank;

    // ---------- PARK ----------
    @JsonSetter("park")
    public void setPark(Object value) {
        if (value instanceof String s) {
            this.park = s;
        } else if (value instanceof Map<?, ?> map) {
            this.park = map.get("name").toString();

            Object countryObj = map.get("country");
            if (countryObj instanceof Map<?, ?> c) {
                this.country = c.get("name").toString();
            }
        }
    }

    // ---------- SIMPLE NAME OBJECTS ----------
    @JsonSetter("status")
    public void setStatus(Object value) {
        if (value instanceof String s) {
            this.status = s;
        } else if (value instanceof Map<?, ?> map) {
            this.status = map.get("name").toString();
        }
    }

    @JsonSetter("materialType")
    public void setMaterial(Object value) {
        if (value instanceof String s) {
            this.material = s;
        } else if (value instanceof Map<?, ?> map) {
            this.material = map.get("name").toString();
        }
    }

    @JsonSetter("seatingType")
    public void setSeatingType(Object value) {
        if (value instanceof String s) {
            this.seatingType = s;
        } else if (value instanceof Map<?, ?> map) {
            this.seatingType = map.get("name").toString();
        }
    }

    @JsonSetter("model")
    public void setModel(Object value) {
        if (value instanceof String s) {
            this.model = s;
        } else if (value instanceof Map<?, ?> map) {
            this.model = map.get("name").toString();
        }
    }

    @JsonSetter("manufacturer")
    public void setManufacturer(Object value) {
        if (value instanceof String s) {
            this.manufacturer = s;
        } else if (value instanceof Map<?, ?> map) {
            Object name = map.get("name");
            if (name != null) {
                this.manufacturer = name.toString();
            }
        }
    }

    @JsonSetter("restraint")
    public void setRestraint(Object value) {
        if (value instanceof String s) {
            this.restraint = s;
        } else if (value instanceof Map<?, ?> map) {
            Object name = map.get("name");
        }
        // this.restraint = ((String) restraint.get("name")).replaceAll("^restraint\\.", "");
    }

    // ---------- ARRAY ----------
    @JsonSetter("launchs")
    public void setLaunchs(List<Map<String, Object>> launchs) {
        if (launchs != null && !launchs.isEmpty()) {
            this.launch = ((String) launchs.getFirst().get("name")).replaceAll("^launch\\.lift\\.", "");
        }
    }

    // ---------- DATE ----------
    @JsonSetter("openingDate")
    public void setOpeningDate(String date) {
        this.openingYear = Integer.parseInt(date.substring(0, 4));
    }

    // ---------- HELPER ----------
    private Integer extractId(Map<String, Object> obj) {
        Object id = obj.get("@id");
        if (id instanceof String s) {
            return Integer.parseInt(s.replaceAll("\\D+", ""));
        }
        return null;
    }

    // ---------- TO-STRING ----------
    public String toString() {
        String str = "Name: " + this.name + "\n" + "Hersteller: " + this.manufacturer + "\n" + "Baujahr: " + this.openingYear + "\n" + "Höhe: " + this.height + "\n";
        str += "Länge: " + this.length + "\n" + "Geschwindigkeit: " + this.speed + "\n" + "Park: " + this.park + "\n" + "Land: " + this.country + "\n" + "Status: " + this.status + "\n";
        return str;
    }
}
