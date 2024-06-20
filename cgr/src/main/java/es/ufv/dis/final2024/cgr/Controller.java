package es.ufv.dis.final2024.cgr;

import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class Controller {

    private final String filePath = "./src/main/resources/cp-national-datafile.json";

    @GetMapping("/users")
    public ArrayList<DataEntry> users() {
        JsonReader reader = new JsonReader();
        ArrayList<DataEntry> userList = reader.readJsonFile(filePath);
        return userList;
    }

    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataEntry> create(@RequestBody DataEntry newUser) {
        System.out.println("Received new user: " + newUser);  // Debugging: ver qué recibes exactamente
        DataHandling dataHandling = new DataHandling();
        ArrayList<DataEntry> usersList = dataHandling.addUser(newUser);
        if (usersList == null) {
            System.out.println("User ID already exists or error in adding user");  // Más información de debugging
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        DataEntry user = usersList.get(usersList.size() - 1);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@RequestBody DataEntry targetUser) {
        DataHandling dataHandling = new DataHandling();
        boolean isDeleted = dataHandling.deleteUser(targetUser);
        if (isDeleted) {
            return ResponseEntity.ok().build(); // Devuelve 200 OK si se eliminó correctamente
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si no se encuentra el usuario
        }
    }
    // Método PUT para actualizar un usuario
    @PutMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataEntry> updateUser(@RequestBody DataEntry updatedUser) {
        DataHandling dataHandling = new DataHandling();
        boolean isUpdated = dataHandling.updateUser(updatedUser);
        if (isUpdated) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<DataEntry> getUserById(@PathVariable UUID id) {
        JsonReader reader = new JsonReader();
        ArrayList<DataEntry> userList = reader.readJsonFile(filePath);
        for (DataEntry user : userList) {
            if (user.get_id().equals(id)) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @Autowired
    private PDFManager pdfManager;

    @Autowired
    private RequestCounterService requestCounterService;

    @Autowired
    private DataHandling dataHandling;


    @PostMapping(path = "/generate-pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generatePDF(@RequestBody Map<String, String> requestData) {
        String mscode = requestData.get("mscode");
        try {
            // Obtener los datos completos del mscode.
            DataEntry data = dataHandling.findDataByMscode(mscode);
            if (data == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró información para el mscode: " + mscode);
            }

            // Generar PDF con la información completa.
            pdfManager.createPdf(data);
            requestCounterService.countRequest(mscode);
            return ResponseEntity.ok("PDF generado correctamente para mscode: " + mscode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el PDF");
        }
    }

    @PostMapping(path = "/export-csv", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportCsv() {
        List<DataEntry> dataEntries = dataHandling.getAllDataEntries(); // Asumimos que este método existe y obtiene todos los datos.
        Path path = Paths.get("./exports/data.csv");

        // Crear directorio si no existe
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path);
                 CSVWriter csvWriter = new CSVWriter(writer)) {
                // Escribir cabecera
                String[] header = {"ID", "Mscode", "Year", "Est. Code", "Estimate", "SE", "Lower CIB", "Upper CIB", "Flag"};
                csvWriter.writeNext(header);

                // Escribir datos
                for (DataEntry entry : dataEntries) {
                    String[] data = {
                            entry.get_id().toString(), entry.getMscode(), entry.getYear(),
                            entry.getEstCode(), String.valueOf(entry.getEstimate()), String.valueOf(entry.getSe()),
                            String.valueOf(entry.getLowerCIB()), String.valueOf(entry.getUpperCIB()), entry.getFlag()
                    };
                    csvWriter.writeNext(data);
                }
            }
            return "CSV generado exitosamente en: " + path.toString();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el CSV", e);
        }
    }











}
