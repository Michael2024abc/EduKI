package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zur PlantUML-Syntax für
 * logische Tabellenmodelle.
 *
 * Der LogicalModelPlantUmlTutorAgent hilft Schülerinnen und Schülern zu
 * verstehen, wie mithilfe von PlantUML logische Modelle (Tabellen,
 * Spalten, Schlüssel) beschrieben werden. Er erklärt die Syntax für
 * Entitäten, Primärschlüssel, Fremdschlüssel und Spalten mit Typen.
 *
 * Die Antwort erfolgt immer als valides JSON-Objekt im vorgegebenen
 * Format: { "answer": "Erklärungstext auf Deutsch ohne Markdown.",
 * "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Bei der speziellen Frage "__INITIAL__" begrüßt der Agent und schlägt
 * Einstiegsfragen zur PlantUML-Syntax logischer Modelle vor (z.B. zu
 * Primär- und Fremdschlüsseln). Bei anderen Fragen liefert er eine
 * Antwort im Zusammenhang mit dem bereitgestellten PlantUML-Kontext.
 */
public interface LogicalModelPlantUmlTutorAgent {

   /**
    * Beantwortet Fragen zur PlantUML-Syntax für logische Tabellenmodelle,
    * wie die Definition von Entitäten, Primärschlüsseln, Fremdschlüsseln
    * und Spalten.
    *
    * @param context  PlantUML-Code des logischen Datenbankmodells.
    * @param question Die Schülerfrage (oder "__INITIAL__" für Begrüßung
    *                 und Einstiegsfragen).
    *
    * @return Valides JSON-Objekt mit Erklärung und Fragenvorschlägen.
    */
   @SystemMessage("""
         Du bist ein Tutor für Datenbankmodellierung, spezialisiert auf die PlantUML-Syntax für logische Tabellenmodelle.
         Deine Aufgabe ist es, Schülern die spezifische Syntax zu erklären, wie z.B. `entity Tabellenname { ... }`, die Definition von Primärschlüsseln `+ id (PK)`, Fremdschlüsseln `+ fk_id (FK)` und normalen Spalten mit Datentypen.
         Antworte immer auf Deutsch. Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper.
         Wenn die Frage "__INITIAL__" ist, gib eine Begrüßung und erste Fragen zur PlantUML-Syntax für logische Modelle (z.B. "Wie deklariere ich einen Primärschlüssel?").
         Ansonsten beantworte die Frage des Benutzers im Kontext des bereitgestellten PlantUML-Codes.
         """)
   @UserMessage("""
         --- KONTEXT (PlantUML des logischen Modells) ---
         {{context}}
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
