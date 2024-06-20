package org.vaadin.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class GreetService {
    private HttpClient client;
    private ObjectMapper objectMapper;

    public GreetService() {
        this.client = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
    }

    public List<DataEntry> getAllDataItems() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/users"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(objectMapper.readValue(response.body(), DataEntry[].class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data", e);
        }
    }

    public DataEntry getUserById(UUID id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/users/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), DataEntry.class);
            } else {
                System.err.println("Failed to fetch user by ID: " + response.body());
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user by ID", e);
        }
    }

    public void createDataItem(DataEntry item) {
        try {
            String json = objectMapper.writeValueAsString(item);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data item", e);
        }
    }

    public void updateDataItem(DataEntry item) {
        try {
            String json = objectMapper.writeValueAsString(item);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/users/" + item.get_id()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update data item", e);
        }
    }

    public void deleteDataItem(String userDataJson) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/users"))
                    .header("Content-Type", "application/json")
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(userDataJson))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete data item", e);
        }
    }


    public void generatePDF(String mscode) {
        try {
            String requestBody = "{\"mscode\":\"" + mscode + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8090/generate-pdf"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(body -> {
                        System.out.println("PDF generado exitosamente: " + body);
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }


    public void exportCsv() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/export-csv"))
                .POST(HttpRequest.BodyPublishers.ofString(""))  // No se necesita cuerpo para esta solicitud
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    Notification.show("CSV exportado con éxito", 3000, Notification.Position.BOTTOM_START);
                })
                .exceptionally(e -> {
                    Notification.show("Error al exportar CSV", 3000, Notification.Position.BOTTOM_START);
                    e.printStackTrace();
                    return null;
                });
    }





}
