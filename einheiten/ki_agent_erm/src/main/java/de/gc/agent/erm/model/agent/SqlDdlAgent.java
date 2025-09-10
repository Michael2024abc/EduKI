package de.gc.agent.erm.model.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Schnittstelle für einen Experten-Agenten zur SQL-DDL-Generierung aus
 * logischen PlantUML-Modellen.
 *
 * Der SqlDdlAgent wandelt ein logisches PlantUML-Datenmodell in ein
 * lauffähiges, didaktisch kommentiertes SQL-DDL-Skript
 * (MariaDB-Dialekt) um. Dabei werden alle Tabellen zuerst mit `CREATE
 * TABLE` und Primärschlüssel erstellt, Fremdschlüssel aus didaktischen
 * Gründen separat am Ende als `ALTER TABLE ... ADD CONSTRAINT`
 * definiert.
 *
 * Jeder Block wird mit deutschsprachigen Kommentaren versehen. Es
 * werden nur gültige, sinnvolle Datentypen verwendet. Keine
 * DROP/USE-Anweisungen, nur reine Tabellen- und Beziehungsdefinitionen.
 */
public interface SqlDdlAgent {

   /**
    * Generiert ein sauberes, didaktisch kommentiertes SQL-DDL-Skript
    * (MariaDB) aus einem logischen PlantUML-Modell. Die Tabellenerstellung
    * und Fremdschlüsseldefinition erfolgen strikt nach
    * Zwei-Phasen-Prinzip.
    *
    * @param logicalPuml Der PlantUML-Code des logischen Modells als
    *                    Eingabe.
    *
    * @return Ein SQL-DDL-Skript, das alle CREATE TABLE- und ALTER
    *         TABLE-Befehle samt Kommentaren enthält.
    */
   @SystemMessage("""
         Du bist ein Experte für die Umwandlung von logischen PlantUML-Datenmodellen in standardkonformes SQL (MariaDB-Dialekt).
         Deine Aufgabe ist es, ein sauberes, didaktisch wertvolles und lauffähiges DDL-Skript zu erstellen.
         Befolge diese Regeln strikt:
         1.  **Zwei-Phasen-Erstellung:** Erstelle IMMER zuerst alle Tabellen mit `CREATE TABLE`, inklusive Primärschlüssel (PK).
         2.  **Fremdschlüssel später:** Füge Fremdschlüssel (FK) AUSSCHLIESSLICH am Ende des Skripts mit separaten `ALTER TABLE ... ADD CONSTRAINT` Anweisungen hinzu. Dies vermeidet Fehler bei zirkulären Abhängigkeiten.
         3.  **Didaktische Kommentare:** Füge über JEDEM `CREATE TABLE` und JEDEM `ALTER TABLE` Block einen kurzen, erklärenden Kommentar auf Deutsch hinzu (z.B. `-- Tabelle für die Stammdaten der Schüler erstellen.`).
         4.  **Keine Zusatz-Anweisungen:** Erzeuge KEINE `DROP TABLE` oder `USE DATABASE` Anweisungen. Gib nur die reinen `CREATE TABLE` und `ALTER TABLE` Befehle mit Kommentaren zurück.
         5.  **Datentypen:** Verwende sinnvolle MariaDB-Datentypen wie `INT`, `VARCHAR(255)`, `DATE`, `TIMESTAMP`, etc. Primärschlüssel sind `INT`, `AUTO_INCREMENT`.
         """)
   @UserMessage("""
         --- NEUES LOGISCHES MODELL (INPUT) ---
         {{logicalPuml}}
         """)
   String generateSqlDdl(@V("logicalPuml") String logicalPuml);
}
