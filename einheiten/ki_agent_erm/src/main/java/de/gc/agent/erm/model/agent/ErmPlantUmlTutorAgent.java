package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Tutor-Agenten zur PlantUML-Syntax von
 * ERM-Diagrammen.
 *
 * Der ErmPlantUmlTutorAgent hilft Schülerinnen und Schülern dabei zu
 * verstehen, wie aus einer gegebenen PlantUML-Beschreibung ein
 * ERM-Diagramm entsteht. Er erklärt gezielt die PlantUML-Syntax und
 * deren Wirkung auf die visuelle Darstellung.
 *
 * Die Antwort erfolgt immer als valides JSON-Objekt im vorgegebenen
 * Format: { "answer": "Erklärungstext auf Deutsch ohne Markdown.",
 * "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"] }
 *
 * Bei der speziellen Frage "__INITIAL__" begrüßt der Agent und schlägt
 * Einstiegsfragen zur PlantUML-Syntax vor. Bei anderen Fragen liefert
 * er eine Antwort im Zusammenhang mit dem bereitgestellten
 * PlantUML-Kontext.
 */
public interface ErmPlantUmlTutorAgent {

   /**
    * Beantwortet Fragen zur PlantUML-Syntax und erklärt, wie der Text das
    * Diagramm erzeugt.
    *
    * @param context  PlantUML-Code des ERM-Diagramms.
    * @param question Die konkrete Schülerfrage (oder "__INITIAL__").
    *
    * @return Valides JSON-Objekt mit Erklärung und Fragenvorschlägen.
    */
   @SystemMessage("""
         Du bist ein Tutor für Datenbankmodellierung, spezialisiert auf die PlantUML-Syntax für ERM-Diagramme.
         Deine Aufgabe ist es, Schülern zu erklären, wie der Text-Code das gezeigte Diagramm erzeugt.
         Antworte immer auf Deutsch. Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle Antwort hier. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Frage 1", "Frage 2", "Frage 3"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper.
         Wenn die Frage "__INITIAL__" ist, gib eine Begrüßung und erste Fragen zur PlantUML-Syntax.
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
