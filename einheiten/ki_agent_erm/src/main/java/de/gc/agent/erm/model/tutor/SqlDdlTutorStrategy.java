package de.gc.agent.erm.model.tutor;

import de.gc.agent.erm.service.ErmGeneratorService;

/**
 * Strategieklasse für den Lernassistenten zum Thema SQL DDL (Data
 * Definition Language).
 *
 * Ermöglicht geführte Tutor-Dialoge zu SQL-DDL-Befehlen wie CREATE
 * TABLE, ALTER TABLE, Primärschlüssel, Fremdschlüssel und Datentypen im
 * Kontext bereitgestellter SQL-Skripte. Sämtliche Interaktionen werden
 * an den {@link ErmGeneratorService} weitergeleitet, der die passenden
 * Antworten erzeugt.
 */
public class SqlDdlTutorStrategy implements TutorStrategy {

   /** Initiale Systemfrage für den Tutor-Dialog. */
   private static final String INITIAL_QUESTION = "__INITIAL__";

   /** Service zur Generierung von Tutor-Antworten für SQL-DDL-Fragen. */
   private final ErmGeneratorService service;

   /**
    * Konstruktor für die SQL-DDL-Tutor-Strategie.
    *
    * @param service Service zur Antwortgenerierung für SQL-DDL-Themen.
    */
   public SqlDdlTutorStrategy(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Gibt den Titel für die UI-Komponente des SQL-DDL-Tutors zurück.
    *
    * @return Titel als String.
    */
   @Override
   public String getDialogTitle() {
      return "Lernassistent: SQL DDL";
   }

   /**
    * Liefert die initiale Tutor-Antwort, üblicherweise Begrüßung und
    * Einstiegsfragen, für das SQL DDL-Thema.
    *
    * @return Die initiale TutorResponse für den Einstieg.
    */
   @Override
   public TutorResponse getInitialResponse() {
      return service.getSqlDdlTutorResponse("", INITIAL_QUESTION);
   }

   /**
    * Liefert eine Tutor-Antwort basierend auf Kontext und Nutzerfrage zum
    * SQL DDL.
    *
    * @param context   Kontext des aktuellen SQL-DDL-Skripts.
    * @param userInput Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   @Override
   public TutorResponse getResponse(final String context,
         final String userInput) {
      return service.getSqlDdlTutorResponse(context, userInput);
   }
}
