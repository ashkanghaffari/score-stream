package org.ashkan.ghaffari.inferenceanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class InferenceAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InferenceAnalyzerApplication.class, args);
    }

    @Bean
    public Function<String, String> inferenceAnalysis(InferenceAnalyzer inferenceAnalyzer) {
        return input -> {
            inferenceAnalyzer.analyze();
            return "Analysis completed successfully.";
        };
    }
}
