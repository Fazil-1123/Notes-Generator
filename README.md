# Notes Generator (Spring Boot + Spring AI)

Minimal REST service that turns a lecture **transcript** into clean, structured study notes.  
It can use either **OpenAI** or a local **Ollama** model (default in `application.yaml` is `gemma3:12b`).

## What it does
- Exposes HTTP endpoints to generate notes from a transcript using a strict template.
- Ships a system prompt and a StringTemplate (`src/main/resources/promptTemplate/userPrompt.st`) that build the user prompt with:
  - `sectionNumber`, `videoNumber`, optional `title`, and the raw `transcript`.
- Two model backends wired via Spring AI:
  - OpenAI (`spring.ai.openai`), key taken from `OPENAI_KEY`.
  - Ollama (`spring.ai.ollama`), model set in `application.yaml`.

## Endpoints (from `ChatController`)
Base path: `/api`

- `GET /api/ollama/chat?prompt=...`  
  Quick sanity check against the Ollama model.

- `POST /api/ollama/transcript` (multipart/form-data)  
  Generates notes from a transcript. Typical parts/fields:
  - `file` (MultipartFile) **or** a raw transcript field
  - `sectionNumber` (int)
  - `videoNumber` (int)
  - `title` (optional string)

> Tip: See `src/main/java/com/ecom/springai/controller/ChatController.java` for the exact parameter names.

## Requirements
- Java 21 (see `<java.version>21</java.version>` in `pom.xml`)
- Maven (wrapper included: `mvnw`/`mvnw.cmd`)
- For OpenAI: an API key in env var `OPENAI_KEY`
- For Ollama (optional): [Ollama](https://ollama.com/) running locally with the configured model (default `gemma3:12b`).

## Configure
`src/main/resources/application.yaml` controls model selection:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_KEY}
    ollama:
      chat:
        options:
          model: gemma3:12b
```
You can switch models or point to OpenAI only by editing this file.

## Run (dev)
Using Maven wrapper:

```bash
# OpenAI path
export OPENAI_KEY=sk-...        # set your key
./mvnw spring-boot:run

# Ollama path (example)
# 1) Install & start Ollama
# 2) Pull a model (first time only):  ollama pull gemma3:12b
./mvnw spring-boot:run
```

## Build a JAR
```bash
./mvnw -DskipTests package
java -jar target/Notes-Generator-0.0.1-SNAPSHOT.jar
```

## Smoke tests
```bash
# 1) Simple chat check against Ollama
curl 'http://localhost:8080/api/ollama/chat?prompt=hello'

# 2) Generate notes from a transcript file
curl -X POST 'http://localhost:8080/api/ollama/transcript'   -F 'file=@/path/to/transcript.txt'   -F 'sectionNumber=1'   -F 'videoNumber=2'   -F 'title=Intro to Spring AI'
```

## Project layout (key files)
- `pom.xml` — Spring Boot 3.5.x, Spring AI deps
- `src/main/resources/application.yaml` — model + API config
- `src/main/resources/promptTemplate/userPrompt.st` — StringTemplate user prompt
- `src/main/java/com/ecom/springai/helper/PromptConstants.java` — system prompt (notes format)
- `src/main/java/com/ecom/springai/config/ChatClientConfig.java` — ChatClient beans (OpenAI & Ollama)
- `src/main/java/com/ecom/springai/controller/ChatController.java` — REST endpoints
