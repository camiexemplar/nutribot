package br.com.fiap.nutribot.ai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class ClinicDocsLoader {

    private static final Logger log = LoggerFactory.getLogger(ClinicDocsLoader.class);

    @Bean
    public ApplicationRunner loadClinicDocuments(VectorStore vectorStore,
                                                  @Value("${spring.ai.openai.api-key}") String apiKey) {
        return args -> {
            if (apiKey.isBlank() || "not-set".equals(apiKey)) {
                log.warn("RAG: OPENAI_API_KEY não configurada. Documentos não indexados.");
                return;
            }
            try {
                Resource[] resources = new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:docs/*.md");
                List<Document> documents = new ArrayList<>();
                for (Resource resource : resources) {
                    String markdown;
                    try (var input = resource.getInputStream()) {
                        markdown = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    }
                    for (String section : splitSections(markdown)) {
                        documents.add(new Document(section, Map.of("source", resource.getFilename())));
                    }
                }
                if (documents.isEmpty()) {
                    log.warn("RAG: nenhum documento da clínica foi encontrado.");
                    return;
                }
                vectorStore.add(documents);
                log.info("RAG: {} trechos de {} documentos carregados no vector store.",
                        documents.size(), resources.length);
            } catch (Exception e) {
                log.error("RAG: falha ao carregar documentos da clínica. Confira a chave, a URL e o modelo de embeddings.", e);
            }
        };
    }

    public static List<String> splitSections(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return List.of();
        }
        List<String> sections = new ArrayList<>();
        for (String section : markdown.split("(?m)(?=^## )")) {
            if (!section.isBlank()) {
                sections.add(section.strip());
            }
        }
        return sections;
    }
}
