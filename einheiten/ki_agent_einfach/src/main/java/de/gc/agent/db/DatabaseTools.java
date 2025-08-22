package de.gc.agent.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.langchain4j.agent.tool.Tool;

/**
 * Diese Klasse enthält die Werkzeuge, die unser KI-Agent verwenden
 * kann. Jede Methode, die mit @Tool annotiert ist, steht dem Agenten
 * zur Verfügung.
 */
public class DatabaseTools {

   /**
    * Dies ist das einzige Werkzeug, das wir dem Agenten geben.
    *
    * Die Beschreibung in der @Tool-Annotation ist EXTREM WICHTIG.
    *
    * Der Agent liest diesen Text, um zu verstehen, was das Werkzeug tut
    * und wann er es einsetzen soll.
    *
    * @param sqlQuery Die SQL-Anfrage, die vom Agenten generiert wurde.
    *
    * @return Ein String mit dem Ergebnis der Datenbankabfrage, formatiert
    *         als Tabelle.
    */
   @Tool("Führt eine SQL-Abfrage in der Schüler-Datenbank aus und gibt das Ergebnis zurück.")
   public String executeQuery(final String sqlQuery) {

      System.out.println("  SQL query: " + sqlQuery);

      // Wir stellen eine Verbindung zur Datenbank her - singelton.
      final Connection con = DbUtil.getConnection();

      // Wir verwenden try-with-resources, um sicherzustellen, dass die
      // Ressourcen am Ende automatisch geschlossen werden.
      try (Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery)) {

         // Wir verwenden die Hilfsmethode DbUtil.getOutputRs(), um das
         // ResultSet in eine String (Tabelle) umzuwandeln.
         final String output = DbUtil.getOutputRs(rs);

         // Wenn keine Zeilen gefunden wurden, geben wir eine freundliche
         // Nachricht zurück.
         if (output.isBlank()) {
            return "Die Abfrage hat keine Ergebnisse geliefert.";
         }

         return output;

      } catch (final SQLException e) {
         // Wenn etwas schiefgeht (z.B. ungültiges SQL), geben wir die
         // Fehlermeldung an den Agenten zurück.
         // Er kann dann versuchen, seinen Fehler zu korrigieren.
         return "Fehler bei der Ausführung der SQL-Abfrage: " + e.getMessage();
      }
   }
}