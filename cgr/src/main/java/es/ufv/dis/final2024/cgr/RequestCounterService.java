package es.ufv.dis.final2024.cgr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class RequestCounterService {
    private static final String FILE_PATH = "./peticiones/mscodeCount.json";
    private Map<String, Integer> mscodeCounts = new HashMap<>();

    public synchronized void countRequest(String mscode) {
        loadCounts();
        mscodeCounts.put(mscode, mscodeCounts.getOrDefault(mscode, 0) + 1);
        saveCounts();
    }

    private void loadCounts() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                String json = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
                mscodeCounts = new Gson().fromJson(json, new TypeToken<Map<String, Integer>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCounts() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(mscodeCounts);
            Files.write(Paths.get(FILE_PATH), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
