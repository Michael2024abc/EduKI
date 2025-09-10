package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Analyse-Agenten, der aus einem gegebenen
 * Datenmodell-Text automatisch alle Entitäten und deren Beziehungen
 * extrahiert.
 *
 * Die Methode `analyzeRelationships` erhält eine frei formulierte
 * Beschreibung eines Datenmodells und gibt die Beziehungen als
 * formatierte Liste zurück. Das Format jeder Zeile ist:
 * Entität1|Kardinalität1|Verb|Kardinalität2|Entität2
 *
 * Besondere Regeln: - Rollen werden nicht als eigene Entität
 * ausgewertet. - Bei komplexen Kardinalitäten werden Phrasen wie
 * "maximal zwei" als "1..2" kodiert. - Jede gefundene Beziehung wird in
 * einer neuen Zeile geliefert.
 */
public interface AnalysisAgent {

   /**
    * Extrahiert alle Beziehungen zwischen Entitäten aus einer
    * Datenmodell-Beschreibung. Das Ergebnis ist eine textuelle Liste im
    * vorgegebenen Format.
    *
    * @param description Die Beschreibung des Datenmodells als Freitext.
    *
    * @return Eine Liste aller extrahierten Beziehungen gemäß dem
    *         vorgegebenen Muster.
    */
   @UserMessage("""
         Du bist ein Experte für die semantische Analyse von Datenmodell-Beschreibungen.
         Deine Aufgabe ist es, ALLE Entitäten und ihre Beziehungen zu extrahieren.
         Gib JEDE gefundene Beziehung in einer NEUEN Zeile zurück.
         Formatiere JEDE Zeile EXAKT nach diesem Muster:
         Entität1|Kardinalität1|Verb|Kardinalität2|Entität2

         BEFOLGE DIESE REGELN:
         1.  **Rollen erkennen:** Wenn ein Substantiv (z.B. "Klassensprecher") eine Rolle einer anderen Entität (z.B. "Schüler") ist, erstelle KEINE neue Entität für die Rolle.
         2.  **Mehrere Beziehungen:** Extrahiere alle unterschiedlichen Beziehungen.
         3.  **Komplexe Kardinalitäten:** Übersetze Phrasen wie "maximal zwei" in "1..2".

         Gib NUR die Liste in diesem Text-Format zurück.

         --- BESCHREIBUNG ---
         {{description}}
         """)
   String analyzeRelationships(@V("description") String description);
}
