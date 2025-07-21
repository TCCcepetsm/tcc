document.addEventListener('DOMContentLoaded', function () {
    // 1. Verificar autenticação
    const token = localStorage.getItem('authToken') || localStorage.getItem('jwtToken');
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || localStorage.getItem('userData') || '{}');

    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // 2. Se for profissional, redireciona para admin
    if (userInfo.roles?.includes('ROLE_PROFISSIONAL') || userInfo.roles?.includes('ROLE_ADMIN')) {
        window.location.href = 'inicialAdmin.html';
        return;
    }

    // 3. Configurar botão de logout
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function () {
            // Limpa todos os dados de autenticação
            localStorage.removeItem('authToken');
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userInfo');
            localStorage.removeItem('userData');

            // Redireciona para login
            window.location.href = 'login.html';
        });
    }

    // 4. Efeitos interativos
    const cards = document.querySelectorAll('.service-card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'translateY(-5px)';
            card.style.boxShadow = '0 10px 20px rgba(0,0,0,0.15)';
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0)';
            card.style.boxShadow = '0 5px 15px rgba(0,0,0,0.1)';
        });
    });

    // Função global de navegação
    window.navigateTo = function (page) {
        window.location.href = page;
    };
});