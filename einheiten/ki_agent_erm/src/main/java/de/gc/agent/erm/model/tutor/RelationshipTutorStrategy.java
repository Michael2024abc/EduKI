package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Konkrete Strategie-Implementierung für den Lernassistenten zur
 * Beziehungstabelle.
 *
 * Diese Klasse steuert den Tutor-Dialog für die Erfassung und Erklärung
 * von Beziehungen, leitet alle Interaktionen an den
 * {@link ErmGeneratorService} weiter und gibt passende Antworten sowie
 * Vorschlagsfragen an die Schüler zurück.
 */
public class RelationshipTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Antwortservice für Tutor-Interaktionen zur Beziehungstabelle. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor für die Beziehungstutor-Strategie.
    *
    * @param service Service zur Generierung von Antworten für Beziehungen.
    */
   public RelationshipTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Titel für die UI-Komponente des Beziehungstutors zurück.
    *
    * @return Titel als String.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent für Beziehungen";
   }

   /**
    * Liefert die initiale TutorResponse für den Dialogstart, in der Regel
    * eine Begrüßung und Einstieg in das Thema Beziehungen.
    *
    * @return TutorResponse mit Begrüßung und Vorschlägen.
    */
   @Override
   public TutorResponse getInitialResponse() {
      return service.getTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert kontext- und nutzerabhängige Antworten für die
    * Beziehungstabelle.
    *
    * @param context   Der Kontext der aktuellen Beziehungstabelle.
    * @param userInput Die Schülerfrage.
    *
    * @return TutorResponse als Antwort und Folgefragen.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getTutorResponse(context, userInput);
   }
}
