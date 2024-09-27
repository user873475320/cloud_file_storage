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

import static ru.storage.util.UserUtil.getUserDetails;
import static ru.storage.util.UserUtil.getUserRootFolderName;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final MinioService minioService;

    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false) String query,
                         Model model, Principal principal) {
        Pair<List<FileDTO>, List<FolderDTO>> pair = minioService.findFilesAndFoldersByQuery(getUserRootFolderName(getUserDetails(principal)), query);

        model.addAttribute("username", getUserDetails(principal).getUsername());
        model.addAttribute("files", pair.getFirst());
        model.addAttribute("folders", pair.getSecond());

        return "search_page";
    }
}
