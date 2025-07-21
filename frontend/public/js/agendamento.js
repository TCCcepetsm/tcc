// Constantes globais
const API_URL = "http://localhost:8080/api";
const BOOKING_ENDPOINT = "/agendamentos2/criar2";

document.addEventListener("DOMContentLoaded", function () {
  // Elementos do DOM
  const bookingForm = document.getElementById("bookingForm");
  const responseDiv = document.getElementById("response");
  const telefoneInput = document.getElementById("telefone");

  // Verificação de autenticação
  const token = localStorage.getItem("jwtToken");
  const userData = JSON.parse(localStorage.getItem("userData"));

  if (!token || !userData) {
    window.location.href = "login.html";
    return;
  }

  // Preencher automaticamente nome e email se existirem
  const nomeInput = document.getElementById("nome");
  const emailInput = document.getElementById("email");

  if (userData.nome && !nomeInput.value) {
    nomeInput.value = userData.nome;
  }

  if (userData.email && !emailInput.value) {
    emailInput.value = userData.email;
  }

  // Configuração do mapa - Coordenadas iniciais no DF (Praça dos Três Poderes)
  let map;
  let marker;

  function initializeMap() {
    // Coordenadas da Praça dos Três Poderes, Brasília
    const dfCoordinates = [-15.7998, -47.8645];

    // Criar o mapa
    map = L.map('map').setView(dfCoordinates, 13);

    // Adicionar camada do OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    // Adicionar evento de clique no mapa
    map.on('click', function (event) {
      const clickedCoordinates = event.latlng;
      const latitude = clickedCoordinates.lat;
      const longitude = clickedCoordinates.lng;

      // Remover marcador anterior se existir
      if (marker) {
        map.removeLayer(marker);
      }

      // Adicionar novo marcador
      marker = L.marker([latitude, longitude]).addTo(map);

      // Atualizar campos do formulário
      document.getElementById('latitude').value = latitude;
      document.getElementById('longitude').value = longitude;

      // Buscar endereço via API Nominatim
      fetch(`https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json`)
        .then(response => response.json())
        .then(data => {
          document.getElementById('local').value = data.display_name || "Local selecionado";
          document.getElementById('endereco_completo').value = data.display_name || "";
        })
        .catch(error => {
          console.error("Erro ao buscar endereço:", error);
          document.getElementById('local').value = "Local selecionado (endereço não disponível)";
        });
    });

    // Garantir que o mapa seja redimensionado corretamente
    setTimeout(function () {
      map.invalidateSize();
    }, 100);
  }

  // Inicializar o mapa quando o DOM estiver pronto
  initializeMap();

  // Corrigir problema de redimensionamento da janela
  window.addEventListener('resize', function () {
    if (map) {
      setTimeout(function () {
        map.invalidateSize();
      }, 100);
    }
  });

  // Máscara para telefone
  telefoneInput.addEventListener('input', function (event) {
    let phoneNumber = event.target.value.replace(/\D/g, '');
    if (phoneNumber.length > 11) {
      phoneNumber = phoneNumber.substring(0, 11);
    }

    // Formatar como (XX) XXXXX-XXXX
    if (phoneNumber.length > 2) {
      phoneNumber = `(${phoneNumber.substring(0, 2)}) ${phoneNumber.substring(2)}`;
    }
    if (phoneNumber.length > 10) {
      phoneNumber = `${phoneNumber.substring(0, 10)}-${phoneNumber.substring(10)}`;
    }

    event.target.value = phoneNumber;
  });

  async function sendBookingToAPI(formData) {
    try {
      const response = await fetch('http://localhost:8080/api/agendamentos2/criar2', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        },
        body: JSON.stringify(formData)
      });

      // Verifica se a resposta é JSON válido
      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        const text = await response.text();
        throw new Error(text || 'Resposta inválida do servidor');
      }

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Erro no servidor');
      }

      return data;

    } catch (error) {
      console.error('Erro na requisição:', error);
      throw error;
    }
  }
  // Função para validar os dados do formulário
  function validateBookingForm(formData) {
    const validationErrors = [];

    if (!formData.nome || formData.nome.trim().length < 3) {
      validationErrors.push("O nome deve ter pelo menos 3 caracteres");
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email || !emailPattern.test(formData.email)) {
      validationErrors.push("Por favor, insira um e-mail válido");
    }

    const phoneDigits = formData.telefone.replace(/\D/g, '');
    if (phoneDigits.length < 10 || phoneDigits.length > 11) {
      validationErrors.push("O telefone deve conter DDD + número (10 ou 11 dígitos)");
    }

    if (!formData.dataJogo) {
      validationErrors.push("Selecione uma data válida");
    }

    if (!formData.horario) {
      validationErrors.push("Selecione um horário válido");
    }

    if (!formData.latitude || !formData.longitude) {
      validationErrors.push("Selecione um local no mapa");
    }

    if (validationErrors.length > 0) {
      throw new Error(validationErrors.join(" • "));
    }
  }

  // Função para mostrar mensagens de resposta
  function displayResponseMessage(message, isSuccess) {
    responseDiv.style.display = "block";
    responseDiv.innerHTML = `<div class="${isSuccess ? 'success' : 'error'}-message">${message}</div>`;
    responseDiv.className = isSuccess ? "success" : "error";

    // Redesenhar o mapa após mostrar mensagens
    if (map) {
      setTimeout(function () {
        map.invalidateSize();
      }, 300);
    }
  }

  // Evento de envio do formulário
  bookingForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    // Mostrar estado de carregamento
    responseDiv.innerHTML = `
            <div class="loading-message">
                <div class="loading-spinner"></div>
                <p>Enviando agendamento...</p>
            </div>
        `;
    responseDiv.className = "loading";
    responseDiv.style.display = "block";

    // Coletar dados do formulário
    const formData = {
      nome: document.getElementById("nome").value.trim(),
      email: document.getElementById("email").value.trim(),
      telefone: document.getElementById("telefone").value,
      plano: document.getElementById("plano").value,
      dataJogo: document.getElementById("data").value,
      horario: document.getElementById("horario").value,
      esporte: document.getElementById("esporte").value,
      local: document.getElementById("local").value,
      latitude: document.getElementById("latitude").value,
      longitude: document.getElementById("longitude").value,
      endereco_completo: document.getElementById("endereco_completo").value,
      usuarioId: userData.id
    };

    try {
      // Validar dados
      validateBookingForm(formData);

      // Enviar para a API
      const bookingResult = await sendBookingToAPI(formData);

      // Formatar data para exibição
      const formattedDate = new Date(formData.dataJogo).toLocaleDateString('pt-BR');

      // Mensagem de sucesso
      const successMessage = `
                <div class="success-message">
                    <h3>Agendamento confirmado!</h3>
                    <p>Olá <strong>${formData.nome}</strong>, seu agendamento para <strong>${formData.esporte}</strong> foi realizado com sucesso!</p>
                    <div class="booking-details">
                        <p><strong>Data:</strong> ${formattedDate} às ${formData.horario}</p>
                        <p><strong>Local:</strong> ${formData.local}</p>
                        <p><strong>Plano:</strong> ${formData.plano}</p>
                    </div>
                    <p>Um e-mail de confirmação foi enviado para: <strong>${formData.email}</strong></p>
                </div>
            `;

      // Exibir mensagem de sucesso
      displayResponseMessage(successMessage, true);

      // Limpar formulário e marcador do mapa
      bookingForm.reset();
      if (marker) {
        map.removeLayer(marker);
        marker = null;
      }

      // Redirecionar após 5 segundos
      setTimeout(function () {
        window.location.href = "inicial.html";
      }, 5000);

    } catch (error) {
      console.error("Erro no agendamento:", error);

      // Mensagem de erro
      const errorMessage = `
                <div class="error-message">
                    <h3>Erro no agendamento</h3>
                    <p>${error.message}</p>
                    <p>Por favor, corrija os dados e tente novamente.</p>
                </div>
            `;

      displayResponseMessage(errorMessage, false);
    }
  });
});