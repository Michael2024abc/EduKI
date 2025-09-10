package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zum ERM-Diagramm.
 *
 * Der ErmDiagramTutorAgent beantwortet Fragen rund um die
 * Datenbankmodellierung und das Verständnis konzeptioneller
 * ERM-Diagramme. Er erklärt Begriffe wie Entitäten, Beziehungen und
 * Kardinalitäten und gibt Antworten immer in Deutsch zurück.
 *
 * Die Antwort erfolgt immer als valides JSON-Objekt im vorgegebenen
 * Format: { "answer": "Erklärungstext auf Deutsch ohne Markdown.",
 * "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Wird die spezielle Userfrage "__INITIAL__" gestellt, begrüßt der
 * Agent und schlägt Einstiegsfragen zum ERM-Thema vor. Bei anderen
 * Fragen erfolgt die Antwort themenbezogen gemäß des bereitgestellten
 * PlantUML-Kontextes.
 */
public interface ErmDiagramTutorAgent {

   /**
    * Beantwortet Schülerfragen zu ERM-Diagrammen kontextbezogen und
    * liefert die Antwort als JSON-Objekt.
    *
    * @param context  PlantUML-Code des konzeptionellen ERM-Diagramms.
    * @param question Die konkrete Frage des Nutzers (oder "__INITIAL__").
    *
    * @return Die Antwort und Vorschläge als valides JSON-Objekt.
    */
   @SystemMessage("""
         Du bist ein Tutor für Datenbankmodellierung, spezialisiert auf konzeptionelle ERM-Diagramme.
         Deine Aufgabe ist es, Schülern die Bedeutung von Entitäten, Beziehungen und Kardinalitäten zu erklären.
         Antworte immer auf Deutsch. Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper.
         Wenn die Frage "__INITIAL__" ist, gib eine Begrüßung und erste Fragen zum Thema ERM-Diagramme.
         Ansonsten beantworte die Frage des Benutzers im Kontext des bereitgestellten PlantUML-Codes.
         """)
   @UserMessage("""
         --- KONTEXT (PlantUML des ERM-Diagramms) ---
         {{context}}
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
