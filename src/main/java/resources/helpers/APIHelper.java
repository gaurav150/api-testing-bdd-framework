package resources.helpers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class APIHelper {

    public RequestSpecification buildAddPlaceRequestSpec() {
        try {
            Path logDir = Path.of("logs");
            Files.createDirectories(logDir);
            PrintStream requestLogs = new PrintStream(Files.newOutputStream(logDir.resolve("requestLog.txt")));
            PrintStream responseLogs = new PrintStream(Files.newOutputStream(logDir.resolve("responseLog.txt")));
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
}
