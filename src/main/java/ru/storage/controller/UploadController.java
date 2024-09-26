package ru.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.service.MinioService;

import java.security.Principal;
import java.util.List;

import static ru.storage.util.UserUtil.getUserDetails;
import static ru.storage.util.UserUtil.getUserRootFolderName;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFolderUpload(@RequestParam(value = "path", required = false) String currentPath, @RequestParam("files") List<MultipartFile> files, Principal principal) {
        currentPath = (currentPath == null) ? "" : currentPath;
        for (MultipartFile file : files) {
            minioService.uploadFileToMinio(file, getUserRootFolderName(getUserDetails(principal)), currentPath);
        }
        return ResponseEntity.ok("Files uploaded successfully!");
    }
}
