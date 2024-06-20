package es.ufv.dis.final2024.cgr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Component
public class JsonReader {
    public ArrayList<DataEntry> readJsonFile(String fichero) {
        try {
            Gson gson = new Gson(); //crea un objeto Gson
            //lee el fichero que le pasemos y lo carga en un reader
            Reader reader = Files.newBufferedReader(Paths.get(fichero));
            // convierte el array JSON a un arraylist de users
            ArrayList<DataEntry> users = new Gson().fromJson(reader, new TypeToken<ArrayList<DataEntry>>() {
            }.getType());
            users.forEach(System.out::println);// imprime los users
            reader.close();// close reader
            return users;

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>(); //si no ha leido nada, devuelve un array vacio
        }

    }

    public boolean writeJsonFile(String fichero, ArrayList<DataEntry> users) {
        try {
            // lee el fichero que le pasemos y lo abre en modo escritura
            Writer writer = Files.newBufferedWriter(Paths.get(fichero));
            // Crea un Gson con pretty printing para estructurar el JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Convierte el arrayList de users a un array JSON formateado, y lo escribe en el fichero
            writer.write(gson.toJson(users));
            writer.close(); // Cierra el escritor
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // Si ocurre una excepción, devuelve false
        }
    }

}