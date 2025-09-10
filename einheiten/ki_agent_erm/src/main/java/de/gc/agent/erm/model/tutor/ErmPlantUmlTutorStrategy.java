package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Strategieklasse für den Lernassistenten zum Thema ERM PlantUML-Code.
 *
 * Diese Klasse leitet die Tutor-Dialoge an den
 * {@link ErmGeneratorService} weiter, um Antworten auf Schülerfragen
 * rund um die PlantUML-Syntax konzeptioneller ERM-Diagramme zu
 * erzeugen. Sie bietet einen spezifischen Dialogtitel für die UI und
 * liefert initiale wie kontextabhängige Antworten.
 */
public class ErmPlantUmlTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Service zur Antwortgenerierung für PlantUML-Fragen. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor, der den Generator-Service für PlantUML-Fragen injiziert.
    *
    * @param service Service zur Erstellung von Tutor-Antworten.
    */
   public ErmPlantUmlTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Titel für den Tutor-Dialog zum PlantUML-Code zurück.
    *
    * @return Titel des Dialogs als String.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent: ERM PlantUML-Code";
   }

   /**
    * Gibt die initiale Tutor-Antwort zurück, z.B. zur Begrüßung und mit
    * ersten Fragen.
    *
    * @return Die initiale TutorResponse für den Einstieg.
    */
   @Override
   public TutorResponse getInitialResponse() {
      return service.getErmPlantUmlTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert eine Tutor-Antwort basierend auf Kontext und Benutzereingabe.
    *
    * @param context   Kontext des aktuellen PlantUML-Diagramms.
    * @param userInput Die Eingabe des Schülers.
    *
    * @return Eine TutorResponse als Antwort auf die Eingabe.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getErmPlantUmlTutorResponse(context, userInput);
   }
}
