# CivitasAuto

API REST em Spring Boot que automatiza, via navegador, a atribuição de perfil de usuário nos sistemas
municipais da plataforma CivitasGov (aplicações JSF/PrimeFaces e Angular/PrimeNG).

O operador informa um CPF, os municípios e os módulos alvo; a API cria um lote, responde imediatamente
com um `jobId` e executa a automação em background. O acompanhamento é feito por polling.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.0.6 (Web MVC, Data JPA, Validation, Security) |
| Banco | PostgreSQL (Hibernate, `ddl-auto: update`) |
| Autenticação | JWT (jjwt 0.12.6) + BCrypt |
| Automação | Playwright for Java 1.59.0 (Chromium) |
| Documentação | springdoc-openapi 3.1.0 (Swagger UI) |
| Build | Maven (wrapper incluso) |

---

## Estrutura de pacotes

```
br.com.civitasauto
├── config/        Segurança, JWT filter, CORS, pool assíncrono, Playwright, OpenAPI, seed do admin
├── controllers/   Endpoints REST
├── services/      Regras de negócio e orquestração da automação
├── repository/    Spring Data JPA
├── model/         Entidades JPA (Usuario, Atribuicao, Processamento)
│                  + page objects Playwright (Acesso, AtribuirPerfil, MenuOpcoes, PageManager, Sessao)
├── DTOs/          Records de entrada e saída
├── enums/         Municipios, Modulos, Role e máquinas de estado (StatusLote, StatusItem, ...)
└── exception/     Exceções de negócio e @RestControllerAdvice
```

---

## Modelo de domínio

- **Usuario** — credenciado por CPF, com `Role` (`USER` | `ADMIN`) e `StatusDeferimento`
  (`INDEFERIDO` | `DEFERIDO`). Só usuários deferidos autenticam. Implementa `UserDetails`.
- **Atribuicao** — o *lote*. Guarda o CPF de destino, o usuário que disparou, o `StatusLote` agregado
  e a mensagem final.
- **Processamento** — o *item* do lote: uma combinação município × módulo, com seu próprio `StatusItem`
  e mensagem. Um lote de 3 municípios × 5 módulos gera 15 itens.

---

## Fluxos principais

### 1. Credenciamento e deferimento

```
POST /credenciamento   (público)  → cria usuário como INDEFERIDO, role USER
GET  /deferimento      (ADMIN)    → lista paginada de credenciados, filtrável por status
POST /deferimento      (ADMIN)    → defere o CPF informado e libera o acesso
```

### 2. Autenticação

```
POST /login            (público)  → valida CPF + senha, exige DEFERIDO, devolve JWT
GET  /auth/validate    (autent.)  → 200 se o token ainda é válido
```

O `SecurityFilter` intercepta cada requisição, valida a assinatura/expiração do token e só popula o
`SecurityContext` se o usuário continuar deferido. A API é *stateless* (sem sessão HTTP).

### 3. Atribuição de perfil

```
POST /home/atribuirperfil            (USER|ADMIN) → cria o lote e retorna { "jobId": "..." }
GET  /home/atribuirperfil/{id}/status(USER|ADMIN) → status agregado + resultado item a item
```

Execução:

1. `AtribuicaoPerfilService` valida a entrada, persiste a `Atribuicao` e explode a matriz
   município × módulo em `Processamento`s. Rápido e síncrono.
2. `AtribuicaoExecucaoService.executar` roda com `@Async("atribuicaoExecutor")` fora da thread da
   requisição e percorre os itens em ordem: limpa a sessão do navegador quando o contexto muda,
   faz login no módulo (`Acesso`), navega pelo menu (`MenuOpcoes`) e preenche a tela
   (`AtribuirPerfil`), gravando status e mensagem de cada item.
3. Ao final, o status do lote é agregado: `CONCLUIDO`, `CONCLUIDO_COM_ERROS` ou `ERRO`.
4. O browser é encerrado no `finally`.

---

## Configuração

Variáveis lidas de `.env` (opcional) ou do ambiente:

| Variável | Descrição |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASS` | Conexão PostgreSQL |
| `SERVER_PORT` | Porta HTTP (padrão `8080`) |
| `api.security.token.secret` | Segredo HMAC do JWT |
| `api.security.token.expiracao-minutos` | Validade do token |
| `CPF_PLATFORM_CPF`, `APP_PLATFORM_ADMIN_NAME`, `APP_PLATFORM_ADMIN_PASSWORD` | Admin criado no boot |
| `RPA_LOGIN`, `RPA_SENHA` | Credenciais do usuário robô usado pela automação nos sistemas alvo |
| `RPA_URL_BASE` | Template da URL dos sistemas alvo; `%s` é substituído pelo domínio do município |

Um modelo com todas as variáveis está em `.env.example`.

O `PlatformAdminSeeder` cria o admin da plataforma no primeiro start.

---

## Como executar

```bash
# 1. Instalar os navegadores do Playwright (uma vez)
./mvnw exec:java -Dexec.args="install chromium"

# 2. Subir a aplicação
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível e um ambiente com interface gráfica — a automação roda com o navegador
visível (`headless = false`).

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

CORS liberado para `http://localhost:4200` (frontend Angular).

---

## Convenções

- Comentários no código só para invariantes não óbvias.
- Respostas de erro seguem o formato `{ "status": ..., "erro": ... }`, centralizadas em
  `TratadorDeErros`.
