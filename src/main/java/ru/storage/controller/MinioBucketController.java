package ru.storage.controller;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.storage.exception.MinioInteractionException;

@Controller
@RequiredArgsConstructor
public class MinioBucketController {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "cloud-storage";

    @PostConstruct
    public void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }
        } catch (Exception e) {
            throw new MinioInteractionException("Error during checking bucket existence or bucket creating",e);
        }
    }
}
