import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Park {
    @JsonProperty("id")
    Integer parcId;
    @JsonProperty("name")
    String name;
    @JsonProperty("latitude")
    double latitude;
    @JsonProperty("longitude")
    double longitude;
    String countryName;

    @JsonProperty("country")
    private void unpackCountry(Map<String, Object> country) {
        if (country != null && country.get("name") != null) {
            this.countryName = country.get("name").toString().replaceFirst("^country\\.","");
        }
    }
}
