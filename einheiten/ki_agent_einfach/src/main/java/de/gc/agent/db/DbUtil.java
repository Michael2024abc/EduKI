package de.gc.agent.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import org.nocrala.tools.texttablefmt.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse bietet Hilfsfunktionen für die Datenbankverbindung und
 * die Verarbeitung von SQL-Abfragen. Sie ermöglicht das Laden der
 * Datenbankkonfiguration aus einer Datei und stellt eine
 * Singleton-Verbindung zur Datenbank bereit.
 */
public class DbUtil {

   /** Logger für die Protokollierung von Ereignissen in dieser Klasse. */
   private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);

   /** Die Datenbankverbindung, die von dieser Klasse verwaltet wird. */
   private static Connection connection;

   /**
    * Schließt die Datenbankverbindung, wenn sie geöffnet ist. Setzt die
    * Verbindung auf null, um anzuzeigen, dass sie geschlossen wurde.
    */
   public static void close() {
      if (connection != null) {
         try {
            if (!connection.isClosed()) {
               connection.close();
               logger.info("Database connection closed.");
               // Setze die Verbindung auf null, um sie als geschlossen zu
               // markieren.
               connection = null;
            }
         } catch (final SQLException e) {
            logger.error("Error closing database connection.", e);
            throw new RuntimeException("Error closing database connection.", e);
         }
      }
   }

   /**
    * Stellt eine Verbindung zur Datenbank her, wenn sie noch nicht
    * besteht. Lädt die Datenbankkonfiguration aus der Datei db.properties.
    *
    * @return Eine aktive {@link Connection} zur Datenbank.
    */
   public static Connection getConnection() {

      // Überprüfen, ob die Verbindung bereits besteht
      if (connection == null) {

         // Wenn die Verbindung nicht besteht, versuchen wir, sie zu laden
         logger.debug("Loading database connection from db.properties...");
         final Properties dbProperties = new Properties();
         try {

            // Laden der Datenbankkonfiguration aus der Datei db.properties
            dbProperties
               .load(DbUtil.class.getResourceAsStream("/db.properties"));
            final String dbUrl = dbProperties.getProperty("db.url");
            final String dbUser = dbProperties.getProperty("db.user");
            final String dbPassword = dbProperties.getProperty("db.password");

            // Überprüfen, ob die erforderlichen Eigenschaften gesetzt sind
            if (dbUrl == null || dbUser == null || dbPassword == null) {
               logger.error(
                     "Die Datei db.properties muss die Eigenschaften db.url, db.user und db.password enthalten.");
               throw new RuntimeException(
                     "Die Datei db.properties muss die Eigenschaften db.url, db.user und db.password enthalten.");
            }

            // Herstellen der Verbindung zur Datenbank
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.debug("Datenbankverbindung war erfolgreich.");

            // Registrieren eines Shutdown-Hooks, um die Verbindung zu
            // schließen, wenn die VM beendet wird.
            Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> { DbUtil.close(); }));

         } catch (final IOException e) {
            logger.error("Fehler beim Laden der Datenbankkonfiguration", e);
            throw new RuntimeException(
                  "Fehler beim Laden der Datenbankkonfiguration.", e);
         } catch (final SQLException e) {
            logger.error("Fehler beim Herstellen der Datenbankverbindung.", e);
            throw new RuntimeException(
                  "Fehler beim Herstellen der Datenbankverbindung.", e);
         }
      }

      return connection;
   }

   /**
    * Gibt das Ergebnis eines ResulSets als formatierte Tabelle zurück.
    *
    * @param rs Das ResultSet, das die Abfrageergebnisse enthält.
    *
    * @return Ein String, der das Ergebnis der Abfrage in Tabellenform
    *         darstellt.
    */
   public static String getOutputRs(final ResultSet rs) {

      try {

         // Ermittelt die Metadaten des ResultSets,
         // um die Spalteninformationen zu erhalten.
         final ResultSetMetaData rsmeta = rs.getMetaData();
         final int cols = rsmeta.getColumnCount();
         final Table t = new Table(cols);

         // Fügt die Spaltenüberschriften zur Tabelle hinzu.
         for (int i = 1; i <= cols; i++) {
            t.addCell(rsmeta.getColumnLabel(i));
         }

         // Fügt die Datenzeilen zur Tabelle hinzu.
         while (rs.next()) {
            for (int i = 1; i <= cols; i++) {
               final Object obj = rs.getObject(i);
               t.addCell(obj == null ? "" : obj.toString());
            }
         }
         return t.render();

      } catch (final SQLException e) {
         throw new RuntimeException(e);
      }
   }

   private DbUtil() {
      // keine Instanzierung erlaubt
   }

}
