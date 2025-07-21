package com.recorder.controller.entity;

import com.recorder.service.SupabaseStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final SupabaseStorageService storageService;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "video/mp4",
            "video/quicktime",
            "video/x-msvideo");

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    public StorageController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_PROFISSIONAL', 'ROLE_ADMIN')")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new BasicResponse("error", "O arquivo não pode estar vazio"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body(
                    new BasicResponse("error", "Tamanho do arquivo excede o limite de 20MB"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body(
                    new BasicResponse("error",
                            "Tipo de arquivo não permitido. Tipos aceitos: " +
                                    String.join(", ", ALLOWED_CONTENT_TYPES)));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : getDefaultExtension(contentType);

            String filename = UUID.randomUUID() + fileExtension;
            String filePath = folder != null ? folder + "/" + filename : filename;

            String fileUrl = storageService.uploadFile(filePath, file.getBytes(), contentType);

            return ResponseEntity.ok(
                    new UploadResponse(
                            fileUrl,
                            filename,
                            contentType,
                            file.getSize(),
                            "success"));

        } catch (IOException exception) {
            return ResponseEntity.internalServerError().body(
                    new BasicResponse("error", "Falha ao fazer upload do arquivo: " + exception.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_PROFISSIONAL', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            storageService.deleteFile(filePath);
            return ResponseEntity.ok(
                    new BasicResponse("success", "Arquivo removido com sucesso"));
        } catch (IOException exception) {
            return ResponseEntity.internalServerError().body(
                    new BasicResponse("error", "Falha ao remover arquivo: " + exception.getMessage()));
        }
    }

    private String getDefaultExtension(String contentType) {
        if (contentType.equals("image/jpeg")) {
            return ".jpg";
        } else if (contentType.equals("image/png")) {
            return ".png";
        } else if (contentType.equals("video/mp4")) {
            return ".mp4";
        } else {
            return ".bin";
        }
    }

    private static class UploadResponse {
        private final String url;
        private final String filename;
        private final String contentType;
        private final long size;
        private final String status;

        public UploadResponse(String url, String filename, String contentType, long size, String status) {
            this.url = url;
            this.filename = filename;
            this.contentType = contentType;
            this.size = size;
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public String getFilename() {
            return filename;
        }

        public String getContentType() {
            return contentType;
        }

        public long getSize() {
            return size;
        }

        public String getStatus() {
            return status;
        }
    }

    private static class BasicResponse {
        private final String status;
        private final String message;

        public BasicResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}