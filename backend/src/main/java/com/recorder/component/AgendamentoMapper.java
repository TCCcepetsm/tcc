package com.recorder.component;

import com.recorder.controller.entity.Agendamento;
import com.recorder.controller.entity.Usuario;
import com.recorder.controller.entity.enuns.StatusAgendamento;
import com.recorder.dto.AgendamentoDTO;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoMapper {

    public Agendamento toEntity(AgendamentoDTO dto, Usuario usuario) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setNome(dto.getNome()); // Usando o DTO recebido como parâmetro
        agendamento.setEmail(dto.getEmail());
        agendamento.setTelefone(dto.getTelefone());
        agendamento.setPlano(dto.getPlano());
        agendamento.setData(dto.getData());
        agendamento.setHorario(dto.getHorario());
        agendamento.setEsporte(dto.getEsporte());
        agendamento.setLocal(dto.getLocal());
        agendamento.setLatitude(dto.getLatitude());
        agendamento.setLongitude(dto.getLongitude());
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        return agendamento;
    }
}