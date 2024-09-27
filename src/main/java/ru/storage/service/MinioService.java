package ru.storage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.storage.dto.FileDTO;
import ru.storage.dto.FolderDTO;
import ru.storage.exception.MinioInteractionException;
import ru.storage.exception.MissingQueryParameterException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.storage.util.MinioUtils.*;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    private static final String BUCKET_NAME = "cloud-storage";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Pair<List<FileDTO>, List<FolderDTO>> findFilesAndFoldersInFolder(String userRootFolderName, String pathToFolder) {
        List<FileDTO> files = new ArrayList<>();
        List<FolderDTO> folders = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .prefix(Paths.get(userRootFolderName, pathToFolder).toString().replace("\\", "/") + "/") // e.g., "user-1-files/pathToFolder"
                            .recursive(false)   // Set recursive to false to only list direct children
                            .build());

            for (Result<Item> result : results) {
                Item item = result.get();
                String objectName = item.objectName();

                if (objectName.endsWith("/")) {
                    folders.add(FolderDTO.builder()
                            .name(getLastPartOfPath(objectName))
                            .fullPath(Paths.get(userRootFolderName).relativize(Paths.get(objectName)).toString().replace("\\", "/"))
                            .build()
                    );
                } else {
                    files.add(FileDTO.builder()
                            .fileName(getLastPartOfPath(objectName))
                                    .icon(determineIcon(objectName))
                                    .lastModifiedDate(DATE_FORMAT.format(Date.from(item.lastModified().toInstant())))
                                    .size(humanReadableByteCount(item.size()))
                            .build());
                }
            }
        } catch (Exception e) {
            throw new MinioInteractionException("Error during file find", e);
        }

        folders.sort(Comparator.comparing(FolderDTO::getName));
        files.sort(Comparator.comparing(FileDTO::getFileName));

        return Pair.of(files, folders);
    }

    public Pair<List<FileDTO>, List<FolderDTO>> findFilesAndFoldersByQuery(String userRootFolderName, String query) {
        if (query == null) {
            throw new MissingQueryParameterException("Error: search query is required");
        }

        List<FileDTO> files = new ArrayList<>();
        List<FolderDTO> folders = new ArrayList<>();
        userRootFolderName = Paths.get(userRootFolderName).toString();

        try {
            // Use listObjects to get all objects in the bucket and filter based on the search term
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(userRootFolderName.replace("\\", "/") + "/")
                    .recursive(true) // Search within subfolders too
                    .build());

            Set<String> folderSet = new HashSet<>(); // Keep track of detected folders

            for (Result<Item> result : items) {
                Item item = result.get();
                String objectName = item.objectName();
                String[] pathParts = objectName.split("/");

                // Extract the last part of the object name (file or folder)
                String lastPartOfPath = getLastPartOfPath(objectName);

                Path pathObjectName = Paths.get(objectName);
                if (lastPartOfPath.contains(query)) {
                    if (objectName.endsWith("/")) { // Indicates a folder
                        // Only add the folder if it hasn't been added yet
                        if (folderSet.add(objectName)) {
                            folders.add(FolderDTO.builder()
                                    .name(lastPartOfPath)
                                    .fullPath(Paths.get(userRootFolderName).relativize(pathObjectName).toString().replace("\\", "/"))
                                    .build());
                        }
                    } else {
                        // This is a file
                        files.add(FileDTO.builder()
                                .fileName(lastPartOfPath)
                                .icon(determineIcon(objectName))
                                .lastModifiedDate(DATE_FORMAT.format(Date.from(item.lastModified().toInstant())))
                                .size(humanReadableByteCount(item.size()))
                                .pathToFile(pathObjectName.subpath(1, pathParts.length - 1).toString().replace("\\", "/"))
                                .build());
                    }
                }

                // Detect folder(if query is a folder) and add parent folders of the file objects
                if (pathParts.length > 1) {
                    for (int i = 1; i < pathParts.length - 1; i++) {
                        String potentialFolder = Paths.get(pathParts[i]).toString();
                        String potentialQueryFolder = Paths.get(query).toString();
                        if (potentialFolder.contains(potentialQueryFolder) && folderSet.add(potentialFolder)) {
                            folders.add(FolderDTO.builder()
                                    .name(pathParts[i])
                                    .fullPath(pathObjectName.subpath(1, i+1).toString())
                                    .build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MinioInteractionException("Error during file search", e);
        }

        folders.sort(Comparator.comparing(FolderDTO::getName));
        files.sort(Comparator.comparing(FileDTO::getFileName));

        return Pair.of(files, folders);
    }

    public void uploadFileToMinio(MultipartFile file, String userRootFolderName, String currentPath) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(Paths.get(userRootFolderName, currentPath, file.getOriginalFilename()).toString().replace("\\", "/"))
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                        .build()
            );
        } catch (Exception e) {
            throw new MinioInteractionException("Error during file upload", e);
        }
    }
}
