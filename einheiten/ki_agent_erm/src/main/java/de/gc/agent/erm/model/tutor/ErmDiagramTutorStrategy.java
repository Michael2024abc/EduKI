package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Strategieklasse für den Lernassistenten zum Thema ERM-Diagramme.
 *
 * Diese Klasse leitet alle Tutor-Dialoge an den
 * {@link ErmGeneratorService} weiter, um Antworten auf Schülerfragen
 * rund um konzeptionelle ERM-Diagramme zu erzeugen. Sie stellt einen
 * Titel für die Dialog-UI bereit und generiert initiale und darauf
 * folgende Antworten für die Interaktion mit dem Schüler.
 */
public class ErmDiagramTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Service zur Antwortgenerierung für ERM-Diagramm-Fragen. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor, der den Generator-Service für ERM-Diagramm-Fragen
    * injiziert.
    *
    * @param service Service zur Generierung von Tutor-Antworten.
    */
   public ErmDiagramTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Dialogtitel für die Tutoransicht zum ERM-Diagramm zurück.
    *
    * @return Titel als String.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent: ERM-Diagramm";
   }

   /**
    * Gibt die initiale Antwort für den Tutor-Dialog zurück, typischerweise
    * mit einer Begrüßung und Einstiegsfragen.
    *
    * @return Die initiale TutorResponse für den Einstieg.
    */
   @Override
   public TutorResponse getInitialResponse() {
      return service.getErmDiagramTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert eine Tutor-Antwort basierend auf Kontext und Benutzereingabe.
    *
    * @param context   Kontext des aktuellen Diagramms (PlantUML).
    * @param userInput Die Eingabe des Schülers.
    *
    * @return Eine TutorResponse als Antwort auf die Eingabe.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getErmDiagramTutorResponse(context, userInput);
   }
}
