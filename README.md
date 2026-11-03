# NutriBot API — Projeto Diamante

**Java Advanced · 2º semestre · FIAP**

A NutriBot é a assistente virtual da Clínica Equilíbrio. A API foi desenvolvida a partir do projeto-base do professor e integra um aplicativo Expo para validação das funcionalidades de inteligência artificial. O backend usa Spring Boot, Spring AI, banco H2 e um vector store em memória.

## Equipe

| Nome completo | RM |
|---|---|
| [NOME COMPLETO DO INTEGRANTE 1] | [RM DO INTEGRANTE 1] |
| [NOME COMPLETO DO INTEGRANTE 2] | [RM DO INTEGRANTE 2] |
| [NOME COMPLETO DO INTEGRANTE 3] | [RM DO INTEGRANTE 3] |

Preencha somente as linhas dos integrantes da equipe antes de publicar.

## Modelo de inteligência artificial

| Item | Configuração padrão |
|---|---|
| Provedor | OpenAI (API compatível com OpenAI) |
| Chat | `gpt-4o-mini` |
| Embeddings | `text-embedding-3-small` |
| Vector store | `SimpleVectorStore`, em memória |

O projeto permite configurar outro provedor e modelo por variáveis de ambiente. Na entrega, registre os modelos efetivamente utilizados, caso sejam diferentes dos padrões.

## Funcionalidades

| Requisito | Implementação |
|---|---|
| Chat com system prompt | `POST /chat`, `ChatService`, `Prompts` |
| Memória por conversa | `ChatMemory`, `MessageChatMemoryAdvisor` e `conversationId` |
| Tool calling | `@Tool` em `NutriTools`: `searchFood`, `getDailySummary` e `logMeal` |
| RAG | `ClinicDocsLoader`, `SimpleVectorStore` e `QuestionAnswerAdvisor` |
| Structured output | `POST /meals/analyze` com `.entity(MealAnalysisResponse.class)` |
| Integração | App Expo original do professor em `app/` |

## Estrutura

```text
nutribot-diamante/
├── pom.xml
├── src/
│   ├── main/java/br/com/fiap/nutribot/
│   │   ├── ai/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── data/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   ├── main/resources/docs/
│   └── test/
├── app/
├── test.http
└── .env.example
```

## Requisitos

- **JDK 25** e `JAVA_HOME` configurado.
- **Node.js 20 ou superior** e npm, para executar o front.
- Conexão com o provedor de IA escolhido, com modelos de chat, tool calling e embeddings disponíveis.
- A chave de API é configurada somente em variável de ambiente e nunca deve ser enviada ao repositório.

## Executar a API no Windows

No PowerShell, na pasta raiz do projeto:

```powershell
Copy-Item .env.example .env
notepad .env
```

Insira sua chave `OPENAI_API_KEY` e confirme o provedor e os nomes dos modelos. Para carregar as variáveis no PowerShell atual:

```powershell
Get-Content .env | Where-Object { $_ -match '^\s*[A-Z_]+=' } | ForEach-Object {
  $k, $v = $_ -split '=', 2
  Set-Item -Path "Env:$($k.Trim())" -Value $v.Trim()
}

.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

O script `mvnw.cmd` baixa o Maven automaticamente na primeira execução. A API responde em **http://localhost:8080**. O `DataLoader` inicializa a tabela de alimentos, o perfil e refeições. Na inicialização, o `ClinicDocsLoader` carrega os documentos em `src/main/resources/docs/` para a busca vetorial. Caso a chave seja inválida, a API ainda pode atender as rotas básicas, mas RAG e funcionalidades de IA ficam indisponíveis até a configuração ser corrigida.

Em Linux ou macOS, use `./mvnw test` e `./mvnw spring-boot:run` depois de exportar as variáveis de ambiente.

## Executar o aplicativo do professor

Em outro terminal:

```powershell
cd app
npm install
npx expo start
```

Pressione `w` para abrir no navegador ou leia o QR Code com o Expo Go. No navegador, use `localhost` como endereço da API. No celular físico, configure no botão de ajustes o IP local do computador (por exemplo, `192.168.1.10`). O celular e o computador devem estar conectados à mesma rede. O app utiliza as mesmas rotas do projeto-base, sem mudança de contrato.

## Rotas HTTP

| Método | Endpoint | Função |
|---|---|---|
| `POST` | `/chat` | Chat com memória, tools e RAG |
| `POST` | `/meals/analyze` | Estima nutrientes sem gravar a refeição |
| `GET` | `/foods?name=` | Consulta a tabela alimentar |
| `GET` | `/foods/{id}` | Consulta um alimento |
| `GET` | `/meals?date=` | Consulta o diário |
| `POST` | `/meals` | Registra uma refeição |
| `DELETE` | `/meals/{id}` | Remove uma refeição |
| `GET` | `/summary?date=` | Consulta calorias e macros do dia |
| `GET` | `/profile` | Consulta o perfil |
| `PUT` | `/profile` | Atualiza o perfil |

### Chat

```http
POST http://localhost:8080/chat
Content-Type: application/json

{"conversationId":"conversa-1","message":"Quantos copos de água devo beber por dia?"}
```

Resposta:

```json
{"answer":"A Clínica Equilíbrio recomenda 10 copos de 200 ml de água por dia, totalizando 2 litros."}
```

O conteúdo da resposta é gerado pelo modelo. Para testar a memória, envie `"Meu nome é Rafael"`, depois `"Qual é o meu nome?"` com o mesmo `conversationId`; use outro identificador para verificar o isolamento. Para tool calling, experimente `"Quantas calorias tem 100 g de tapioca?"`, `"Quantas calorias ainda posso comer hoje?"` e `"Comi 2 ovos no café"`.

### Structured output

```http
POST http://localhost:8080/meals/analyze
Content-Type: application/json

{"description":"arroz, feijão e um bife"}
```

O formato esperado é:

```json
{
  "items": [
    {"food":"Arroz branco","grams":150,"calories":192},
    {"food":"Feijão carioca","grams":100,"calories":76},
    {"food":"Bife grelhado","grams":120,"calories":289}
  ],
  "totalCalories":557,
  "protein":48.2,
  "carbs":56.0,
  "fat":14.1
}
```

Os números acima são somente um exemplo do contrato, não uma resposta garantida. A análise **não** registra refeições no diário.

## Provedores e configuração

As variáveis são:

| Variável | Exemplo |
|---|---|
| `OPENAI_API_KEY` | Sua chave privada, não versionada |
| `OPENAI_BASE_URL` | `https://api.openai.com/v1` |
| `OPENAI_MODEL` | `gpt-4o-mini` |
| `OPENAI_EMBEDDING_MODEL` | `text-embedding-3-small` |

Para usar Ollama com API compatível com OpenAI, configure a base como `http://localhost:11434/v1`, `OPENAI_API_KEY=ollama`, `OPENAI_MODEL=llama3.2` e `OPENAI_EMBEDDING_MODEL=nomic-embed-text`. É necessário baixar os dois modelos localmente e garantir suporte às tools. Para experimentar a API compatível com OpenAI do Gemini, use uma chave **válida** do Google AI Studio e configure `OPENAI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/openai`, `OPENAI_MODEL=gemini-2.5-flash` e `OPENAI_EMBEDDING_MODEL=gemini-embedding-001`. A compatibilidade das ferramentas e embeddings deve ser validada com sua conta e modelo. Se aparecer `API_KEY_INVALID`, gere ou confira a chave no provedor correto. Não misture uma chave do Google com o endereço da OpenAI nem o contrário.

O arquivo `.env` está ignorado pelo Git. O exemplo `.env.example` não contém segredos. Nunca publique chaves reais.

## Testes e entrega

O arquivo `test.http` pode ser executado pelo cliente HTTP do IntelliJ. Os testes unitários em `src/test/` não exigem chamadas ao provedor externo. Para conferir toda a integração, execute as rotas com uma chave válida e teste as quatro abas do aplicativo.

O professor solicita a entrega do **link do repositório GitHub** na plataforma GeminiDev. Para preservar a origem solicitada, crie um fork de [joaocarloslima/nutribot](https://github.com/joaocarloslima/nutribot) e copie os arquivos deste projeto para o fork, incluindo a pasta `app/`. Preencha os nomes, RMs e modelos efetivamente usados; execute os testes; confirme que o `.env` não foi incluído; então publique o repositório.

## Referências

- [Projeto-base NutriBot](https://github.com/joaocarloslima/nutribot)
- [App do professor](https://github.com/joaocarloslima/nutribot-app)
- [suporte-pg](https://github.com/joaocarloslima/suporte-pg)
- [suporte-px](https://github.com/joaocarloslima/suporte-px)
- [trip-canvas-px](https://github.com/joaocarloslima/trip-canvas-px)
