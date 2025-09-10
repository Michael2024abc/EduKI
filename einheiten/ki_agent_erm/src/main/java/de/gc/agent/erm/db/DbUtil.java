package de.gc.agent.erm.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.nocrala.tools.texttablefmt.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse bietet Hilfsfunktionen für die Verwaltung von
 * Datenbankverbindungen und die Verarbeitung von SQL-Abfragen. Sie
 * ermöglicht das Laden von Konfigurationsdaten aus einer Datei und
 * stellt eine Singleton-Verbindung zur Datenbank bereit. Weitere
 * Features sind das Schließen der Verbindung und die Darstellung von
 * Ergebnissen.
 */
public class DbUtil {

   /** Logger für die Protokollierung von Ereignissen in dieser Klasse. */
   private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);

   /** Die aktuell gehaltene Datenbankverbindung (Singleton-Muster). */
   private static Connection connection;

   /** Der Katalogname der aktuellen Datenbankverbindung. */
   private static String catalog;

   /** Die Metadaten der aktuellen Datenbankverbindung. */
   private static DatabaseMetaData meta;

   /** Der Schema-Name der aktuellen Datenbankverbindung. */
   private static String schema;

   /**
    * Prüft, ob die Datenbankverbindung initialisiert ist.
    *
    * @throws IllegalStateException Wird geworfen, falls keine Verbindung
    *                               existiert.
    */
   private static void check() {
      if (connection == null) {
         throw new IllegalStateException(
               """
                     Die Datenbankverbindung ist nicht initialisiert.
                     Rufen Sie zuerst DbUtil.getConnection() auf.
                     """);
      }
   }

   /**
    * Erstellt eine synthetische Schemadatei auf Basis der aktuellen
    * Datenbank, falls diese nicht existiert.
    *
    * @param path Pfad zur Zieldatei.
    */
   public static void checkSyntheticSchema(final Path path) {

      check();

      if (path == null || !path.toFile()
         .exists()) {

         try {
            final String schema = getSyntheticSchema();
            Files.writeString(path, schema, StandardCharsets.UTF_8);
            System.out.format("Datei %s erstellt.", path.toString());

         } catch (final Exception e) {
            throw new RuntimeException("Fehler beim Schreiben der Datei: "
                  + path.toString(), e);
         }

      }
   }

   /**
    * Schließt die Datenbankverbindung, sofern sie noch offen ist, und gibt
    * die Resourcen frei.
    */
   public static void close() {
      if (connection != null) {
         try {
            if (!connection.isClosed()) {
               connection.close();
               logger.info("Database connection closed.");
               connection = null;
            }
         } catch (final SQLException e) {
            logger.error("Error closing database connection.", e);
            throw new RuntimeException("Error closing database connection.", e);
         }
      }
   }

   /**
    * Gibt die Singleton-Verbindung zur Datenbank zurück und lädt die
    * Konfiguration aus der Datei db.properties, falls das erste Mal
    * aufgerufen.
    *
    * @return Die aktive {@link Connection} zur Datenbank.
    *
    * @throws RuntimeException Falls die Konfiguration oder die Verbindung
    *                          fehlschlägt.
    */
   public static Connection getConnection() {

      if (connection == null) {

         logger.debug("Loading database connection from db.properties...");
         final Properties dbProperties = new Properties();
         try {

            dbProperties
               .load(DbUtil.class.getResourceAsStream("/db.properties"));
            final String dbUrl = dbProperties.getProperty("db.url");
            final String dbUser = dbProperties.getProperty("db.user");
            final String dbPassword = dbProperties.getProperty("db.password");

            if (dbUrl == null || dbUser == null || dbPassword == null) {
               logger.error(
                     "Die Datei db.properties muss die Eigenschaften db.url, db.user und db.password enthalten.");
               throw new RuntimeException(
                     "Die Datei db.properties muss die Eigenschaften db.url, db.user und db.password enthalten.");
            }

            // Herstellen der Verbindung zur Datenbank
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.debug("Datenbankverbindung war erfolgreich.");

            // Ermitteln von Katalog, Schema und Metadaten der Verbindung
            catalog = connection.getCatalog();
            schema = connection.getSchema();
            meta = connection.getMetaData();

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
    * Erstellt eine formatierte ASCII-Tabelle aus den Zeilen des
    * übergebenen ResultSets.
    *
    * @param rs Das ResultSet, das die Abfrageergebnisse enthält.
    *
    * @return Der Tabelleninhalt als String im ASCII-Format.
    *
    * @throws RuntimeException Falls ein SQL-Fehler auftritt.
    */
   public static String getOutputRs(final ResultSet rs) {

      check();

      try {

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

   /**
    * Liest das Schema der aktuellen Datenbank aus und gibt eine textuelle
    * Übersicht über Tabellen, Spalten und Fremdschlüsselbeziehungen
    * zurück.
    *
    * @return Ein String, der das Schema der Datenbank beschreibt.
    *
    * @throws SQLException Falls ein Fehler beim Zugriff auf die Metadaten
    *                      auftritt.
    */
   public static String getSyntheticSchema()
         throws SQLException {

      check();

      final StringBuilder schemaBuilder = new StringBuilder();
      final DatabaseMetaData metaData = connection.getMetaData();

      // 1. Tabellen und Spalten auslesen
      final List<String> tables = getTablesNames();

      for (final String table : tables) {
         schemaBuilder.append("-- Tabelle: ")
            .append(table)
            .append("\n");
         schemaBuilder.append("-- Spalten: ");

         try (ResultSet columns = metaData.getColumns(null, null, table,
               null)) {
            boolean first = true;
            while (columns.next()) {
               if (!first) {
                  schemaBuilder.append(", ");
               }
               final String columnName = columns.getString("COLUMN_NAME");
               final String columnType = columns.getString("TYPE_NAME");
               schemaBuilder.append(columnName)
                  .append(" (")
                  .append(columnType)
                  .append(")");
               first = false;
            }
         }
         schemaBuilder.append("\n\n");
      }

      // 2. Fremdschlüsselbeziehungen auslesen
      schemaBuilder.append("-- Beziehungen (Fremdschlüssel):\n");
      for (final String table : tables) {
         try (ResultSet foreignKeys = metaData.getImportedKeys(null, null,
               table)) {
            while (foreignKeys.next()) {
               final String fkTableName = foreignKeys
                  .getString("FKTABLE_NAME");
               final String fkColumnName = foreignKeys
                  .getString("FKCOLUMN_NAME");
               final String pkTableName = foreignKeys
                  .getString("PKTABLE_NAME");
               final String pkColumnName = foreignKeys
                  .getString("PKCOLUMN_NAME");
               schemaBuilder.append(String.format("-- %s.%s -> %s.%s\n",
                     fkTableName, fkColumnName, pkTableName, pkColumnName));
            }
         }
      }

      return schemaBuilder.toString();
   }

   /**
    * Gibt alle Tabellennamen des aktuellen Katalogs und Schemas als Liste
    * zurück.
    *
    * @return Eine Liste der Tabellennamen der Datenbank.
    */
   public static List<String> getTablesNames() {

      check();

      final List<String> list = new ArrayList<>();

      try {
         final ResultSet rs = meta.getTables(catalog, schema, null,
               new String[] { "TABLE" });

         while (rs.next()) {
            list.add(rs.getString("TABLE_NAME"));
         }
      } catch (final SQLException e) {
         new RuntimeException(e);
      }

      return list;
   }

   /**
    * Privater Konstruktor: Instanzierung der Hilfsklasse ist nicht
    * erlaubt.
    */
   private DbUtil() {
      // keine Instanzierung erlaubt
   }
}
