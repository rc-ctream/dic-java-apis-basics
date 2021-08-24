package com.coneva.dic;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class ExtractAuthorNames {

    static File authors = new File("authors.txt");

    public static void main(String[] args) throws IOException {

        var profileCounter = new AtomicInteger();
        var path = Paths.get("src", "main", "resources", "profiles").toAbsolutePath();
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .filter(Files::isRegularFile)
                .map(ExtractAuthorNames::extractAuthorNameAndBirthday)
                .forEach(profile -> writeToFile(profile, profileCounter.incrementAndGet()));

    }

    @SneakyThrows
    private static void writeToFile(String profile, int incrementAndGet) {
        var identity = incrementAndGet + " " + profile;
        Files.writeString(authors.toPath(), identity + System.lineSeparator(), StandardCharsets.UTF_8, CREATE, APPEND);
    }

    @SneakyThrows
    private static String extractAuthorNameAndBirthday(Path path) {
        return Files.readAllLines(path).stream()
                .filter(line -> line.contains("Name") || line.contains("Date of birth"))
                .reduce((s, s2) -> {
                    var name = s.split("Name:")[1];
                    var birthday = s2.split("Date of birth:")[1];
                    return String.join(",", name, birthday);
                }).get();
    }


}
