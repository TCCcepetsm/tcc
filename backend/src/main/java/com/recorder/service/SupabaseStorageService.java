package com.recorder.service;

import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SupabaseStorageService {

        private final OkHttpClient httpClient;
        private final String supabaseUrl;
        private final String supabaseKey;
        private final String bucketName;

        public SupabaseStorageService(OkHttpClient httpClient,
                        String supabaseUrl,
                        String supabaseKey,
                        String bucketName) {
                this.httpClient = httpClient;
                this.supabaseUrl = supabaseUrl;
                this.supabaseKey = supabaseKey;
                this.bucketName = bucketName;
        }

        /**
         * Versão que aceita MultipartFile diretamente
         */
        public String uploadFile(MultipartFile file, String filePath) throws IOException {
                return uploadFile(filePath, file.getBytes(), file.getContentType());
        }

        /**
         * Versão que aceita bytes + contentType
         */
        public String uploadFile(String filePath, byte[] fileData, String contentType) throws IOException {
                RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", filePath,
                                                RequestBody.create(fileData, MediaType.parse(contentType)))
                                .build();

                Request request = new Request.Builder()
                                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath)
                                .header("Authorization", "Bearer " + supabaseKey)
                                .header("apikey", supabaseKey)
                                .post(requestBody)
                                .build();

                try (Response response = httpClient.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                                throw new IOException("Upload failed: " + response.body().string());
                        }
                        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + filePath;
                }
        }

        public void deleteFile(String filePath) throws IOException {
                Request request = new Request.Builder()
                                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath)
                                .header("Authorization", "Bearer " + supabaseKey)
                                .header("apikey", supabaseKey)
                                .delete()
                                .build();

                try (Response response = httpClient.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                                throw new IOException("Delete failed: " + response.body().string());
                        }
                }
        }
}