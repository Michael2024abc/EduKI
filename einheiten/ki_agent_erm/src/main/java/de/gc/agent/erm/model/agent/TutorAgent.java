package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zur Datenbankmodellierung.
 *
 * Der TutorAgent beantwortet Schülerfragen rund um Datenbankkonzepte
 * und liefert die Antwort immer als valides JSON-Objekt im folgenden
 * Format: { "answer": "Erklärungstext auf Deutsch.",
 * "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Bei "__INITIAL__" folgt eine kurze Begrüßung und allgemeine Fragen
 * zur Datenbankmodellierung. Sonst wird jede Frage im Kontext der
 * bereitgestellten Tabellendaten beantwortet.
 */
public interface TutorAgent {

   /**
    * Beantwortet Schülerfragen zu Datenbankmodellierung immer freundlich,
    * geduldig und kontextsensitiv. Liefert die Antwort als JSON-Objekt mit
    * Vorschlagsfragen.
    *
    * @param context  Der aktuelle Inhalt der Beziehungstabelle als
    *                 Kontext.
    * @param question Die konkrete Schülerfrage oder "__INITIAL__" für
    *                 Begrüßung und Einstiegsfragen.
    *
    * @return Valides JSON-Objekt mit Erklärung und drei Fragenvorschlägen.
    */
   @SystemMessage("""
         Du bist ein freundlicher und geduldiger Tutor für Datenbankmodellierung. Deine Aufgabe ist es, Schülern zu helfen, die Konzepte zu verstehen.
         Antworte immer auf Deutsch.
         Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten. Sie darf NICHT in Markdown-Code-Blöcken (``````) eingeschlossen sein. Die Antwort muss mit `{` beginnen und mit `}` enden.
         Wenn der Benutzer "__INITIAL__" fragt, gib eine kurze Begrüßung und allgemeine erste Fragen zurück.
         Ansonsten beantworte die Frage des Benutzers im Kontext der bereitgestellten Tabellendaten.
         """)
   @UserMessage("""
         --- KONTEXT (Aktueller Inhalt der Beziehungstabelle) ---
         {{context}}
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
