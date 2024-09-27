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

import java.security.Principal;
import java.util.List;

import static ru.storage.util.UserUtil.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MinioService minioService;

    @GetMapping("/")
    public String showIndexPage() {
        return "auth/index_page";
    }

    @GetMapping("/home")
    public String showHomePage(@RequestParam(value = "path", required = false) String currentPath, Model model, Principal principal) {
        currentPath = (currentPath == null) ? "" : currentPath;
        Pair<List<FileDTO>, List<FolderDTO>> pair = minioService.findFilesAndFoldersInFolder(getUserRootFolderName(getUserDetails(principal)), currentPath);

        model.addAttribute("username", getUserDetails(principal).getUsername());
        model.addAttribute("files", pair.getFirst());
        model.addAttribute("folders", pair.getSecond());
        model.addAttribute("breadcrumbFolders", getBreadcrumbsFolders(currentPath));
        model.addAttribute("currentPath", currentPath);

        return "home_page";
    }
}
