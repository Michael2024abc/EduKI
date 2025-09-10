package de.gc.agent.erm.view;

/**
 * Stellt statische Methoden zur Verfügung, um formatierte Hilfetexte
 * für verschiedene Themenbereiche bereitzustellen. Dies hält den
 * MainController schlank und sorgt für zentrale Pflege der Hilfetexte.
 */
public final class HelpContentProvider {

   /**
    * Liefert einen Hilfetext zur Einführung ins ERM-Diagramm.
    *
    * @return Erklärungstext zum Entity-Relationship-Modell.
    */
   public static String getErmDiagramHelp() {
      return """
            Was ist ein ERM-Diagramm?

            ERM steht für "Entity-Relationship-Modell". Es ist eine grafische Darstellung der Daten und ihrer Zusammenhänge auf einer hohen, konzeptionellen Ebene.

            - **Entity (Rechteck):** Stellt ein "Ding" oder Objekt aus der realen Welt dar, über das wir Informationen speichern wollen (z.B. "Kunde", "Produkt").

            - **Beziehung (Linie):** Zeigt, wie zwei Entitäten miteinander verbunden sind (z.B. "Kunde bestellt Produkt").

            - **Kardinalitäten (Zeichen an den Linienenden):** Geben an, WIE VIELE Entitäten an der Beziehung beteiligt sind. Beispiele:
              - "1" (genau eins)
              - "*" (viele, 0 bis unendlich)
              - "1..*" (eins bis viele)

            Dieses Diagramm ist der Bauplan für die spätere Datenbank.
            """;
   }

   /**
    * Liefert einen Hilfetext zur PlantUML-Syntax für ERM-Diagramme.
    *
    * @return Erklärungstext zur PlantUML-Schreibweise.
    */
   public static String getErmPlantUmlHelp() {
      return """
            Was ist dieser PlantUML-Code?

            PlantUML ist eine Sprache, mit der man Diagramme aus Text beschreiben kann. Der Code in diesem Feld erzeugt das ERM-Diagramm, das Sie oben sehen.

            Grundlegende Syntax:
            - `entity Name {}`: Definiert eine neue Entität.
            - `Entität1 "Kard1" -- "Kard2" Entität2 : Text`: Definiert eine Beziehung zwischen zwei Entitäten.

            Der Vorteil: Sie können den Code direkt bearbeiten! Wenn Sie eine Beziehung ändern oder eine Entität umbenennen und dann auf "Aktualisieren" klicken, wird das Diagramm sofort neu gezeichnet.
            """;
   }

   /**
    * Liefert einen Hilfetext zur Bedeutung und Umsetzung des logischen
    * Tabellenmodells.
    *
    * @return Erklärungstext zum logischen Modell und dessen
    *         Besonderheiten.
    */
   public static String getLogicalModelHelp() {
      return """
            Was ist ein logisches Tabellenmodell?

            Dies ist der nächste Schritt nach dem ERM. Es ist eine detailliertere Darstellung, die sehr nah an der finalen Datenbankstruktur ist.

            Wichtige Unterschiede zum ERM:
            - **Tabellen statt Entitäten:** Jede Entität wird zu einer Tabelle mit spezifischen Spalten und Datentypen (z.B. VARCHAR, INT).
            - **Primärschlüssel (PK):** Jede Tabelle erhält eine eindeutige ID-Spalte (`id (PK)`), um jeden Datensatz unmissverständlich zu identifizieren.
            - **Fremdschlüssel (FK):** Beziehungen werden durch Fremdschlüssel realisiert. Wenn ein Schüler zu EINER Klasse gehört (1:n), bekommt die Schülertabelle eine Spalte `klas_id (FK)`, die auf die ID der Klasse verweist.
            - **Zwischentabellen:** n:m-Beziehungen (z.B. "Ein Schüler besucht viele Kurse, ein Kurs hat viele Schüler") werden durch eine eigene Tabelle aufgelöst, die nur die Fremdschlüssel der beiden verbundenen Tabellen enthält.
            """;
   }

   /**
    * Liefert einen Hilfetext zur PlantUML-Notation für logische
    * Tabellenmodelle.
    *
    * @return Erklärung zu den Syntaxelementen des logischen
    *         PlantUML-Modells.
    */
   public static String getLogicalModelPlantUmlHelp() {
      return """
            PlantUML für das logische Modell

            Die Syntax hier ist spezifischer als beim ERM, um die Details einer Datenbanktabelle darzustellen.

            - `entity Tabellenname { ... }`: Definiert eine Tabelle.
            - `+ id (PK)`: Definiert die Spalte 'id' als Primärschlüssel (Primary Key). Das `+` bedeutet, die Spalte ist öffentlich sichtbar.
            - `--`: Trennt den Schlüsselbereich von den normalen Attributen.
            - `spaltenname: DATENTYP`: Definiert eine normale Spalte mit ihrem MariaDB-Datentyp.
            - `+ fk_id (FK)`: Definiert eine Spalte als Fremdschlüssel (Foreign Key).
            """;
   }

   /**
    * Privater Konstruktor: Diese Hilfsklasse soll nicht instanziiert
    * werden.
    */
   private HelpContentProvider() {
   }
}
