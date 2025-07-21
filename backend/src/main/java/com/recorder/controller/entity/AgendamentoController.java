package com.recorder.controller.entity;

import com.recorder.dto.AgendamentoDTO;
import com.recorder.service.AgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos2")
public class AgendamentoController {

	private final AgendamentoService agendamentoService;

	public AgendamentoController(AgendamentoService agendamentoService) {
		this.agendamentoService = agendamentoService;
	}

	@PostMapping("/criar2")
	public ResponseEntity<?> criarAgendamento(
			@RequestBody AgendamentoDTO agendamentoDTO,
			Authentication authentication) {

		try {
			// Verifica se o usuário está autenticado
			if (authentication == null || !authentication.isAuthenticated()) {
				return ResponseEntity.status(401).body("Usuário não autenticado");
			}

			// Obtém o email do usuário autenticado
			String emailUsuario = authentication.getName();

			// Processa o agendamento
			Agendamento agendamento = agendamentoService.criarAgendamento(agendamentoDTO, emailUsuario);
			return ResponseEntity.ok(agendamento);

		} catch (Exception exception) {
			return ResponseEntity.internalServerError()
					.body("{\"mensagem\":\"" + exception.getMessage() + "\"}");
		}
	}
}