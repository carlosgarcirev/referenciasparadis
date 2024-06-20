package es.ufv.dis.final2024.cgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DataHandling {
    private JsonReader reader = new JsonReader();
    ArrayList<DataEntry> addUser (DataEntry newUser){
        ArrayList<DataEntry> userList = reader.readJsonFile("./src/main/resources/cp-national-datafile.json");
        // Verificar si el ID ya existe
        for (DataEntry user : userList) {
            if (user.get_id().equals(newUser.get_id())) {
                return null; // Si el ID ya existe, retornar null para indicarlo
            }
        }
        userList.add(newUser);
        reader.writeJsonFile("./src/main/resources/cp-national-datafile.json", userList);
        return userList;
    }
    DataEntry getUserInfo (String name){
        DataEntry foundUser = null;
        JsonReader reader = new JsonReader();

        ArrayList<DataEntry> usersList = reader.readJsonFile("./src/main/resources/cp-national-datafile.json");
        for (DataEntry user : usersList){
            if (user.getMscode().equalsIgnoreCase(name)){
                foundUser = user;
            }
        }
        return foundUser;
    }
    // Método para eliminar un usuario que coincida completamente
    boolean deleteUser(DataEntry targetUser) {
        JsonReader reader = new JsonReader();
        ArrayList<DataEntry> userList = reader.readJsonFile("./src/main/resources/cp-national-datafile.json");
        boolean isRemoved = userList.removeIf(user ->
                Objects.equals(user.get_id(), targetUser.get_id()) &&
                        Objects.equals(user.getMscode(), targetUser.getMscode()) &&
                        Objects.equals(user.getYear(), targetUser.getYear()) &&
                        Objects.equals(user.getEstCode(), targetUser.getEstCode()) &&
                        Objects.equals(user.getEstimate(), targetUser.getEstimate()) &&
                        Objects.equals(user.getSe(), targetUser.getSe()) &&
                        Objects.equals(user.getLowerCIB(), targetUser.getLowerCIB()) &&
                        Objects.equals(user.getUpperCIB(), targetUser.getUpperCIB()) &&
                        Objects.equals(user.getFlag(), targetUser.getFlag())
        );
        if (isRemoved) {
            reader.writeJsonFile("./src/main/resources/cp-national-datafile.json", userList);
        }
        return isRemoved;
    }
    // Método para actualizar los datos de un usuario por _id
    boolean updateUser(DataEntry updatedUser) {
        JsonReader reader = new JsonReader();
        ArrayList<DataEntry> userList = reader.readJsonFile("./src/main/resources/cp-national-datafile.json");
        boolean isUpdated = false;

        for (int i = 0; i < userList.size(); i++) {
            DataEntry user = userList.get(i);
            if (user.get_id().equals(updatedUser.get_id())) {
                // Actualiza los datos del usuario
                user.setMscode(updatedUser.getMscode());
                user.setYear(updatedUser.getYear());
                user.setEstCode(updatedUser.getEstCode());
                user.setEstimate(updatedUser.getEstimate());
                user.setSe(updatedUser.getSe());
                user.setLowerCIB(updatedUser.getLowerCIB());
                user.setUpperCIB(updatedUser.getUpperCIB());
                user.setFlag(updatedUser.getFlag());
                isUpdated = true;
                break;
            }
        }
        if (isUpdated) {
            reader.writeJsonFile("./src/main/resources/cp-national-datafile.json", userList);
        }
        return isUpdated;
    }

    @Autowired
    private JsonReader jsonReader; // Injecta tu JsonReader

    public DataEntry findDataByMscode(String mscode) {
        // Leer todos los datos
        ArrayList<DataEntry> userList = jsonReader.readJsonFile("./src/main/resources/cp-national-datafile.json");
        // Buscar por mscode
        for (DataEntry user : userList) {
            if (user.getMscode().equals(mscode)) {
                return user;  // Retorna la entrada si el mscode coincide
            }
        }
        return null; // Retorna null si no encuentra una entrada con ese mscode
    }
    public List<DataEntry> getAllDataEntries() {
        // Aquí, leemos directamente desde el archivo JSON que contiene todos los registros.
        // Asumimos que el archivo se encuentra en el path especificado.
        String filePath = "./src/main/resources/cp-national-datafile.json";
        return jsonReader.readJsonFile(filePath);
    }


}
