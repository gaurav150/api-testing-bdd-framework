package wiremock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceResponseTransformer extends ResponseDefinitionTransformer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    // place_id -> name
    private static final Map<String, String> PLACE_MAP = new ConcurrentHashMap<>();

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource files, Parameters parameters) {
        try {
            String url = request.getUrl();
            String method = request.getMethod().value();

            if (url.startsWith("/maps/api/place/add/json") && "POST".equalsIgnoreCase(method)) {
                JsonNode node = MAPPER.readTree(request.getBodyAsString());
                String name = node.has("name") ? node.get("name").asText() : "unknown";
                String placeId = UUID.randomUUID().toString();
                PLACE_MAP.put(placeId, name);
                String body = MAPPER.createObjectNode()
                        .put("status", "OK")
                        .put("place_id", placeId)
                        .put("scope", "APP")
                        .toString();
                return ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .build();
            }

            if (url.startsWith("/maps/api/place/get/json") && "GET".equalsIgnoreCase(method)) {
                // extract place_id query param
                String placeId = null;
                String[] parts = url.split("\\?");
                if (parts.length > 1) {
                    String query = parts[1];
                    for (String param : query.split("&")) {
                        String[] kv = param.split("=");
                        if (kv.length == 2 && "place_id".equals(kv[0])) {
                            placeId = kv[1];
                            break;
                        }
                    }
                }
                String name = placeId != null ? PLACE_MAP.get(placeId) : null;
                if (name == null) {
                    String body = MAPPER.createObjectNode()
                            .put("error", "place not found")
                            .toString();
                    return ResponseDefinitionBuilder.responseDefinition()
                            .withStatus(404)
                            .withHeader("Content-Type", "application/json")
                            .withBody(body)
                            .build();
                }
                String body = MAPPER.createObjectNode()
                        .put("name", name)
                        .put("place_id", placeId)
                        .toString();
                return ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .build();
            }

            if (url.startsWith("/maps/api/place/delete/json") && "POST".equalsIgnoreCase(method)) {
                JsonNode node = MAPPER.readTree(request.getBodyAsString());
                String placeId = node.has("place_id") ? node.get("place_id").asText() : null;
                if (placeId != null) {
                    PLACE_MAP.remove(placeId);
                }
                String body = MAPPER.createObjectNode()
                        .put("status", "OK")
                        .toString();
                return ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseDefinitionBuilder.responseDefinition().withStatus(500).withBody("{}").build();
    }

    @Override
    public String getName() {
        return "place-transformer";
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }
}
