package com.recorder.repository;

import com.recorder.controller.entity.Galeria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GaleriaRepository extends JpaRepository<Galeria, Integer> {
    List<Galeria> findByProfissionalId(Integer profissionalId);

    List<Galeria> findByTipoEvento(String tipoEvento);
}