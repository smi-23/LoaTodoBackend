package com.loatodo.loatodobackend.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER"),
    GUEST("GUEST");


    private final String key;
}
