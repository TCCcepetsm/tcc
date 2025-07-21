package com.recorder.controller.entity;

import com.recorder.controller.entity.enuns.TipoMidia;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "galeria")
public class Galeria {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false)
        private String midiaUrl;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TipoMidia tipo;

        @Column(name = "profissional_id", nullable = false)
        private Integer profissionalId;

        @Column(name = "data_postagem", nullable = false)
        private LocalDateTime dataPostagem;

        @Column(name = "tipo_evento")
        private String tipoEvento;

        @Column(name = "data_evento")
        private LocalDateTime dataEvento;
}