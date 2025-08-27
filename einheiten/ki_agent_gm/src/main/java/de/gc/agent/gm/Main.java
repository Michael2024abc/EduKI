package de.gc.agent.gm;

import java.io.File;

import de.gc.agent.gm.controller.AgentController;
import de.gc.agent.gm.model.AgentModel;
import de.gc.agent.gm.view.AgentView;
import de.gc.agent.ki.model.XKiLogin;
import de.gc.agent.ki.model.XKiLogin.KiSystem;
import dev.langchain4j.model.chat.ChatModel;

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

         // -----------------------------------------------------------------
         // GEMINI -> ANPASSEN
         // final String token = XKiLogin.getToken(new
         // File("/tmp/gi.properties"),
         // "ge");
         //
         // // Wir erstellen das Sprachmodell - hier für Google Gemini.
         // final ChatModel model = XKiLogin.createChatModel(KiSystem.GEMINI,
         // "gemini-1.5-flash-latest", token, null);

         // -----------------------------------------------------------------
         // GITHUB -> ANPASSEN
         final String token = XKiLogin.getToken(new File("/tmp/gi.properties"),
               "rest");

         // Wir erstellen das Sprachmodell - hier für Github Copilot.
         final ChatModel model = XKiLogin.createChatModel(KiSystem.GITHUB,
               "GPT-4.1", token, null);

         // -----------------------------------------------------------------
         // OLLAMA -> ANPASSEN
         // Wir erstellen das Sprachmodell - hier für Ollama - lokal.
         //
         // final ChatModel model = XKiLogin.createChatModel(KiSystem.OLLAMA,
         // "mistral:7b", null,
         // "http://127.0.0.1:11434");

         // ==========================================================================
         // 2. ZUSAMMENBAU DER MVC-ARCHITEKTUR
         // ==========================================================================

         // 2.1. Erstelle das Model mit der KI-Logik und dem ChatModel.
         final AgentModel agentmodel = new AgentModel(model);

         // 2.2. Erstelle die View, also das Fenster unserer Anwendung.
         final AgentView view = new AgentView();

         // 2.3. Erstelle den Controller, der Model und View verbindet.
         final AgentController controller = new AgentController(agentmodel,
               view);

         // 2.4. Initialisiere den Controller (setzt die ActionListener).
         controller.initController();

         // 2.5. Mache die View (das Fenster) sichtbar.
         view.setVisible(true);

      } catch (final Exception e) {
         System.err.println("Ein Fehler ist aufgetreten: " + e.getMessage());
         e.printStackTrace();
      }
   }
}