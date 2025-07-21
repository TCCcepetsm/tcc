document.addEventListener('DOMContentLoaded', function () {
    // Busca os dados da API
    fetch('http://localhost:3000/agendamento')
        .then(res => {
            if (!res.ok) {
                throw new Error('Erro ao carregar dados');
            }
            return res.json();
        })
        .then(dados => {
            // Formata a data para exibição
            const dataFormatada = new Date(dados.data).toLocaleDateString('pt-BR');

            // Preenche os dados na página
            document.getElementById('info-nome').textContent = dados.nome || 'Não informado';
            document.getElementById('info-email').textContent = dados.email || 'Não informado';
            document.getElementById('info-telefone').textContent = dados.telefone || 'Não informado';
            document.getElementById('info-plano').textContent = dados.plano || 'Não selecionado';
            document.getElementById('info-endereco').textContent = dados.endereco || 'Não informado';
            document.getElementById('info-data').textContent = dataFormatada;
            document.getElementById('info-horario').textContent = dados.horario || 'Não informado';

            // Atualiza o status se existir nos dados
            if (dados.status) {
                updateStatus(dados.status);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            document.getElementById('info-nome').textContent = 'Erro ao carregar dados';
        });

    // Configura o botão de confirmação
    document.getElementById('btn-confirmar').addEventListener('click', function () {
        // Aqui você pode adicionar uma chamada para atualizar o status no backend
        updateStatus('Confirmado');

        this.textContent = 'Agendamento Confirmado';
        this.disabled = true;
        document.getElementById('btn-recusar').disabled = true;

        alert('Agendamento confirmado com sucesso!');
    });

    // Configura o botão de recusa
    document.getElementById('btn-recusar').addEventListener('click', function () {
        const motivo = prompt('Por favor, informe o motivo da recusa:');
        if (motivo === null || motivo.trim() === '') {
            alert('O motivo da recusa é obrigatório.');
            return;
        }

        // Aqui você pode enviar o motivo para o backend
        updateStatus('Recusado');

        this.textContent = 'Agendamento Recusado';
        this.disabled = true;
        document.getElementById('btn-confirmar').disabled = true;

        alert('Agendamento recusado com sucesso! Motivo: ' + motivo);
    });

    function updateStatus(newStatus) {
        const statusElement = document.getElementById('info-status');
        statusElement.textContent = newStatus;
        statusElement.className = 'info-value status-' + newStatus.toLowerCase();
    }
});