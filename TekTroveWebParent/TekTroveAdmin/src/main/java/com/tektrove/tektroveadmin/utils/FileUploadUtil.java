package com.tektrove.tektroveadmin.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUploadUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);

        multipartFile.transferTo(filePath);
    }

    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);
        if (Files.exists(dirPath)) {
            try (Stream<Path> files = Files.list(dirPath)) {
                files.filter(file -> !Files.isDirectory(file))
                        .forEach(FileUploadUtil::deleteFile);
            } catch (IOException e) {
                throw new RuntimeException("Error cleaning directory: " + dir, e);
            }
        }
    }

    private static void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + file, e);
        }
    }

    public static void removeDir(String dir) {
        cleanDir(dir);

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
