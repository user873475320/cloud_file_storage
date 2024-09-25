package ru.storage.util;

import ru.storage.dto.Icons;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MinioUtils {
    private MinioUtils() {}

    public static String getLastPartOfPath(String path) {
        Path filePath = Paths.get(path);
        return filePath.getFileName().toString();
    }

    public static Icons determineIcon(String fileName) {
        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".png") || lowerFileName.endsWith(".jpeg") || lowerFileName.endsWith(".webp") || lowerFileName.endsWith(".svg")) {
            return Icons.IMAGE;
        } else if (lowerFileName.endsWith(".mp3") || lowerFileName.endsWith(".wav")) {
            return Icons.MUSIC;
        } else if (lowerFileName.endsWith(".mp4") || lowerFileName.endsWith(".mkv")) {
            return Icons.VIDEO;
        } else {
            return Icons.FILE;
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
