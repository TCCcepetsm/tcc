package com.recorder.service;

import com.recorder.controller.entity.Agendamento;
import com.recorder.controller.entity.Usuario;
import com.recorder.controller.entity.enuns.StatusAgendamento;
import com.recorder.dto.AgendamentoDTO;
import com.recorder.repository.AgendamentoRepository;
import com.recorder.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Agendamento criarAgendamento(AgendamentoDTO dadosAgendamento, String emailUsuario) {
        try {
            // Validação dos dados de entrada
            if (dadosAgendamento == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Os dados do agendamento não podem ser nulos");
            }

            // Busca o usuário no banco de dados
            Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Nenhum usuário encontrado com o email fornecido: " + emailUsuario));

            // Validação dos campos obrigatórios
            if (dadosAgendamento.getNome() == null || dadosAgendamento.getNome().trim().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O nome do solicitante é obrigatório");
            }

            if (dadosAgendamento.getData() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A data do agendamento é obrigatória");
            }

            if (dadosAgendamento.getHorario() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O horário do agendamento é obrigatório");
            }

            // Criação do novo agendamento
            Agendamento novoAgendamento = new Agendamento();
            novoAgendamento.setUsuario(usuario);
            novoAgendamento.setNome(dadosAgendamento.getNome().trim());
            novoAgendamento.setEmail(dadosAgendamento.getEmail().trim());
            novoAgendamento.setTelefone(dadosAgendamento.getTelefone());
            novoAgendamento.setPlano(dadosAgendamento.getPlano());
            novoAgendamento.setData(dadosAgendamento.getData());
            novoAgendamento.setHorario(dadosAgendamento.getHorario());
            novoAgendamento.setEsporte(dadosAgendamento.getEsporte());
            novoAgendamento.setLocal(dadosAgendamento.getLocal());
            novoAgendamento.setLatitude(dadosAgendamento.getLatitude());
            novoAgendamento.setLongitude(dadosAgendamento.getLongitude());
            novoAgendamento.setStatus(StatusAgendamento.PENDENTE);

            // Persistência no banco de dados
            return agendamentoRepository.save(novoAgendamento);

        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ocorreu um erro inesperado ao processar o agendamento: " + exception.getMessage());
        }
    }
}