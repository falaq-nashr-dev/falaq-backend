package uz.app.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class FileController {

    @GetMapping("/photos/{filename:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String filename) {
        try {
            Path file = Paths.get("src/main/files/photos/" + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFiles() {
        File directory = new File("src/main/files/photos/");
        Map<String, Object> response = new HashMap<>();

        if (!directory.exists() || !directory.isDirectory()) {
            response.put("error", "Directory not found");
            return ResponseEntity.badRequest().body(response);
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".webp"));
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                fileNames.add(file.getName());
            }
        }

        response.put("count", fileNames.size());
        response.put("files", fileNames);

        return ResponseEntity.ok(response);
    }
}
