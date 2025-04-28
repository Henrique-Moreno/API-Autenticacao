# Documentação

Esse projeto é uma API de autenticação segura, desenvolvida para ser simples e funcional, ideal para aprender como criar um sistema de login com segurança. Ele foi feito pensando em proteger o acesso dos usuários com várias camadas, como senha criptografada, verificação de IP, um código extra (OTP) que aparece no console e no log, e até um captcha se algo der errado. Tudo isso foi construído de forma organizada para ser fácil de entender e usar, com uma interface interativa para testar os endpoints.

## Como o projeto foi feito

Usamos o **Spring Boot** para criar a API porque ele facilita muito o desenvolvimento, trazendo várias ferramentas prontas para usar. A ideia foi criar um sistema onde o usuário pode se cadastrar, fazer login e confirmar sua identidade com um segundo fator de autenticação (o OTP). Para deixar mais seguro, a senha é criptografada com **SHA256**, e também verificamos o IP do usuário. Se a senha estiver errada, o sistema gera um captcha de 6 dígitos. O OTP, que seria enviado por email em um sistema real, aqui é mostrado no console e registrado no arquivo `log.txt`. Todas as ações, como cadastros e logins, também são salvas nesse arquivo para acompanhamento.

## Ferramentas

- **Spring Boot Starter Data JPA**: Usamos isso para conectar a API ao banco de dados MySQL de forma simples, usando o Hibernate para criar e gerenciar a tabela de usuários automaticamente.
- **Spring Boot Starter Security**: Essa ferramenta adiciona segurança à API, ajudando a proteger os endpoints e garantindo que só usuários autenticados acessem certas partes.
- **Spring Boot Starter Web**: Com ela, conseguimos criar endpoints REST (como `/api/auth/login`) para a API funcionar e responder às requisições.
- **MySQL Connector/J**: É o driver que permite a conexão com o banco de dados MySQL, onde salvamos os dados dos usuários.
- **Swagger Annotations e Springdoc OpenAPI Starter WebMVC UI**: Essas ferramentas geram uma página interativa (Swagger UI) para testar a API. Você pode acessar em `http://localhost:8080/swagger-ui/index.html` e ver todos os endpoints documentados.
- **Jsoup**: Usamos essa biblioteca para limpar as entradas dos usuários, evitando que códigos maliciosos (como scripts) sejam enviados e garantindo mais segurança.

## Organização do projeto

Organizamos o código em pastas para deixar tudo mais claro e seguir boas práticas:

- **Pasta `config`**: Aqui colocamos as configurações gerais, como o `SecurityConfig.java`, que define quais endpoints são públicos (ex.: `/api/auth/**`) e quais precisam de autenticação.
- **Pasta `modules`**: Essa pasta separa o código em partes específicas, cada uma com sua responsabilidade:
  - **`controllers`**: Contém os endpoints da API, como o `AuthController.java`, que gerencia cadastro, login e verificação de OTP.
  - **`dto`**: Tem as classes que recebem os dados das requisições, como `RegisterRequest.java`, para organizar o que chega do frontend.
  - **`entity`**: Aqui fica a classe `User.java`, que representa a tabela de usuários no banco.
  - **`repositories`**: O `UserRepository.java` faz as consultas ao banco de dados, como buscar um usuário pelo username.
  - **`services`**: Contém a lógica do sistema, como o `AuthService.java` (para autenticação), `EmailService.java` (para mostrar o OTP no console e no log) e `LogService.java` (para registrar ações).
  - **`utils`**: Tem utilitários, como o `SanitizationUtil.java`, que limpa os dados recebidos.

Essa separação ajuda a manter o código organizado, fácil de entender e de dar manutenção, seguindo boas práticas de desenvolvimento.
