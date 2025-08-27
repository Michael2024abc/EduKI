package de.gc.agent.gm.model;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gc.agent.gm.db.DbUtil;
import dev.langchain4j.agent.tool.Tool;

/**
 * Diese Klasse enthält Werkzeuge (Tools), die ein KI-Agent verwenden
 * kann, um mit der Datenbank zu interagieren. Hier definieren wir die
 * Fähigkeit, Daten als CSV-Datei zu exportieren.
 */
public class DatabaseTools {

   private static final Logger logger = LoggerFactory
      .getLogger(DatabaseTools.class);

   /**
    * Exportiert das Ergebnis einer gegebenen SQL-Abfrage in eine
    * CSV-Datei. Diese Methode ist mit @Tool annotiert, damit LangChain4j
    * sie als Fähigkeit für einen Agenten erkennen kann.
    *
    * @param sqlQuery  Die SQL-SELECT-Abfrage, deren Ergebnis exportiert
    *                  werden soll.
    * @param dateiname Der Name der Zieldatei (z.B. "kunden.csv").
    *
    * @return Eine Erfolgs- oder Fehlermeldung als String.
    */
   @Tool("Exportiert das Ergebnis einer gegebenen SQL-Abfrage in eine CSV-Datei.")
   public String exportiereAbfrageAlsCsv(final String sqlQuery,
         final String dateiname) {

      logger.info("CSV-Export angefordert für Datei '{}' mit Abfrage: {}",
            dateiname, sqlQuery);

      // 1. Sicherheitscheck: Nur SELECT-Abfragen erlauben.
      // Dies ist eine wichtige Schutzmaßnahme, um zu verhindern, dass der
      // Agent schreibende Befehle (UPDATE, DELETE etc.) ausführt.
      if (!sqlQuery.trim()
         .toLowerCase()
         .startsWith("select")) {
         logger.warn(
               "CSV-Export verweigert: Es sind nur SELECT-Abfragen erlaubt.");
         return "Fehler: Nur SELECT-Abfragen dürfen exportiert werden.";
      }

      // 2. Verbindung zur Datenbank herstellen.
      final Connection con = DbUtil.getConnection();

      // CSV-Format
      final CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
         .setDelimiter(';')
         .setQuote('"')
         .setQuoteMode(QuoteMode.ALL)
         .build();

      try (PreparedStatement stmt = con.prepareStatement(sqlQuery);
            ResultSet rs = stmt.executeQuery();
            FileWriter out = new FileWriter(dateiname);
            final CSVPrinter printer = new CSVPrinter(out, format);) {

         printer.printHeaders(rs);
         printer.printRecords(rs);

         logger.info("Daten erfolgreich nach {} exportiert.", dateiname);
         return "Daten erfolgreich nach " + dateiname + " exportiert.";

      } catch (final SQLException e) {
         logger.error("SQL-Fehler beim CSV-Export.", e);
         return "Fehler beim Ausführen der SQL-Abfrage: " + e.getMessage();
      } catch (final IOException e) {
         logger.error("IO-Fehler beim Schreiben der CSV-Datei.", e);
         return "Fehler beim Schreiben der CSV-Datei: " + e.getMessage();
      }
   }
}