// Dados das imagens (simulando um banco de dados)
        const eventosEsportivos = [
            { titulo: "Futebol Profissional", imagem: "/../images/futebol.jpg" },
            { titulo: "Tênis de Alta Performance", imagem: "/../images/tenis.jpg" },
            { titulo: "Maratonas Internacionais", imagem: "/../images/maratona.jpg" }
        ];

        // Carrega as caixas dinamicamente
        document.addEventListener('DOMContentLoaded', () => {
            const container = document.getElementById('galeria');
            eventosEsportivos.forEach(evento => {
                container.innerHTML += `
                    <div class="box">
                        <img src="images/${evento.imagem}" alt="${evento.titulo}">
                        <h3>${evento.titulo}</h3>
                    </div>
                `;
            });
        });