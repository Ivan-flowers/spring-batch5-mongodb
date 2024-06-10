package com.bootlabs.springbatch5mongodb.domain.enums;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserGender {

    //@// @formatter:off,
    UNKNOWN(0),
    MALE(1),
    FEMALE(2);
    // @formatter:on

    private final int type;

    UserGender(int type) {
        this.type = type;
    }


    public static UserGender getType(@NotNull int type){
        return Arrays.stream(UserGender.values())
                .filter(gender -> gender.type == type )
                .findFirst()
                .orElse(UserGender.UNKNOWN);
    }
}