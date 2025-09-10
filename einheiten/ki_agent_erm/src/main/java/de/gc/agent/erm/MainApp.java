package de.gc.agent.erm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;

import de.gc.agent.erm.ki.model.KiModelFactory;
import de.gc.agent.erm.service.ErmGeneratorService;
import de.gc.agent.erm.view.MainController;
import dev.langchain4j.model.chat.ChatModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Einstiegspunkt für die ERM-Editor-Anwendung mit KI-Unterstützung.
 *
 * Nutzt Picocli zur Verarbeitung der Kommandozeilenparameter und lädt
 * externe Konfigurationsdateien für die Initialisierung der KI-Modelle.
 * Die Anwendung startet JavaFX und verbindet die generierten Services
 * mit dem MainController.
 */
@Command(name = "ERM-Editor", version = "1.0", mixinStandardHelpOptions = true, description = "Startet den KI-gestützten ERM-Editor mit einer externen Konfiguration.")
public class MainApp extends Application implements Callable<Integer> {

   /**
    * Statische Variable: Service, der an die JavaFX-Startmethode übergeben
    * wird.
    */
   private static ErmGeneratorService configuredService;

   /**
    * Hauptmethode: Startet Picocli und die JavaFX-Anwendung.
    *
    * @param args Kommandozeilenargumente.
    */
   public static void main(final String[] args) {
      // Picocli verarbeitet die Argumente und ruft die 'call'-Methode auf.
      final int exitCode = new CommandLine(new MainApp()).execute(args);
      System.exit(exitCode);
   }

   @picocli.CommandLine.Parameters(index = "0", description = "Der Konfigurations-Präfix (z.B. 'ermsystem.gemini') aus der Properties-Datei.")
   private String configPrefix;

   @picocli.CommandLine.Parameters(index = "1", description = "Pfad zur Properties-Datei.")
   private File configFile;

   /**
    * Erstellt die KI-Modelle und initialisiert den zentralen Service.
    * Danach wird die JavaFX Runtime für den UI-Start getriggert.
    *
    * @return Exit-Code (0: Erfolg, 1: Fehler)
    *
    * @throws Exception Bei Initialisierungsschwierigkeiten.
    */
   @Override
   public Integer call() throws Exception {
      // 1. Konfiguration laden
      if (!configFile.exists()) {
         System.err.println(
               "Fehler: Die angegebene Konfigurationsdatei existiert nicht: "
                     + configFile.getAbsolutePath());
         return 1; // Fehlercode
      }
      final Properties configProps = new Properties();
      try (FileInputStream fis = new FileInputStream(configFile)) {
         configProps.load(fis);
      } catch (final IOException e) {
         System.err.println(
               "Fehler beim Laden der Konfigurationsdatei: " + e.getMessage());
         return 1;
      }

      // 2. KI-Modelle mithilfe der Factory erstellen
      System.out
         .println("Lade KI-Modelle mit Präfix '" + configPrefix + "'...");
      final ChatModel analysisModel = KiModelFactory
         .createFromPrefix(configProps, configPrefix, "analysis");
      final ChatModel tableModel = KiModelFactory.createFromPrefix(configProps,
            configPrefix, "table");
      final ChatModel sqlModel = KiModelFactory.createFromPrefix(configProps,
            configPrefix, "sql");
      final ChatModel tutorModel = KiModelFactory.createFromPrefix(configProps,
            configPrefix, "tutor");
      System.out.println("Alle Modelle erfolgreich erstellt.");

      // 3. Service initialisieren und in statischer Variable speichern
      configuredService = new ErmGeneratorService(analysisModel, tableModel,
            sqlModel, tutorModel);

      // 4. JavaFX-Anwendung auf dem UI-Thread starten
      Application.launch(MainApp.class);

      return 0; // Erfolgscode
   }

   /**
    * Wird von JavaFX zum Start der UI aufgerufen und setzt den Controller.
    *
    * @param stage Die JavaFX-Hauptbühne (Fenster).
    *
    * @throws IOException Bei Fehlern im UI-Layout.
    */
   @Override
   public void start(final Stage stage) throws IOException {
      if (configuredService == null) {
         System.err.println(
               "Fehler: Der ErmGeneratorService wurde nicht initialisiert. Starten Sie die Anwendung über die main-Methode.");
         Platform.exit();
         return;
      }

      final FXMLLoader fxmlLoader = new FXMLLoader(
            MainApp.class.getResource("view/MainView.fxml"));
      fxmlLoader
         .setControllerFactory(param -> new MainController(configuredService));

      final Scene scene = new Scene(fxmlLoader.load(), 1400, 900);
      scene.getStylesheets()
         .add(getClass().getResource("view/styles.css")
            .toExternalForm());

      stage.setTitle("KI-gestützter ERM-Designer V1.0");
      stage.setScene(scene);
      stage.show();
   }
}
