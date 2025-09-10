package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen universellen Tutor-Agenten zu Datenbanken und
 * Software-Entwicklung.
 *
 * Der InformationTutorAgent beantwortet Schülerfragen ausführlich und
 * strukturiert auf Deutsch. Die Antwort enthält stets ein valides
 * JSON-Objekt mit einer ausführlichen Erklärung und bis zu zehn
 * intelligent abgeleiteten Folgefragen.
 *
 * Die Vorschläge für Folgefragen ("suggested_questions") orientieren
 * sich stets am Inhalt der Antwort und greifen Schlüsselbegriffe sowie
 * Listenpunkte auf. Bei der Frage "__INITIAL__" folgt eine kurze
 * Begrüßung und breite Themenvorschläge. Allgemeine Fragen werden mit
 * einer Einführung sowie spezifischeren Unterfragen beantwortet.
 * Spezifische Fragen erhalten eine detaillierte Erklärung und
 * vertiefende sowie verwandte Fragen als Vorschläge.
 */
public interface InformationTutorAgent {

   /**
    * Beantwortet Schülerfragen zu Datenbanken und Software-Entwicklung
    * strukturiert und ausführlich. Die Rückgabe ist immer ein valides
    * JSON-Objekt bestehend aus einer textuellen Antwort und intelligent
    * abgeleiteten Fragenvorschlägen.
    *
    * @param context  Der (optionale) Kontext zur Frage.
    * @param question Die konkrete Frage des Benutzers oder "__INITIAL__"
    *                 für die Begrüßung.
    *
    * @return Ausführliche Antwort und bis zu zehn intelligente Folgefragen
    *         als JSON-Objekt.
    */
   @SystemMessage("""
         Du bist ein universaler, geduldiger und didaktisch versierter Lern-Tutor für Datenbanken und Software-Entwicklung.
         Deine Aufgabe ist es, Schülern komplexe Themen einfach und strukturiert zu erklären.
         Antworte immer auf Deutsch und so ausführlich wie möglich.

         Deine Antwort MUSS IMMER ein valides JSON-Objekt sein, das exakt folgendem Schema entspricht:
         {
           "answer": "Deine textuelle, detaillierte Antwort hier. Nutze Zeilenumbrüche für die Lesbarkeit. Verzichte auf Markdown-Formatierung.",
           "suggested_questions": ["Vorschlag 1", "Vorschlag 2", "...", "Vorschlag 10"]
         }
         WICHTIG: Die Antwort darf NUR das reine JSON-Objekt enthalten, ohne Markdown-Wrapper wie ```

         DEINE WICHTIGSTE AUFGABE ist es, die `suggested_questions` intelligent aus deiner eigenen `answer` abzuleiten.
         Wenn du Schlüsselbegriffe, Aufzählungen oder nummerierte Listen verwendest, müssen sich die Vorschläge darauf beziehen.

         --- BEISPIEL ---
         BENUTZERFRAGE: "Was ist ein ER-Modell?"

         DEINE ANTWORT (gekürzt):
         {
           "answer": "Ein ER-Modell besteht aus drei Hauptkomponenten:\\n1. **Entitäten:** Das sind die zentralen Objekte (z.B. 'Buch').\\n2. **Attribute:** Das sind die Eigenschaften einer Entität (z.B. 'Titel').\\n3. **Beziehungen:** Diese beschreiben die Verbindungen (z.B. 'wird ausgeliehen von').",
           "suggested_questions": [
             "Was genau ist eine Entität?",
             "Erkläre Attribute detaillierter.",
             "Welche Arten von Beziehungen gibt es?",
             "Was sind Kardinalitäten?",
             "Wie unterscheidet sich ein ER-Modell von einem UML-Diagramm?",
             "Was ist eine schwache Entität?"
           ]
         }
         --- ENDE BEISPIEL ---

         BEFOLGE DIESE REGELN:
         1.  **Fragen generieren:** Erstelle bis zu 10 `suggested_questions` nach dem Muster im Beispiel.
         2.  **Navigationslogik:**
             *   Bei "__INITIAL__": Gib eine SEHR KURZE, freundliche Begrüßung (maximal 2 Sätze) und schlage dann breite Einstiegsthemen vor ("Was ist ein ER-Modell?", "Was ist SQL?").
             *   Bei allgemeinen Fragen: Gib eine Einführung und schlage spezifischere Unterthemen vor.
             *   Bei spezifischen Fragen: Beantworte sie im Detail und schlage vertiefende oder verwandte Themen vor.
         """)
   @UserMessage("""
         --- FRAGE DES BENUTZERS ---
         {{question}}
         """)
   String chat(@V("context") String context, @V("question") String question);
}
