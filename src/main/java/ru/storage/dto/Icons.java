package ru.storage.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Icons {
    IMAGE("image.svg"), MUSIC("music.svg"), VIDEO("video.svg"), FILE("file.svg"), FOLDER("folder.svg");

    private final String iconFileName;
}
