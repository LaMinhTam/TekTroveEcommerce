package com.tektrove.tektroveadmin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileUploadUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);

        multipartFile.transferTo(filePath);
    }

    public static void cleanDir(String dir) {
        try {
            Files.walk(Paths.get(dir))
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(FileUploadUtil::deleteFile);
        } catch (IOException e) {
            LOGGER.error("Error cleaning directory: {}", dir, e);
        }
    }

    private static void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            LOGGER.error("Failed to delete file: " + file, e);
        }
    }

    public static void removeDir(String dir) {
        cleanDir(dir);

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
            LOGGER.error("Error removing directory: {}", dir, e);
        }
    }
}
