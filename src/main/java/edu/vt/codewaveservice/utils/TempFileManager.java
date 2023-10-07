package edu.vt.codewaveservice.utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TempFileManager {

    private List<File> tempFiles;

    public TempFileManager() {
        tempFiles = new ArrayList<>();
    }

    public File createTempFile(String path) {
        File tempFile = new File(path);
        tempFiles.add(tempFile);
        return tempFile;
    }

    public void deleteAllTempFiles() {
        for (File tempFile : tempFiles) {
            if (!tempFile.delete()) {
                System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
        tempFiles.clear();  // Clear the list after deleting all temp files
    }
}

