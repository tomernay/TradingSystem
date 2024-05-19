package DataBase.Files;

import cn.hutool.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FilesHandler {

    /**
     * Write a JSONObject to a file.
     *
     * @param jsonObject The JSONObject to write.
     * @param file       The file to write to.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeJSONObjectToFile(JSONObject jsonObject, File file) throws IOException {
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
    public static JSONObject readJSONObjectFromFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        deleteFile(file);
        return new JSONObject(content);
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
