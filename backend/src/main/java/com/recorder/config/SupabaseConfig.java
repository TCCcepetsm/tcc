package com.recorder.config;

import com.recorder.service.SupabaseStorageService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public SupabaseStorageService supabaseStorageService(OkHttpClient okHttpClient) {
        return new SupabaseStorageService(
                okHttpClient,
                supabaseUrl,
                supabaseKey,
                bucketName);
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}