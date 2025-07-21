// Aguarda o carregamento completo do DOM
document.addEventListener("DOMContentLoaded", () => {
    // Exemplo de dados de agendamentos
    const bookings = [
        {
            id: 1,
            nome: "João Silva",
            email: "joao@mail.com",
            telefone: "(11) 99999-9999",
            plano: "Avançado",
            data: "2025-05-20",
            horario: "14:00",
            esporte: "Futebol",
            local: "Estádio XYZ",
            status: "Pendente"
        },
        {
            id: 2,
            nome: "Maria Souza",
            email: "maria@mail.com",
            telefone: "(21) 88888-8888",
            plano: "Intermediário",
            data: "2025-05-21",
            horario: "16:00",
            esporte: "Basquete",
            local: "Ginásio ABC",
            status: "Pendente"
        }
    ];

    const tbody = document.querySelector("#bookingsTable tbody");

    // Verifica se o tbody existe
    if (!tbody) {
        console.error("Elemento tbody não encontrado");
        return;
    }

    // Função para renderizar os agendamentos na tabela
    function renderBookings() {
        tbody.innerHTML = "";
        bookings.forEach((booking, index) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td data-label="Data">${booking.data}</td>
                <td data-label="Horário">${booking.horario}</td>
                <td data-label="Acessar">
                    <button class="btn btn-access" data-id="${booking.id}" data-index="${index}">Acessar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    // Renderiza os agendamentos inicialmente
    renderBookings();

    // Delegação de evento para tratar cliques no botão "Acessar"
    tbody.addEventListener("click", (event) => {
        const button = event.target.closest(".btn-access");
        
        if (button) {
            const index = button.getAttribute("data-index");
            const id = button.getAttribute("data-id");
            const booking = bookings[index];
            
            // Armazena o agendamento selecionado no sessionStorage para acesso na próxima página
            sessionStorage.setItem("selectedBooking", JSON.stringify(booking));
            
            // Redireciona para a página de detalhes com o ID como parâmetro
            window.location.href = `agendamentoDetalhes.html`;
        }
    });
});