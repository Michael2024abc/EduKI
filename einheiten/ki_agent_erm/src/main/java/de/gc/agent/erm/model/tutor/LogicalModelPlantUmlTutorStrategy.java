package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Strategieklasse für den Lernassistenten zur PlantUML-Syntax logischer
 * Datenbankmodelle.
 *
 * Diese Klasse steuert den Tutor-Dialog für logische Modelle auf Basis
 * einer PlantUML-Beschreibung. Die Antworten werden über den
 * {@link ErmGeneratorService} erzeugt und passend zum jeweiligen
 * Kontext und zur Eingabe des Nutzers geliefert.
 */
public class LogicalModelPlantUmlTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Service zur Generierung von Tutor-Antworten. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor für die Strategie, injiziert den Antwort-Service für
    * logische Modelle.
    *
    * @param service Service zur Erzeugung von Tutor-Antworten für logische
    *                PlantUML-Modelle.
    */
   public LogicalModelPlantUmlTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Titel für die Tutor-UI des logischen Modells aus.
    *
    * @return Titel als String.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent: Logisches Modell (PlantUML)";
   }

   /**
    * Erzeugt die initiale Tutor-Antwort, z.B. Begrüßung und
    * Einstiegsfragen, für das logische PlantUML-Modell.
    *
    * @return Die initiale TutorResponse für den Start des Dialogs.
    */
   @Override
   public TutorResponse getInitialResponse() {
      return service.getLogicalModelPlantUmlTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert eine kontextbezogene Tutor-Antwort anhand des aktuellen
    * Diagramm-Kontextes und der Nutzerfrage.
    *
    * @param context   PlantUML-Code des logischen Datenbankmodells.
    * @param userInput Die Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getLogicalModelPlantUmlTutorResponse(context, userInput);
   }
}
