package de.gc.agent.db;

import java.util.Scanner;

import de.gc.agent.ki.model.XKiLogin;
import de.gc.agent.ki.model.XKiLogin.KiSystem;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Dies ist die Hauptklasse, die unsere Anwendung startet. Sie baut den
 * "DB-Genius"-Agenten zusammen und startet die interaktive Konsole.
 */
public class Main {

   public static void main(final String[] args) {

      try {

         // ==========================================================================
         // 1. KONFIGURATION & SETUP
         // ==========================================================================

         // Hier fügt jeder Schüler seinen eigenen, kostenlosen API-Schlüssel
         // vom Google AI Studio ein oder einem anderem System.
         //
         // über die Umgebungsvariable:
         // String token = System.getenv("GEMINI_API_KEY");
         //
         // Als String im Code - das ist nicht empfohlen
         // String token = "DEIN_KOSTENLOSER_GEMINI_API_SCHLUESSEL_HIER";
         //
         // oder aus einer Datei, die wir hier angeben:
         // z.B.
         // ge_token=XXXGEHEIMXXX
         // rest_token=YYYGEHEIMYYY
         // siehe auch ki/model/XKiLogin.java

         // -----------------------------------------------------------------
         // GEMINI -> ANPASSEN
         // final String token = XKiLogin.getToken(new
         // File("/tmp/gi.properties"), "ge");
         //
         // // Wir erstellen das Sprachmodell - hier für Google Gemini.
         // final ChatModel model = XKiLogin.createChatModel(KiSystem.GEMINI,
         // "gemini-1.5-flash-latest", token, null);

         // -----------------------------------------------------------------
         // GITHUB -> ANPASSEN
         // final String token = XKiLogin.getToken(new
         // File("/tmp/gi.properties"), "rest");
         //
         // // Wir erstellen das Sprachmodell - hier für Github Copilot.
         // final ChatModel model = XKiLogin.createChatModel(KiSystem.GITHUB,
         // "GPT-4.1", token, null);

         // -----------------------------------------------------------------
         // OLLAMA -> ANPASSEN
         // Wir erstellen das Sprachmodell - hier für Ollama - lokal.
         //
         final ChatModel model = XKiLogin.createChatModel(KiSystem.OLLAMA,
               "mistral:7b", null,
               "http://127.0.0.1:11434");

         // Wir geben dem Agenten ein Kurzzeitgedächtnis für 10 Nachrichten.
         final ChatMemory chatMemory = MessageWindowChatMemory
            .withMaxMessages(10);

         // ==========================================================================
         // 2. DER ZUSAMMENBAU DES AGENTEN
         // ==========================================================================

         // Mit 'AiServices.builder()' beginnt die "Montage" unseres Agenten.
         final StudentAgent agent = AiServices.builder(StudentAgent.class)
            .chatModel(model)
            .tools(new DatabaseTools())
            .chatMemory(chatMemory)
            .build();

         // ==========================================================================
         // 3. DIE INTERAKTIVE KONSOLE
         // ==========================================================================

         System.out.println("""
               Hallo! Ich bin DB-Genius.
               Stell mir eine Frage über unsere Schüler-Datenbank.

               Gib 'exit' ein, um das Programm zu beenden.
               ------------------------------------------------------------
               """);

         try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
               System.out.print("Deine Frage: ");
               final String userQuery = scanner.nextLine();

               if ("exit".equalsIgnoreCase(userQuery)) {
                  System.out.println("Auf Wiedersehen!");
                  break;
               }

               // Wenn die Eingabe leer ist, überspringen wir die Verarbeitung.
               // Das Framework wirft einen Fehler (IllegalArgumentException),
               // wenn der Prompt leer ist.
               if (userQuery.isBlank()) {
                  continue;
               }

               final String agentResponse = agent.chat(userQuery);
               System.out.println("\nDB-Genius: " + agentResponse);
            }
         }

      } catch (final Exception e) {
         System.err.println("Ein Fehler ist aufgetreten: " + e.getMessage());
         e.printStackTrace();
      }
   }
}