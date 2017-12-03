package filecatalog.client.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    private final String root = "./client";
    private Path workingDir = Paths.get(root);
    private static FileManager instance;

    public static FileManager getInstance(){
        if(instance == null){
            instance = new FileManager();
        }
        return instance;
    }

    private FileManager(){
    }

    public void writeFile(String filename, byte[] bytes) throws IOException {
        Path file = workingDir.resolve(Paths.get(filename));
        Files.write(file,bytes);
    }

    public boolean fileExists(String filename) {
        Path file = workingDir.resolve(Paths.get(filename));
        return Files.exists(file);
    }

    public byte[] readFile(String filename) throws IOException {
        Path file = workingDir.resolve(Paths.get(filename));
        if (!Files.exists(file)) {
            throw new FileNotFoundException("Could not find " + file);
        }
        return Files.readAllBytes(file);
    }
}
