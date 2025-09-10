package de.gc.agent.erm.model.tutor;

/**
 * Definiert den Vertrag für die Tutor-Logik.
 *
 * Durch diese Schnittstelle kann die Benutzeroberfläche vom konkreten
 * Fachgebiet des Tutors entkoppelt werden. Jede konkrete Strategie
 * liefert einen eigenen Titel, die erste Begrüßungsantwort und die
 * Folgeantworten basierend auf Kontext und Nutzereingabe.
 */
public interface TutorStrategy {

   /**
    * Gibt den anzuzeigenden Titel für das Tutor-Dialogfenster zurück.
    *
    * @return Der Titel des Tutor-Dialogs als String.
    */
   String getDialogTitle();

   /**
    * Liefert die initiale Begrüßung und die ersten Vorschlagsfragen.
    *
    * @return Eine TutorResponse mit der Willkommensnachricht.
    */
   TutorResponse getInitialResponse();

   /**
    * Gibt eine Antwort des Tutors auf Basis der Nutzereingabe und des
    * aktuellen Kontextes zurück.
    *
    * @param context   Die aktuellen Kontextdaten (z.B. Tabelleninhalt als
    *                  String).
    * @param userInput Die vom Nutzer gestellte Frage.
    *
    * @return Eine TutorResponse mit Antwort und neuen Vorschlägen.
    */
   TutorResponse getResponse(String context, String userInput);
}
