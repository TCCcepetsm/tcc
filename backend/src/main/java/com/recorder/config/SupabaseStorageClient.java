package com.recorder.config;

import okhttp3.*;
import java.io.IOException;

public class SupabaseStorageClient {
    private final OkHttpClient client;
    private final String supabaseUrl;
    private final String apiKey;
    private final String bucketName;

    public SupabaseStorageClient(String supabaseUrl, String apiKey, String bucketName) {
        this.client = new OkHttpClient();
        this.supabaseUrl = supabaseUrl;
        this.apiKey = apiKey;
        this.bucketName = bucketName;
    }

    public String uploadFile(String fileName, byte[] fileData, String contentType) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(fileData, MediaType.parse(contentType)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro no upload: " + response);
            }
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        }
    }
}