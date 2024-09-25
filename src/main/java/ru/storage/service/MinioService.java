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

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static ru.storage.util.MinioUtils.*;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    private static final String BUCKET_NAME = "cloud-storage";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Pair<List<FileDTO>, List<FolderDTO>> findFilesInFolder(String userRootFolderName, String pathToFolder) {
        List<FileDTO> files = new ArrayList<>();
        List<FolderDTO> directories = new ArrayList<>();

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
                    // This is a folder (MinIO treats folders as prefixes)
                    FolderDTO folderDTO = new FolderDTO();
                    folderDTO.setName(getLastPartOfPath(objectName));
                    folderDTO.setFullPath(Paths.get(userRootFolderName).relativize(Paths.get(objectName)).toString().replace("\\", "/"));
                    directories.add(folderDTO);
                } else {
                    // This is a file
                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setFileName(getLastPartOfPath(objectName));
                    fileDTO.setIcon(determineIcon(objectName));
                    fileDTO.setLastModifiedDate(DATE_FORMAT.format(Date.from(item.lastModified().toInstant())));
                    fileDTO.setSize(humanReadableByteCount(item.size()));
                    files.add(fileDTO);
                }
            }
        } catch (Exception e) {
            throw new MinioInteractionException("Error during file find", e);
        }

        directories.sort(Comparator.comparing(FolderDTO::getName));
        files.sort(Comparator.comparing(FileDTO::getFileName));

        return Pair.of(files, directories);
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
