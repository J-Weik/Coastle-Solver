import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

public class APIService {

    public void saveCoastersToFile(List<Coaster> coasters, String filePath) {
        try {
            Path path = Paths.get(filePath);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(path.toFile(), coasters);
            System.out.println("Coasters gespeichert in " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Speichern der Coasters", e);
        }
    }

    public List<Coaster> loadCoastersFromFile(String filePath) {
        try {
            return mapper.readValue(
                    new File(filePath),
                    new TypeReference<List<Coaster>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden der Coasters", e);
        }
    }

    private static final String BASE_URL = "https://captaincoaster.com/api/coasters";

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String apiKey;

    public APIService(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }



    public Park getParkById(int id) {
        String url = "https://captaincoaster.com/api/parks/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", apiKey)
                .header("Accept", "application/ld+json")
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Park #" + id + " wurde nicht gefunden: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API Fehler: " + response.statusCode() + " -> " + response.body()
            );
        }
        return mapper.readValue(response.body(), Park.class);
    }

    public Coaster getCoaster(int id) {
        int retries = 5;
        long waitMs = 1000;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                String url = BASE_URL + "/" + id;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", apiKey)
                        .header("Accept", "application/ld+json")
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return mapper.readValue(response.body(), Coaster.class);
                }

                if (response.statusCode() == 522) {
                    System.out.println("522 Timeout bei Coaster " + id + " → Retry " + attempt);
                    Thread.sleep(waitMs);
                    waitMs *= 2; // exponential backoff
                    continue;
                }

                throw new RuntimeException("API Fehler: " + response.statusCode());

            } catch (Exception e) {
                if (attempt == retries) {
                    throw new RuntimeException("Coaster " + id + " endgültig fehlgeschlagen", e);
                }
            }
        }
        return null;
    }

    public String getCoasterJson(int id) {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", apiKey)
                .header("Accept", "application/ld+json")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Coaster #" + id + " wurde nicht gefunden: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API Fehler: " + response.statusCode() + " -> " + response.body()
            );
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.body());
    }

    public Coaster getCoasterByName(String name) {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = BASE_URL + "?page=1&name=" + encodedName + "&order%5Bid%5D=asc&order%5Brank%5D=asc";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", apiKey)
                .header("Accept", "application/ld+json")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Coaster " + name + " wurde nicht gefunden: " + e.getMessage(), e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API Fehler: " + response.statusCode() + " -> " + response.body()
            );
        }

        try {
            JsonNode root = mapper.readTree(response.body());
            JsonNode members = root.path("member");
            if (!members.isArray() || members.isEmpty()) {
                return null;
            }
            return mapper.treeToValue(members.get(0), Coaster.class);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Parsen des Coasters", e);
        }
    }


    public List<Coaster> getFirstCoasters() {
        List<Coaster> result = new ArrayList<>();
        String url = BASE_URL + "?page=1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", apiKey)
                .header("Accept", "application/ld+json")
                .GET()
                .build();
        HttpResponse<String> response =
                null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("API Fehler: " + e.getMessage());
            throw new RuntimeException(e);
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API Fehler: " + response.statusCode() + " -> " + response.body()
            );
        }
        JsonNode root = mapper.readTree(response.body());
        JsonNode members = root.path("member");
        List<Coaster> pageCoasters =
                mapper.readValue(
                        members.toString(),
                        new TypeReference<List<Coaster>>() {}
                );
        System.out.println("loaded " + pageCoasters.size() + " Coasters");
        for (Coaster coaster : pageCoasters) {
            result.add(getCoaster(coaster.id));
            System.out.println("got Coaster: " + coaster.name + ", " + coaster.id + "/6819" );
        }

        return result;
    }

    public List<Coaster> getAllCoasters() {
        List<Coaster> lowDetailCoasters = new ArrayList<>();
        List<Coaster> result = new ArrayList<>();

        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            try {
                String url = BASE_URL + "?page=" + page;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", apiKey)
                        .header("Accept", "application/ld+json")
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException(
                            "API Fehler: " + response.statusCode() + " -> " + response.body()
                    );
                }

                JsonNode root = mapper.readTree(response.body());
                JsonNode members = root.path("member");

                if (!members.isArray() || members.isEmpty()) {
                    hasMore = false;
                } else {
                    List<Coaster> pageCoasters =
                            mapper.readValue(
                                    members.toString(),
                                    new TypeReference<List<Coaster>>() {}
                            );

                    lowDetailCoasters.addAll(pageCoasters);
                    System.out.println("Loaded Page #" + page);
                    page++;
                }

            } catch (Exception e) {
                throw new RuntimeException("Fehler beim Laden der Coasters", e);
            }
        }

        for (Coaster coaster : lowDetailCoasters) {
            try {
                Coaster full = getCoaster(coaster.id);
                if (full != null) {
                    result.add(full);
                }
            } catch (Exception e) {
                System.out.println("/!\\ Überspringe Coaster " + coaster.id);
            }
            System.out.println("got Coaster: " + coaster.name + ", " + coaster.id + "/" + lowDetailCoasters.size());
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }
}
