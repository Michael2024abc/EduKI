package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Eine universelle Tutor-Strategie, die nicht an einen bestimmten
 * UI-Kontext gebunden ist. Sie dient der allgemeinen Wissensvermittlung
 * zu Datenbank- und Modellierungsthemen.
 */
public class InformationTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den universellen Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /**
    * Service zur Generierung von Antworten für allgemeine Tutor-Fragen.
    */
   private final ErmGeneratorService service;

   /**
    * Konstruktor für die InformationTutorStrategy.
    *
    * @param service Service zur Tutor-Antwortgenerierung für allgemeine
    *                Wissensfragen.
    */
   public InformationTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Liefert den Dialogtitel für die UI-Komponente des Wissens-Tutors.
    *
    * @return Titel des Tutors als String.
    */
   @Override
   public String getDialogTitle() {
      return "Wissens-Tutor";
   }

   /**
    * Liefert die initiale Tutor-Antwort bei Start des Dialogs. Hier ist
    * kein spezieller Kontext notwendig.
    *
    * @return Die initiale TutorResponse, z.B. Begrüßung und Vorschläge.
    */
   @Override
   public TutorResponse getInitialResponse() {
      // Der Kontext ist leer, da der Tutor universell ist.
      return service.getInformationTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Generiert eine Tutor-Antwort unabhängig vom Kontext basierend auf der
    * Frage des Schülers.
    *
    * @param context   Kontext (wird ignoriert).
    * @param userInput Die Schülerfrage.
    *
    * @return TutorResponse mit Antwort und Vorschlägen.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      // Der Kontext wird ignoriert, da der Tutor universell ist.
      return service.getInformationTutorResponse(context, userInput);
   }
}
