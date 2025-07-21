package com.recorder.service;

import com.recorder.controller.entity.Galeria;
import com.recorder.controller.entity.enuns.TipoMidia;
import com.recorder.repository.GaleriaRepository;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GaleriaService {

    private final OkHttpClient httpClient;
    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucketName;

    @Autowired
    private GaleriaRepository galeriaRepository;

    @Autowired
    public GaleriaService(OkHttpClient httpClient,
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.key}") String supabaseKey,
            @Value("${supabase.bucket}") String bucketName) {
        this.httpClient = httpClient;
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
        this.bucketName = bucketName;
    }

    public Galeria uploadMidia(MultipartFile file, Integer profissionalId, String tipoEvento, LocalDateTime dataEvento)
            throws IOException {
        // Gerar nome único para o arquivo
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Determinar o tipo de mídia
        TipoMidia tipo = file.getContentType().startsWith("image/") ? TipoMidia.FOTO : TipoMidia.VIDEO;

        // Fazer upload para o Supabase
        String fileUrl = uploadToSupabase(fileName, file.getBytes(), file.getContentType());

        // Salvar no banco de dados
        Galeria galeria = new Galeria();
        galeria.setMidiaUrl(fileUrl);
        galeria.setTipo(tipo);
        galeria.setProfissionalId(profissionalId);
        galeria.setDataPostagem(LocalDateTime.now());
        galeria.setTipoEvento(tipoEvento);
        galeria.setDataEvento(dataEvento);

        return galeriaRepository.save(galeria);
    }

    private String uploadToSupabase(String fileName, byte[] fileData, String contentType) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(fileData, MediaType.parse(contentType)))
                .build();

        Request request = new Request.Builder()
                .url(supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName)
                .header("Authorization", "Bearer " + supabaseKey)
                .header("apikey", supabaseKey)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.body().string());
            }

            // Retorna a URL pública do arquivo
            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        }
    }

    public List<Galeria> listarPorProfissional(Integer profissionalId) {
        return galeriaRepository.findByProfissionalId(profissionalId);
    }

    public List<Galeria> listarPorTipoEvento(String tipoEvento) {
        return galeriaRepository.findByTipoEvento(tipoEvento);
    }
}