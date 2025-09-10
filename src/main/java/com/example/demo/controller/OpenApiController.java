package com.example.demo.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class OpenApiController {

    @GetMapping("/api/openapi.yaml")
    public String getOpenApiYaml() throws IOException {
        Resource resource = new ClassPathResource("openapi/server.yaml");
        return new String(Files.readAllBytes(Paths.get(resource.getURI())));
    }
}