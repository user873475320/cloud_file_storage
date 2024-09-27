package ru.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private String fileName;
    private Icons icon;
    private String lastModifiedDate;
    private String size;
    private String pathToFile;

    public String getEncodedPathToFile() {
        return URLEncoder.encode(pathToFile, StandardCharsets.UTF_8);
    }
}
