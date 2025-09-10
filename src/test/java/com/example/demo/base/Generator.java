package com.example.demo.base;

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import static net.datafaker.providers.base.Text.*;

public class Generator {
    private final static Faker faker = new Faker();

    public static String password() {
        var trc = Text.TextSymbolsBuilder.builder()
            .len(16)
            .with(EN_UPPERCASE, 1)
            .with(EN_LOWERCASE, 1)
            .with(DIGITS, 1)
            .with(DEFAULT_SPECIAL, 1)
            .build();

        return faker.text().text(trc);
    }

    public static String username() {
        return faker.internet().username();
    }

    public static String email() {
        return faker.internet().emailAddress();
    }
}
