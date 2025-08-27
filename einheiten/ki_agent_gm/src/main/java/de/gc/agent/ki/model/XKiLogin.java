/*
 * Copyright (C) 2025 GNU GENERAL PUBLIC LICENSE Version 3, 29 June 2007
 *
 * Lizenzhinweis / License Notice
 *
 * Deutsch: Dieses Programm ist freie Software. Sie dürfen es unter den
 * Bedingungen der GNU General Public License, Version 3, wie von der
 * Free Software Foundation veröffentlicht, weitergeben und/oder
 * modifizieren. Weitere Informationen finden Sie unter:
 * https://www.gnu.org/licenses/gpl-3.0.de.html
 *
 * English: This program is free software. You can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation. For more
 * information, see: https://www.gnu.org/licenses/gpl-3.0.html
 */
package de.gc.agent.ki.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel.GoogleAiGeminiChatModelBuilder;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel.OllamaChatModelBuilder;

/**
 * XKiLogin is a utility class for creating chat models for various KI
 * systems.
 *
 * @author Michael Niedermair
 */
public class XKiLogin {

   /** Enum for the supported KI systems. */
   public enum KiSystem {
      GEMINI, GITHUB, OLLAMA, NONE
   }

   /**
    * Creates a {@link ChatModel} based on the provided KI system and model
    * name.
    *
    * @param kisystem  The KI system to use.
    * @param modelName The name of the model.
    * @param token     The authentication token.
    * @param url       The base URL for the KI service.
    *
    * @return A configured {@link ChatModel}.
    */
   public static ChatModel createChatModel(final KiSystem kisystem,
         final String modelName,
         final String token, final String url) {

      return switch (kisystem) {

      case GITHUB ->
         createGitHubBuilder(modelName, token, 0.7, 0.95, false,
               Duration.ofSeconds(60));

      case OLLAMA -> createOllamaBuilder(modelName, url, 0.7, 0.95, false,
            Duration.ofSeconds(180));

      case GEMINI ->
         createGeminiBuilder(modelName, token, 0.7, 0.95, false,
               Duration.ofSeconds(60));

      default -> throw new IllegalArgumentException(
            "Chat model for " + kisystem.name() + " not implemented yet.");
      };

   }

   /**
    * Creates a {@link ChatModel} based on the provided configuration.
    *
    * @param kisystem    The KI system to use.
    * @param modelName   The name of the model.
    * @param token       The authentication token.
    * @param url         The base URL for the KI service.
    * @param temperature The temperature for the model.
    * @param topP        The top P value for the model.
    * @param logRequests Whether to log requests.
    * @param timeout     The timeout for requests in seconds.
    */
   public static ChatModel createChatModel(final KiSystem kisystem,
         final String modelName,
         final String token, final String url, final double temperature,
         final double topP,
         final boolean logRequests, final int timeout) {

      // timeout
      final Duration tout = timeout >= 60 ? Duration.ofSeconds(timeout)
            : Duration.ofSeconds(60);

      return switch (kisystem) {

      case GITHUB -> createGitHubBuilder(modelName, token, temperature,
            topP, logRequests, tout);

      case OLLAMA -> createOllamaBuilder(modelName, url, temperature,
            topP, logRequests, tout);

      case GEMINI -> createGeminiBuilder(modelName, token, temperature,
            topP, logRequests, tout);

      default -> throw new IllegalArgumentException(
            "Chat model for " + kisystem.name() + " not implemented yet.");
      };

   }

   /**
    * Creates a {@link ChatModel} for the Gemini KI system.
    *
    * @param modelName   The name of the model.
    * @param token       The authentication token.
    * @param temperature The temperature for the model.
    * @param topP        The top P value for the model.
    * @param logRequests Whether to log requests.
    * @param tout        The timeout for requests.
    */
   private static ChatModel createGeminiBuilder(final String modelName,
         final String token, final double temperature,
         final double topP, final boolean logRequests,
         final Duration tout) {

      final GoogleAiGeminiChatModelBuilder builder = GoogleAiGeminiChatModel
         .builder();
      builder.modelName(modelName);
      builder.apiKey(token);
      builder.timeout(tout);

      // Optional
      Optional.ofNullable(temperature)
         .ifPresent(builder::temperature);
      Optional.ofNullable(topP)
         .ifPresent(builder::topP);
      Optional.ofNullable(logRequests)
         .ifPresent(builder::logRequestsAndResponses);

      return builder.build();
   }

   /**
    * Creates a {@link ChatModel} for the GitHub KI system.
    *
    * @param modelName   The name of the model.
    * @param token       The authentication token.
    * @param temperature The temperature for the model.
    * @param topP        The top P value for the model.
    * @param logRequests Whether to log requests.
    * @param tout        The timeout for requests.
    */
   private static ChatModel createGitHubBuilder(final String modelName,
         final String token, final double temperature, final double topP,
         final boolean logRequests, final Duration tout) {

      final GitHubModelsChatModel.Builder builder = GitHubModelsChatModel
         .builder();
      builder.modelName(modelName);
      builder.gitHubToken(token);
      builder.timeout(tout);

      // Optional
      Optional.ofNullable(temperature)
         .ifPresent(builder::temperature);
      Optional.ofNullable(topP)
         .ifPresent(builder::topP);
      Optional.ofNullable(logRequests)
         .ifPresent(builder::logRequestsAndResponses);

      return builder.build();
   }

   /**
    * Creates a {@link ChatModel} for the Ollama KI system.
    *
    * @param modelName   The name of the model.
    * @param url         The base URL for the Ollama service.
    * @param temperature The temperature for the model.
    * @param topP        The top P value for the model.
    * @param logRequests Whether to log requests.
    * @param tout        The timeout for requests.
    */
   private static ChatModel createOllamaBuilder(final String modelName,
         final String url, final double temperature, final double topP,
         final boolean logRequests, final Duration tout) {

      final OllamaChatModelBuilder builder = OllamaChatModel.builder();
      builder.modelName(modelName);
      builder.baseUrl(url);
      builder.timeout(tout);

      // Optional
      Optional.ofNullable(temperature)
         .ifPresent(builder::temperature);
      Optional.ofNullable(topP)
         .ifPresent(builder::topP);
      Optional.ofNullable(logRequests)
         .ifPresent(builder::logRequests);

      return builder.build();
   }

   /**
    * Retrieves the token for a given KI system from a properties file.
    * <p>
    * Example:
    *
    * <pre>
    * String token = getToken(new File("path/to/propsfile.properties"),"ge");
    *
    * ge_token = YOUR_GEMINI_API_KEY
    * </pre>
    *
    * @param propsfile           The properties file containing the tokens.
    * @param kisystem_short_name The short name of the KI system.
    *
    * @return The token for the specified KI system.
    */
   public static String getToken(final File propsfile,
         final String kisystem_short_name) {
      try {
         final Properties properties = new Properties();
         try (InputStream input = new FileInputStream(propsfile)) {
            properties.load(input);
            return properties.getProperty(kisystem_short_name + "_token");
         }
      } catch (final IOException e) {
         throw new RuntimeException(
               "Error reading properties file: " + propsfile.getAbsolutePath(),
               e);
      }
   }

   /**
    * Converts a string to a KiSystem enum.
    */
   public static KiSystem kiSystemFromString(final String name) {
      for (final KiSystem type : KiSystem.values()) {
         if (type.name()
            .equalsIgnoreCase(name)) {
            return type;
         }
      }
      return KiSystem.NONE;
   }
}
