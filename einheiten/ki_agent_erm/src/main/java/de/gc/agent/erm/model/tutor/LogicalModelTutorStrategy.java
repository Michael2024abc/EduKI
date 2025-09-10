package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Strategieklasse für den Lernassistenten für logische
 * Datenbankmodelle.
 *
 * Ermöglicht die geführte Interaktion mit Schülerinnen und Schülern zu
 * den Themen Tabellen, Primärschlüssel, Fremdschlüssel und
 * Zwischentabellen im Kontext logischer Modellierung. Leitet alle
 * Anfragen an den {@link ErmGeneratorService} weiter, der die
 * eigentlichen Antworten generiert.
 */
public class LogicalModelTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Service zur Generierung von Antworten auf Schülerfragen. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor für die Strategie des logischen Modell-Tutors.
    *
    * @param service Service zur Antwortgenerierung für logische
    *                Modellfragen.
    */
   public LogicalModelTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Titel für die UI-Komponente des logischen Modell-Tutors aus.
    *
    * @return Titel des Lernassistenten.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent: Logisches Modell";
   }

   /**
    * Liefert die initiale Tutor-Antwort, üblicherweise eine Begrüßung und
    * Einstiegsfragen. Bei Start des Dialogs wird ein leerer Kontext
    * übergeben.
    *
    * @return TutorResponse mit Begrüßung und Vorschlägen.
    */
   @Override
   public TutorResponse getInitialResponse() {
      // Der Kontext ist bei der Initialisierung leer, der Agent gibt eine
      // allgemeine Begrüßung zurück.
      return service.getLogicalModelTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert eine Antwort des Lernassistenten basierend auf Kontext und
    * Nutzerinput.
    *
    * @param context   Kontext des logischen Modells (PlantUML).
    * @param userInput Schülerfrage.
    *
    * @return TutorResponse mit Antwort und angemessenen Fragenvorschlägen.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getLogicalModelTutorResponse(context, userInput);
   }
}
