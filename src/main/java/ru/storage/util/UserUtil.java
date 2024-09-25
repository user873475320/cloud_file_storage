package ru.storage.util;

import org.springframework.security.core.Authentication;
import ru.storage.config.security.CustomUserDetails;
import ru.storage.dto.FolderDTO;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UserUtil {

    private UserUtil() {}

    public static List<FolderDTO> getBreadcrumbsFolders(String currentPath) {
        if (currentPath == null) {
            return new ArrayList<>();
        }

        String decodedPath = URLDecoder.decode(currentPath, StandardCharsets.UTF_8);
        Path fullPath = Paths.get(decodedPath);

        return IntStream.range(0, fullPath.getNameCount())
                .mapToObj(i -> {
                    Path partialPath = fullPath.subpath(0, i + 1);
                    return new FolderDTO(partialPath.getFileName().toString(), partialPath.toString().replace("\\", "/"));
                })
                .toList();
    }

    public static String getUserRootFolderName(CustomUserDetails customUserDetails) {
        return "user-" + customUserDetails.getId() + "-files";
    }

    public static CustomUserDetails getUserDetails(Principal principal) {
        return (CustomUserDetails) ((Authentication) principal).getPrincipal();

    }
}
