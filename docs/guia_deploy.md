# Guia de Deploy do Projeto

Este guia detalha o processo de deploy do seu projeto, incluindo a configuração do banco de dados PostgreSQL e armazenamento de imagens no Supabase, e a hospedagem do backend (Spring Boot) e frontend (HTML/CSS/JS) no Render.

## 1. Configuração do Supabase

O Supabase oferece um backend de código aberto com banco de dados PostgreSQL, autenticação, armazenamento de arquivos e muito mais. É uma excelente opção para projetos que buscam soluções gratuitas e escaláveis.

### 1.1. Criar um Novo Projeto no Supabase

1. Acesse o site do Supabase: `https://supabase.com/`
2. Clique em `Start your project` ou `Sign In` se já tiver uma conta.
3. Faça login com sua conta GitHub ou crie uma nova conta.
4. No painel, clique em `New project`.
5. Preencha os detalhes do projeto:
   - **Name:** Escolha um nome para o seu projeto (ex: `recorder-app`).
   - **Organization:** Selecione sua organização ou crie uma nova.
   - **Database Password:** Crie uma senha forte para o seu banco de dados. **Guarde esta senha em segurança!**
   - **Region:** Escolha a região mais próxima de você ou dos seus usuários para menor latência.
6. Clique em `Create new project`.

### 1.2. Configurar o Banco de Dados PostgreSQL

O Supabase já provisiona um banco de dados PostgreSQL para você. Você precisará obter as credenciais de conexão para configurar seu backend.

1. No painel do seu projeto Supabase, navegue até `Project Settings` (ícone de engrenagem no canto inferior esquerdo).
2. Clique em `Database`.
3. Na seção `Connection string`, você encontrará as credenciais para o seu banco de dados. Copie a `Connection string` (URI) para uso posterior. Ela será algo como:
   `postgresql://postgres:[YOUR_PASSWORD]@db.[YOUR_PROJECT_REF].supabase.co:5432/postgres`

   **Importante:** Substitua `[YOUR_PASSWORD]` pela senha que você definiu ao criar o projeto.

### 1.3. Configurar o Supabase Storage para Imagens

O Supabase Storage permite armazenar e servir arquivos, como imagens, de forma eficiente.

1. No painel do seu projeto Supabase, navegue até `Storage` (ícone de pasta).
2. Clique em `New bucket`.
3. Preencha os detalhes do bucket:
   - **Name:** Dê um nome ao seu bucket (ex: `imagens-projeto`). **Este nome deve ser o mesmo configurado no seu `application.properties` (`supabase.bucket=your-bucket-name`).**
   - **Public bucket:** Marque esta opção se as imagens forem acessíveis publicamente (o que é o caso para galerias de imagens, por exemplo). Se não for marcado, você precisará de autenticação para acessar os arquivos.
4. Clique em `Create bucket`.

### 1.4. Obter as Chaves de API do Supabase

Seu backend precisará de chaves de API para interagir com o Supabase (banco de dados e storage).

1. No painel do seu projeto Supabase, navegue até `Project Settings`.
2. Clique em `API`.
3. Você encontrará as chaves `anon public` e `service_role secret`. Para o frontend e para a maioria das interações do backend, a chave `anon public` é suficiente. Para operações que exigem mais privilégios (como algumas operações de storage ou acesso direto ao banco de dados via API), a `service_role secret` pode ser necessária, mas use-a com cautela, pois ela tem privilégios de administrador.

   **Guarde a `anon public key` (API Key) e a `Project URL` (URL do projeto) para uso posterior.** Elas serão usadas para configurar as variáveis de ambiente no seu backend.



## 2. Deploy do Backend (Spring Boot) no Render

O Render é uma plataforma de nuvem unificada que permite hospedar aplicações web, bancos de dados e muito mais. Ele suporta deploy via Docker, o que é ideal para sua aplicação Spring Boot.

### 2.1. Criar um Dockerfile para o Backend

Para que o Render possa construir e executar sua aplicação Spring Boot, você precisará de um `Dockerfile` na raiz do seu diretório `backend`. Crie um arquivo chamado `Dockerfile` (sem extensão) dentro de `/home/ubuntu/projeto/backend` com o seguinte conteúdo:

```dockerfile
# Use uma imagem base oficial do OpenJDK com a versão do Java que você está usando (Java 17)
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o script de build do Maven e o pom.xml para otimizar o cache do Docker
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Copia o código fonte da aplicação
COPY src ./src

# Constrói a aplicação Spring Boot
RUN ./mvnw clean package -DskipTests

# Expõe a porta em que a aplicação Spring Boot será executada (8080 por padrão)
EXPOSE 8080

# Define o comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "target/recorder-src-0.0.1-SNAPSHOT.jar"]
```

**Observação:** Certifique-se de que o nome do arquivo `.jar` no `ENTRYPOINT` (`recorder-src-0.0.1-SNAPSHOT.jar`) corresponde ao nome do artefato gerado pelo seu `pom.xml`.

### 2.2. Configurar o Render para o Backend

1. Acesse o site do Render: `https://render.com/`
2. Clique em `Get Started Free` ou `Sign In` se já tiver uma conta.
3. Faça login com sua conta GitHub ou crie uma nova conta.
4. No painel, clique em `New Web Service`.
5. Conecte seu repositório GitHub onde o projeto está hospedado. Se o projeto ainda não estiver em um repositório, você precisará criá-lo e fazer o push.
6. Selecione o repositório do seu projeto.
7. Configure o serviço web:
   - **Name:** Dê um nome ao seu serviço (ex: `recorder-backend`).
   - **Region:** Escolha a região mais próxima de você ou dos seus usuários.
   - **Branch:** `main` (ou a branch que você usa para deploy).
   - **Root Directory:** `/backend` (o diretório onde seu `Dockerfile` está localizado dentro do repositório).
   - **Runtime:** `Docker`.
   - **Build Command:** Deixe em branco, pois o `Dockerfile` já contém o comando de build.
   - **Start Command:** Deixe em branco, pois o `Dockerfile` já contém o comando de start (`ENTRYPOINT`).
   - **Plan:** Escolha o plano `Free`.

### 2.3. Configurar Variáveis de Ambiente no Render

As variáveis de ambiente configuradas no `application.properties` do seu Spring Boot precisarão ser definidas no Render para que a aplicação possa se conectar ao Supabase.

1. No painel de configuração do seu serviço web no Render, vá para a seção `Environment`.
2. Adicione as seguintes variáveis de ambiente, substituindo os valores pelos seus dados do Supabase:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://db.[YOUR_PROJECT_REF].supabase.co:5432/postgres` (Copie a URI do Supabase e remova `postgres:` e `[YOUR_PASSWORD]@`)
   - `SPRING_DATASOURCE_USERNAME`: `postgres`
   - `SPRING_DATASOURCE_PASSWORD`: Sua senha do banco de dados Supabase
   - `SUPABASE_URL`: `https://[YOUR_PROJECT_REF].supabase.co`
   - `SUPABASE_KEY`: Sua `anon public key` do Supabase
   - `SUPABASE_BUCKET`: O nome do seu bucket de armazenamento no Supabase (ex: `imagens-projeto`)
   - `JWT_SECRET`: Sua chave JWT (a mesma do `application.properties`)
   - `JWT_EXPIRATION`: `86400000`
   - `JWT_ISSUER`: `recorder-app`
   - `SPRING_SECURITY_USER_NAME`: `admin`
   - `SPRING_SECURITY_USER_PASSWORD`: `admin123`
   - `SPRING_SECURITY_USER_ROLES`: `ADMIN`

   **Importante:** Para `SPRING_DATASOURCE_URL`, o Render espera apenas o host e a porta, não o usuário e a senha. A senha e o usuário são fornecidos separadamente.

3. Clique em `Create Web Service`.

O Render começará a construir e implantar sua aplicação. Você pode acompanhar o progresso nos logs de deploy. Uma vez que o deploy esteja completo, o Render fornecerá uma URL pública para o seu backend.



## 3. Deploy do Frontend (HTML/CSS/JS) no Render

O frontend do seu projeto é composto por arquivos estáticos (HTML, CSS, JavaScript). O Render pode hospedar esses arquivos como um `Static Site`.

### 3.1. Configurar o Render para o Frontend

1. Acesse o site do Render: `https://render.com/`
2. No painel, clique em `New Static Site`.
3. Conecte seu repositório GitHub onde o projeto está hospedado. Certifique-se de que o frontend esteja no mesmo repositório ou em um repositório separado, dependendo da sua estrutura.
4. Selecione o repositório do seu projeto.
5. Configure o site estático:
   - **Name:** Dê um nome ao seu site (ex: `recorder-frontend`).
   - **Region:** Escolha a região mais próxima de você ou dos seus usuários.
   - **Branch:** `main` (ou a branch que você usa para deploy).
   - **Root Directory:** `/frontend` (o diretório onde seus arquivos HTML, CSS e JS estão localizados dentro do repositório).
   - **Build Command:** Deixe em branco, pois não há um processo de build complexo para arquivos estáticos simples.
   - **Publish Directory:** Deixe em branco ou defina como `/frontend` se o Render solicitar um diretório de publicação.
   - **Plan:** Escolha o plano `Free`.

6. Clique em `Create Static Site`.

O Render fará o deploy dos seus arquivos estáticos e fornecerá uma URL pública para o seu frontend.

### 3.2. Atualizar URLs do Backend no Frontend

Após o deploy do backend no Render, você terá uma URL pública para ele (ex: `https://recorder-backend.onrender.com`). Você precisará atualizar os arquivos JavaScript do seu frontend para apontar para esta nova URL, em vez de `http://localhost:8080`.

1. Abra os arquivos JavaScript no diretório `/home/ubuntu/projeto/frontend/public/js/`.
2. Procure por todas as ocorrências de `http://localhost:8080`.
3. Substitua `http://localhost:8080` pela URL pública do seu backend no Render (ex: `https://recorder-backend.onrender.com`).

   **Exemplo (no `login.js`):**
   ```javascript
   // Antes:
   const response = await fetch('http://localhost:8080/api/auth/authenticate', {

   // Depois:
   const response = await fetch('https://recorder-backend.onrender.com/api/auth/authenticate', {
   ```

4. Após fazer as alterações, faça o commit e push para o seu repositório GitHub. O Render detectará as mudanças e fará o redeploy automaticamente.

## 4. Considerações Finais

- **Entidade `Endereco`:** Foi identificado que a entidade `Endereco.java` não está anotada com `@Entity` ou `@Embeddable`. Para que o JPA a reconheça e crie a tabela correspondente (ou a incorpore em outra entidade), você precisará adicionar a anotação `@Entity` (se for uma tabela separada) ou `@Embeddable` (se for um componente de outra entidade) e configurar o relacionamento adequado.
- **`ddl-auto=update`:** A configuração `spring.jpa.hibernate.ddl-auto=update` no `application.properties` é útil para desenvolvimento, pois o Hibernate tenta atualizar o esquema do banco de dados automaticamente. No entanto, em ambientes de produção, é geralmente recomendado usar migrações de banco de dados (como Flyway ou Liquibase) para gerenciar o esquema de forma mais controlada e evitar perdas de dados.
- **Planos Gratuitos:** Lembre-se que os planos gratuitos do Supabase e Render possuem limitações de recursos (uso de CPU, memória, largura de banda, etc.). Para projetos com maior tráfego ou requisitos de desempenho, pode ser necessário considerar a atualização para planos pagos no futuro.

Este guia deve fornecer um ponto de partida sólido para o deploy do seu projeto. Certifique-se de seguir cada passo cuidadosamente e ajustar as configurações conforme necessário para o seu ambiente específico.

