package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zu SQL DDL (Data Definition
 * Language).
 *
 * Der SqlDdlTutorAgent erklärt Schülerinnen und Schülern die Bedeutung
 * und Anwendung von SQL-DDL-Befehlen wie `CREATE TABLE`, `ALTER TABLE`,
 * sowie den Umgang mit Primär- und Fremdschlüsseln und Datentypen. Die
 * Antwort erfolgt stets als valides JSON-Objekt: { "answer":
 * "Erklärungstext auf Deutsch ohne Markdown.", "suggested_questions":
 * ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Bei der Frage "__INITIAL__" folgt eine Begrüßung und grundlegende
 * Fragen rund um SQL-DDL. Sonst liefert der Agent eine kontextbezogene
 * Antwort zum bereitgestellten SQL-DDL-Skript.
 */
public interface SqlDdlTutorAgent {

   /**
    * Beantwortet Fragen zur SQL-DDL und erklärt Begriffe wie CREATE TABLE,
    * ALTER TABLE, Primär-/Fremdschlüssel und Datentypen, stets bezogen auf
    * das bereitgestellte Skript.
    *
    * @param context  Das generierte SQL-DDL-Skript als Kontext für die
    *                 Antwort.
    * @param question Die Frage des Nutzers (oder "__INITIAL__" für
    *                 Begrüßung/Einstiegsfragen).
    *
    * @return Valides JSON-Objekt mit Erklärung und Fragenvorschlägen.
    */
   @SystemMessage("""
         Du bist ein Tutor für Datenbanken, spezialisiert auf SQL DDL (Data Definition Language).
         Deine Aufgabe ist es, Schülern die Bedeutung von `CREATE TABLE`, `ALTER TABLE`, Primär- und Fremdschlüsseln sowie Datentypen zu erklären.
         Antworte immer auf Deutsch. Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper.
         Wenn die Frage "__INITIAL__" ist, gib eine Begrüßung und erste Fragen zum Thema SQL DDL (z.B. "Was bedeutet `CREATE TABLE`?").
         Ansonsten beantworte die Frage des Benutzers im Kontext des bereitgestellten SQL-Skripts.
         """)
   @UserMessage("""
         --- KONTEXT (Generiertes SQL-DDL-Skript) ---
         {{context}}
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
