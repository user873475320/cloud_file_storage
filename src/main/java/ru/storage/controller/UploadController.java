package ru.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.service.MinioService;

import java.security.Principal;
import java.util.List;

import static ru.storage.util.UserUtil.getUserDetails;
import static ru.storage.util.UserUtil.getUserRootFolderName;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MinioService minioService;

    @PostMapping("/file")
    public ResponseEntity<String> handleFileUpload(@RequestParam(value = "path", required = false) String currentPath, @RequestParam("files") MultipartFile[] files, Principal principal) {
        currentPath = (currentPath == null) ? "" : currentPath;
        for (MultipartFile file : files) {
            minioService.uploadFileToMinio(file, getUserRootFolderName(getUserDetails(principal)), currentPath);
        }
        return ResponseEntity.ok("Files uploaded successfully");
    }

    @PostMapping("/folder")
    public ResponseEntity<String> handleFolderUpload(@RequestParam(value = "path", required = false) String currentPath, @RequestParam("files") List<MultipartFile> files, Principal principal) {
        currentPath = (currentPath == null) ? "" : currentPath;
        for (MultipartFile file : files) {
            minioService.uploadFileToMinio(file, getUserRootFolderName(getUserDetails(principal)), currentPath);
        }
        return ResponseEntity.ok("Folder uploaded successfully!");
    }
}
