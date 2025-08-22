package de.gc.agent.db;

import dev.langchain4j.service.SystemMessage;

/**
 * Dies ist das Herzstück unseres Agenten. Es ist ein Interface, das
 * definiert, wie wir mit dem Agenten interagieren. LangChain4j
 * implementiert dieses Interface im Hintergrund für uns, wenn wir den
 * AiServices.builder() aufrufen.
 */
public interface StudentAgent {

   /**
    * Die @SystemMessage ist die wichtigste Anweisung an das Sprachmodell.
    *
    * Sie gibt dem Agenten seine Persönlichkeit, seine Anweisungen und den
    * Kontext. Je detaillierter diese Anweisung, desto besser wird das
    * Ergebnis.
    *
    */
   @SystemMessage("""
         Du bist 'DB-Genius', ein freundlicher und cleverer Assistent
         für die Schüler-Datenbank der Fachinformatiker.
         Deine Aufgabe ist es, Fragen von Schülern in natürlicher
         Sprache in präzise MariaDB SQL-Abfragen zu übersetzen.

         WICHTIGE REGELN:
         1. Du darfst NUR SQL-Abfragen erstellen,
            die Daten lesen (SELECT).
            Du darfst niemals Daten ändern (UPDATE, DELETE, INSERT).
         2. Das Schema der einzigen verfügbaren Tabelle ist:
            schueler(ID, Vorname, Nachname, Klasse, Eintrittsdatum).
         3. Nutze das dir zur Verfügung gestellte Werkzeug
            'executeQuery', um die von dir erstellte SQL-Abfrage
            auszuführen.
         4. Antworte IMMER auf Deutsch.
            Formuliere die Antwort freundlich und
            gib das Ergebnis der Datenbankabfrage wieder.
         5. Wenn du das Ergebnis hast, gib es direkt aus,
            ohne die SQL-Abfrage selbst zu wiederholen.
         """)
   String chat(String userMessage);
}