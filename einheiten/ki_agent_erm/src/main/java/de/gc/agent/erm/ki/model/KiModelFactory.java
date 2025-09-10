package de.gc.agent.erm.ki.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Eine Factory-Klasse zur Erstellung von ChatModel-Instanzen basierend
 * auf Konfigurationen aus einer Properties-Datei.
 *
 * Diese Klasse liest Modell- und Systemparameter für verschiedene
 * KI-Modelle (Gemini, Ollama, GitHub) aus einer Properties-Datei und
 * gibt darauf basierend das passende ChatModel zurück. Die Auswahl des
 * Modells erfolgt über ein Präfix und den Typ des gewünschten Modells.
 */
public class KiModelFactory {

   /**
    * Enum für die unterstützten KI-Systeme. Verwendet für das Routing auf
    * die jeweiligen KI-Modelle.
    */
   public enum KiSystem {
      GEMINI, GITHUB, OLLAMA, NONE
   }

   /**
    * Erstellt ein ChatModel basierend auf einem Konfigurationspräfix und
    * Modelltyp. Die eigentlichen Modellparameter wie Name, Temperatur etc.
    * werden aus der Properties-Datei gelesen.
    *
    * @param configProps Die geladene Properties-Datei mit allen
    *                    Modell-Konfigurationen.
    * @param prefix      Der Hauptpräfix für das Konfigurations-Set (z.B.
    *                    "ermsystem.gemini").
    * @param modelType   Der Typ des Modells (z.B. "analysis", "table",
    *                    "tutor").
    *
    * @return Ein konfiguriertes ChatModel gemäß den Eigenschaften.
    *
    * @throws IllegalArgumentException Wenn ein notwendiges Token fehlt
    *                                  oder ein System nicht unterstützt
    *                                  wird.
    */
   public static ChatModel createFromPrefix(final Properties configProps,
         final String prefix, final String modelType) {

      final String keyPrefix = prefix + "." + modelType;
      final String systemStr = getProperty(configProps, keyPrefix + ".system",
            "NONE");
      final KiSystem system = kiSystemFromString(systemStr);

      final String modelName = getProperty(configProps,
            keyPrefix + ".model.name", "");
      final double temperature = Double.parseDouble(
            getProperty(configProps, keyPrefix + ".model.temperature", "0.7"));
      final double topP = Double.parseDouble(
            getProperty(configProps, keyPrefix + ".model.topP", "0.95"));
      final int timeout = Integer.parseInt(getProperty(configProps,
            keyPrefix + ".model.timeoutSeconds", "60"));
      final boolean logRequests = Boolean.parseBoolean(getProperty(configProps,
            keyPrefix + ".model.logRequests", "false"));
      final String baseUrl = getProperty(configProps,
            keyPrefix + ".model.baseUrl", "http://127.0.0.1:11434");

      final String token = resolveToken(configProps, prefix, keyPrefix);

      // Initialisiere das gewünschte ChatModel anhand des gewählten Systems.
      return switch (system) {
      case GEMINI -> createGeminiModel(modelName, token, temperature, topP,
            logRequests, Duration.ofSeconds(timeout));
      case OLLAMA -> createOllamaModel(modelName, baseUrl, temperature, topP,
            logRequests, Duration.ofSeconds(timeout));
      case GITHUB -> createGitHubModel(modelName, token, temperature, topP,
            logRequests, Duration.ofSeconds(timeout));
      default ->
         throw new IllegalArgumentException("Unsupported KI System: " + system);
      };
   }

   /**
    * Erstellt ein ChatModel für Google Gemini.
    *
    * @param modelName   Name des Modells.
    * @param token       API-Key für Gemini.
    * @param temperature Kreativität des Modells.
    * @param topP        Sampling-Parameter.
    * @param logRequests Protokollierung der Requests.
    * @param timeout     Timeout als Dauer.
    *
    * @return Konfiguriertes GoogleAiGeminiChatModel.
    *
    * @throws IllegalArgumentException Falls kein Token angegeben ist.
    */
   private static ChatModel createGeminiModel(final String modelName,
         final String token, final double temperature, final double topP,
         final boolean logRequests, final Duration timeout) {
      if (token == null) {
         throw new IllegalArgumentException("API key for Gemini is required.");
      }
      return GoogleAiGeminiChatModel.builder()
         .apiKey(token)
         .modelName(modelName)
         .temperature(temperature)
         .topP(topP)
         .timeout(timeout)
         .logRequestsAndResponses(logRequests)
         .build();
   }

   /**
    * Erstellt ein ChatModel für GitHub KI-Modelle.
    *
    * @param modelName   Name des Modells.
    * @param token       GitHub-Token.
    * @param temperature Kreativität des Modells.
    * @param topP        Sampling-Parameter.
    * @param logRequests Protokollierung der Requests.
    * @param timeout     Timeout als Dauer.
    *
    * @return Konfiguriertes GitHubModelsChatModel.
    *
    * @throws IllegalArgumentException Falls kein Token angegeben ist.
    */
   private static ChatModel createGitHubModel(final String modelName,
         final String token, final double temperature, final double topP,
         final boolean logRequests, final Duration timeout) {
      if (token == null) {
         throw new IllegalArgumentException("GitHub token is required.");
      }
      return GitHubModelsChatModel.builder()
         .gitHubToken(token)
         .modelName(modelName)
         .temperature(temperature)
         .topP(topP)
         .timeout(timeout)
         .logRequestsAndResponses(logRequests)
         .build();
   }

   /**
    * Erstellt ein ChatModel für Ollama KI-Modelle.
    *
    * @param modelName   Name des Modells.
    * @param baseUrl     Basis-URL der Ollama-API.
    * @param temperature Kreativität des Modells.
    * @param topP        Sampling-Parameter.
    * @param logRequests Protokollierung der Requests.
    * @param timeout     Timeout als Dauer.
    *
    * @return Konfiguriertes OllamaChatModel.
    */
   private static ChatModel createOllamaModel(final String modelName,
         final String baseUrl, final double temperature, final double topP,
         final boolean logRequests, final Duration timeout) {
      return OllamaChatModel.builder()
         .baseUrl(baseUrl)
         .modelName(modelName)
         .temperature(temperature)
         .topP(topP)
         .timeout(timeout)
         .logRequests(logRequests)
         .build();
   }

   /**
    * Hilfsfunktion zum Auslesen einer Eigenschaft aus der
    * Properties-Datei.
    *
    * @param props        Properties-Objekt.
    * @param key          Key der gewünschten Eigenschaft.
    * @param defaultValue Rückgabewert, falls Eigenschaft nicht gesetzt.
    *
    * @return Wert der Eigenschaft, ggf. der Default-Wert.
    */
   private static String getProperty(final Properties props, final String key,
         final String defaultValue) {
      final String value = props.getProperty(key);
      return value == null || value.isBlank() ? defaultValue : value;
   }

   /**
    * Wandelt einen String in das passende KiSystem-Enum um.
    *
    * @param name Name des gewünschten Systems als String.
    *
    * @return Passendes KiSystem-Enum.
    */
   private static KiSystem kiSystemFromString(final String name) {
      for (final KiSystem type : KiSystem.values()) {
         if (type.name()
            .equalsIgnoreCase(name)) {
            return type;
         }
      }
      return KiSystem.NONE;
   }

   /**
    * Löst das benötigte Token für das KI-System aus der Konfiguration oder
    * einer sicheren Properties-Datei auf.
    *
    * Falls ein apiKeyLookup angegeben ist, wird das Token aus einer
    * separaten, sicheren Properties-Datei geladen. Andernfalls wird das
    * Token direkt aus der Haupt-Properties zurückgegeben.
    *
    * @param configProps Properties mit Modell- und System-Konfiguration.
    * @param mainPrefix  Präfix des Hauptsystems (z.B. "ermsystem.gemini").
    * @param modelPrefix Präfix des Modelltyps (z.B.
    *                    "ermsystem.gemini.analysis").
    *
    * @return Das gefundene Token als String, oder null falls keines
    *         vorhanden.
    *
    * @throws IllegalArgumentException Bei fehlender oder fehlerhafter
    *                                  Token-Konfiguration.
    * @throws RuntimeException         Bei IO-Fehlern mit der sicheren
    *                                  Properties-Datei.
    */
   private static String resolveToken(final Properties configProps,
         final String mainPrefix, final String modelPrefix) {

      final String apiKeyLookup = getProperty(configProps,
            modelPrefix + ".model.apiKeyLookup", null);
      if (apiKeyLookup != null) {
         final String securePropsPath = getProperty(configProps,
               mainPrefix + ".secure.properties.path", null);
         if (securePropsPath == null) {
            throw new IllegalArgumentException("Property '" + modelPrefix
                  + ".model.apiKeyLookup' is set, but '" + mainPrefix
                  + ".secure.properties.path' is missing.");
         }
         try {
            final Properties secureProps = new Properties();
            secureProps.load(new FileInputStream(new File(securePropsPath)));
            final String token = secureProps.getProperty(apiKeyLookup);
            if (token == null) {
               throw new IllegalArgumentException("Token key '" + apiKeyLookup
                     + "' not found in secure properties file: "
                     + securePropsPath);
            }
            return token;
         } catch (final IOException e) {
            throw new RuntimeException(
                  "Could not load secure properties file: " + securePropsPath,
                  e);
         }
      }
      // Fallback auf Klartext-Token aus Hauptkonfiguration
      return getProperty(configProps, modelPrefix + ".model.apiKey", null);
   }
}
