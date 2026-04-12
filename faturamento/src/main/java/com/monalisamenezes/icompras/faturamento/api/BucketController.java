package com.monalisamenezes.icompras.faturamento.api;

import com.monalisamenezes.icompras.faturamento.bucket.BucketFile;
import com.monalisamenezes.icompras.faturamento.bucket.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("/bucket")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService service;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
       try (InputStream inputStream = file.getInputStream()) {
           MediaType type = MediaType.parseMediaType(Objects.requireNonNull(file.getContentType()));
           service.upload(new BucketFile(file.getOriginalFilename(), inputStream, type, file.getSize()));
           return ResponseEntity.ok("Arquivo enviado com sucesso!");
       } catch (Exception e) {
       return ResponseEntity.status(500).body("Erro ao enviar o arquivo: " + e.getMessage());
       }
    }

    @GetMapping
    public ResponseEntity<String> getUrl(@RequestParam String filename) {
        try {
            String url = service.getUrl(filename);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao obter a URL: " + e.getMessage());
        }
    }
}
