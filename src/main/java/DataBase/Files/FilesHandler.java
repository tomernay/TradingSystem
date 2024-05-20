package src.main.java.DataBase.Files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FilesHandler {

    /**
     * Write a JSONObject to a file.
     *
     * @param jsonObject The JSONObject to write.
     * @param file       The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeJSONObjectToFile(JsonNode jsonObject, File file) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(jsonObject.toString());
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * Read a JSONObject from a file.
     *
     * @param file The file to read from.
     * @return The JSONObject read from the file.
     * @throws IOException If an I/O error occurs.
     */
    public static JsonNode readJSONObjectFromFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(file);
        deleteFile(file);
        return jsonObject;
    }

    /**
     * Delete a file.
     *
     * @param file The file to delete.
     * @return True if the file was successfully deleted, false otherwise.
     */
    public static boolean deleteFile(File file) {
        return file.delete();
    }
}
