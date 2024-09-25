package ru.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.storage.dto.FileDTO;
import ru.storage.dto.FolderDTO;
import ru.storage.service.MinioService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import static ru.storage.util.UserUtil.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MinioService minioService;

    @GetMapping("/home")
    public String showHomePage(@RequestParam(value = "path", required = false) String currentPath, Model model, Principal principal) {
        String decodedCurrentPath =  (currentPath == null) ? "" : URLDecoder.decode(currentPath, StandardCharsets.UTF_8);
        Pair<List<FileDTO>, List<FolderDTO>> pair = minioService.findFilesInFolder(getUserRootFolderName(getUserDetails(principal)), decodedCurrentPath);

        model.addAttribute("username", getUserDetails(principal).getUsername());
        model.addAttribute("files", pair.getFirst());
        model.addAttribute("directories", pair.getSecond());
        model.addAttribute("breadcrumbFolders", getBreadcrumbsFolders(currentPath));
        model.addAttribute("currentPath", decodedCurrentPath);
        return "home_page";
    }
}
