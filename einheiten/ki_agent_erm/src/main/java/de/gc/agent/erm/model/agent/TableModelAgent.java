package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * KI-Agent zur Umwandlung konzeptioneller ERM-Diagramme in logische
 * Tabellenmodelle.
 *
 * Die Schnittstelle übergibt ein neues konzeptionelles ERM-Modell als
 * PlantUML-Code, das nach den im perfekten Beispiel vorgegebenen Regeln
 * und Syntax automatisch in ein neues logisches PlantUML-Modell
 * transformiert wird.
 *
 * Die Antwort enthält ausschließlich den PlantUML-Code des logischen
 * Modells ohne jegliche Kommentare oder zusätzliche Erklärungen.
 */
public interface TableModelAgent {

   /**
    * Transformiert ein konzeptionelles ERM-Diagramm (PlantUML) gemäß der
    * Musterlogik ins PlantUML-Format eines logischen Tabellenmodells.
    *
    * @param ermPuml Das konzeptionelle ERM-Diagramm als PlantUML-Code.
    *
    * @return Das logische Tabellenmodell als PlantUML-Code gemäß
    *         Beispielsyntax.
    */
   @UserMessage("""
         Du bist ein PlantUML-Transformationsexperte. Deine einzige Aufgabe ist es, ein konzeptionelles ERM-Diagramm in ein logisches Tabellenmodell umzuwandeln.
         Deine bisherigen Versuche waren fehlerhaft. Du musst dich jetzt EXAKT an das folgende, perfekte Beispiel halten. Jede Abweichung von der Logik und Syntax dieses Beispiels ist ein Fehler.

         --- PERFEKTES BEISPIEL ---

         --- INPUT (Konzeptionelles ERM) ---
         @startuml
         entity Lehrkraft {}
         entity Schüler {}
         entity Klasse {}
         Klasse "1" -- "*" Schüler : besteht aus >
         Klasse "1" -- "1" Schüler : hat KS >
         Klasse "1" -- "1" Schüler : hat KSV >
         Klasse "*" -- "*" Lehrkraft : hat >
         Klasse "*" -- "1" Lehrkraft : KL >
         @enduml
         --- ENDE INPUT ---

         --- KORREKTER OUTPUT (Logisches Modell) ---
         @startuml
         entity Klasse {
           + id (PK)
           --
           name: varchar(255)
           raum: varchar(10)
           + sch_ks_id (FK)
           + sch_ksv_id (FK)
           + leh_kl_id (FK)
         }
         entity Schüler {
           + id (PK)
           --
           vorname: varchar(255)
           nachname: varchar(255)
           + klas_id (FK)
         }
         entity Lehrkraft {
           + id (PK)
           --
           vorname: varchar(255)
           nachname: varchar(255)
         }
         entity Klasse_Lehrkraft {
           + klas_id (FK) (PK)
           + leh_id (FK) (PK)
         }
         Klasse "1" -- "*" Schüler : besteht aus >
         Klasse "1" -- "1" Schüler : hat KS >
         Klasse "1" -- "1" Schüler : hat KSV >
         Klasse "1" -- "*" Klasse_Lehrkraft : hat >
         Lehrkraft "1" -- "*" Klasse_Lehrkraft : hat >
         Lehrkraft "1" -- "*" Klasse : KL >
         @enduml
         --- ENDE KORREKTER OUTPUT ---

         ANWEISUNGEN:
         1. Analysiere den neuen Input, den du erhältst.
         2. Wende die exakte Transformationslogik aus dem "PERFEKTEN BEISPIEL" an, um den neuen Input in ein logisches Modell umzuwandeln.
         3. Gib NUR den reinen PlantUML-Code für das neue logische Modell zurück. KEINE Kommentare, KEINE Erklärungen, KEINE `!include`-Anweisungen.

         --- NEUES KONSEPTIONELLES MODELL (INPUT) ---
         {{ermPuml}}
         """)
   String generateTableModel(@V("ermPuml") String ermPuml);
}
