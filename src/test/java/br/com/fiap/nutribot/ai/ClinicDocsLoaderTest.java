package br.com.fiap.nutribot.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

class ClinicDocsLoaderTest {

    @Test
    void shouldLoadAllSections() throws Exception {
        var resources = new PathMatchingResourcePatternResolver().getResources("classpath*:docs/*.md");
        assertEquals(2, resources.length);
        int count = 0;
        for (var resource : resources) {
            try (var input = resource.getInputStream()) {
                count += ClinicDocsLoader.splitSections(new String(input.readAllBytes(), StandardCharsets.UTF_8)).size();
            }
        }
        assertEquals(13, count);
    }

    @Test
    void shouldPreserveClinicFacts() throws Exception {
        var resource = new PathMatchingResourcePatternResolver()
                .getResource("classpath:docs/guia-alimentar.md");
        try (var input = resource.getInputStream()) {
            var sections = ClinicDocsLoader.splitSections(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            assertTrue(sections.stream().anyMatch(section -> section.contains("10 copos de 200 ml")));
        }
    }

    @Test
    void shouldHandleEmptyContent() {
        assertTrue(ClinicDocsLoader.splitSections("").isEmpty());
        assertTrue(ClinicDocsLoader.splitSections(null).isEmpty());
        assertEquals(2, ClinicDocsLoader.splitSections("# Título\n\n## Primeiro\nTexto").size());
    }
}
