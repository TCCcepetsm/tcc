document.addEventListener('DOMContentLoaded', function () {
    // 1. Verificar autenticação e perfil
    const token = localStorage.getItem('jwtToken');
    const userData = JSON.parse(localStorage.getItem('userData') || '{}');

    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // 2. Se NÃO for profissional, redireciona para página comum
    if (!userData.roles?.includes('ROLE_PROFISSIONAL')) {
        window.location.href = 'inicial.html';
        return;
    }

    // 3. Configurar botão de logout
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }

    // 4. Efeitos interativos (apenas para admin)
    const adminCards = document.querySelectorAll('.admin-card');
    adminCards.forEach(card => {
        card.addEventListener('click', () => {
            card.style.transform = 'scale(0.98)';
            setTimeout(() => card.style.transform = 'scale(1)', 200);
        });
    });

    // Função global de navegação
    window.navigateTo = function (page) {
        window.location.href = page;
    };
});

// Função para lidar com o logout
function handleLogout() {
    // Limpar dados de autenticação do localStorage
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userData');

    // Opcional: Fazer requisição para invalidar o token no servidor
    fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
        }
    });

    // Redirecionar para a página de login
    window.location.href = 'login.html';
}
