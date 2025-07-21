package com.recorder.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome não pode ter mais que 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Telefone deve conter apenas números (10 a 15 dígitos)")
    private String telefone;

    @Size(max = 50, message = "O plano não pode ter mais que 50 caracteres")
    private String plano;

    @NotNull(message = "A data é obrigatória")
    @FutureOrPresent(message = "A data deve ser atual ou futura")
    private LocalDate data;

    @NotNull(message = "O horário é obrigatório")
    private LocalTime horario;

    @NotBlank(message = "O esporte é obrigatório")
    @Size(max = 50, message = "O esporte não pode ter mais que 50 caracteres")
    private String esporte;

    @NotBlank(message = "O local é obrigatório")
    @Size(max = 200, message = "O local não pode ter mais que 200 caracteres")
    private String local;

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "A latitude mínima é -90.0")
    @DecimalMax(value = "90.0", message = "A latitude máxima é 90.0")
    private BigDecimal latitude;

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "A longitude mínima é -180.0")
    @DecimalMax(value = "180.0", message = "A longitude máxima é 180.0")
    private BigDecimal longitude;

    // Getters e Setters mantidos conforme original
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = plano;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getEsporte() {
        return esporte;
    }

    public void setEsporte(String esporte) {
        this.esporte = esporte;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}