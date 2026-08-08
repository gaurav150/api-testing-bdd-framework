package resources.helpers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private static final Path LOG_DIR = Path.of("logs");
    private static final Path REQUEST_LOG = LOG_DIR.resolve("request.log");
    private static final Path RESPONSE_LOG = LOG_DIR.resolve("response.log");

    public static void initLogFiles() {
        try {
            Files.createDirectories(LOG_DIR);
            Files.writeString(REQUEST_LOG, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.writeString(RESPONSE_LOG, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to initialize log files", e);
        }
    }

    public static void writeLogSeparator(String scenarioName) {
        String separator = System.lineSeparator()
                + "========== Scenario: " + scenarioName + " =========="
                + System.lineSeparator();
        try {
            Files.writeString(REQUEST_LOG, separator, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(RESPONSE_LOG, separator, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write log separator", e);
        }
    }

    public RequestSpecification buildAddPlaceRequestSpec() {
        try {
            Files.createDirectories(LOG_DIR);
            PrintStream requestLogs = new PrintStream(
                    Files.newOutputStream(REQUEST_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            PrintStream responseLogs = new PrintStream(
                    Files.newOutputStream(RESPONSE_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            return new RequestSpecBuilder()
                    .setBaseUri(getGlobalValue("baseUrl"))
                    .addFilter(RequestLoggingFilter.logRequestTo(requestLogs))
                    .addFilter(ResponseLoggingFilter.logResponseTo(responseLogs))
                    .addQueryParam("key", "qaclick123")
                    .addHeader("Content-Type", "application/json")
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create request/response log files", e);
        }
    }

    public String getGlobalValue(String key) {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("global.properties")) {
            if (input == null) {
                throw new IllegalStateException("global.properties not found on classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load global.properties", e);
        }
        return properties.getProperty(key);
    }

    public RequestSpecification buildGetPlaceRequest(String placeId) {
        try {
            Files.createDirectories(LOG_DIR);
            PrintStream requestLogs = new PrintStream(
                    Files.newOutputStream(REQUEST_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            PrintStream responseLogs = new PrintStream(
                    Files.newOutputStream(REQUEST_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            return new RequestSpecBuilder()
                    .setBaseUri(getGlobalValue("baseUrl"))
                    .addFilter(RequestLoggingFilter.logRequestTo(requestLogs))
                    .addFilter(ResponseLoggingFilter.logResponseTo(responseLogs))
                    .addQueryParam("key", "qaclick123")
                    .addQueryParam("place_id", placeId)
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create request/response log files", e);
        }
    }

    public RequestSpecification buildDeletePlaceRequestSpec() {
        try {
            Files.createDirectories(LOG_DIR);
            PrintStream requestLogs = new PrintStream(
                    Files.newOutputStream(REQUEST_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            PrintStream responseLogs = new PrintStream(
                    Files.newOutputStream(RESPONSE_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            return new RequestSpecBuilder()
                    .setBaseUri(getGlobalValue("baseUrl"))
                    .addFilter(RequestLoggingFilter.logRequestTo(requestLogs))
                    .addFilter(ResponseLoggingFilter.logResponseTo(responseLogs))
                    .addQueryParam("key", "qaclick123")
                    .addHeader("Content-Type", "application/json")
                    .build();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create request/response log files", e);
        }
    }

    public String getJsonPath(Response response, String key) {
        String body = response.getBody().asString();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Response body is empty. Cannot read key: " + key);
        }
        JsonPath js = new JsonPath(body);
        return js.getString(key);
    }

}
