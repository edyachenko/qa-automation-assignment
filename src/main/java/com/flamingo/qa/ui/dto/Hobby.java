package com.flamingo.qa.ui.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Hobby {
    SPORTS("Sports"),
    READING("Reading"),
    MUSIC("Music");

    private final String label;
}
