package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zu logischen Tabellenmodellen.
 *
 * Der LogicalModelTutorAgent erklärt Schülern die Datenbank-Konzepte
 * rund um Tabellen, Primärschlüssel (PK), Fremdschlüssel (FK), sowie
 * Zwischentabellen für n:m-Beziehungen. Die Rückgabe erfolgt immer als
 * valides JSON-Objekt im vorgegebenen Format: { "answer":
 * "Erklärungstext auf Deutsch ohne Markdown.", "suggested_questions":
 * ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Bei der speziellen Frage "__INITIAL__" begrüßt der Agent und schlägt
 * Einstiegsfragen zu logischen Modellen vor (z.B. Primärschlüssel,
 * Fremdschlüssel). Bei anderen Fragen liefert er die Antwort im Kontext
 * des bereitgestellten PlantUML-Modells.
 */
public interface LogicalModelTutorAgent {

   /**
    * Beantwortet Fragen zu logischen Tabellenmodellen, z.B. zu
    * Primärschlüsseln, Fremdschlüsseln oder Zwischentabellen, immer im
    * Kontext des PlantUML-Modells.
    *
    * @param context  PlantUML-Code des logischen Datenbankmodells.
    * @param question Die Nutzerfrage (bzw. "__INITIAL__" für Begrüßung und
    *                 Einstiegsfragen).
    *
    * @return Valides JSON-Objekt mit ausführlicher Antwort und
    *         Fragenvorschlägen.
    */
   @SystemMessage("""
         Du bist ein Tutor für Datenbankmodellierung, spezialisiert auf logische Tabellenmodelle.
         Deine Aufgabe ist es, Schülern die Konzepte von Tabellen, Primärschlüsseln (PK), Fremdschlüsseln (FK) und Zwischentabellen (für n:m-Beziehungen) zu erklären.
         Antworte immer auf Deutsch. Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper.
         Wenn die Frage "__INITIAL__" ist, gib eine Begrüßung und erste Fragen zum Thema logische Modelle (z.B. "Was ist ein Primärschlüssel?").
         Ansonsten beantworte die Frage des Benutzers im Kontext des bereitgestellten PlantUML-Codes für das logische Modell.
         """)
   @UserMessage("""
         --- KONTEXT (PlantUML des logischen Modells) ---
         {{context}}
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
