package ru.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private String name;
    private String fullPath;

    public String getEncodedPath() {
        return URLEncoder.encode(fullPath, StandardCharsets.UTF_8);
    }
}
