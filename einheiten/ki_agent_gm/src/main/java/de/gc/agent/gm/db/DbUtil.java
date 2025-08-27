package de.gc.agent.gm.db;

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

   /** Der Katalog der Datenbankverbindung. */
   private static String catalog;

   /** Die Metadaten der Datenbankverbindung. */
   private static DatabaseMetaData meta;

   /** Das Schema der Datenbankverbindung. */
   private static String schema;

   /**
    * Überprüft, ob die Datenbankverbindung initialisiert ist.
    *
    * @throws IllegalStateException Wenn die Verbindung nicht initialisiert
    *                               ist.
    */
   private static void check() {
      if (connection == null) {
         throw new IllegalStateException(
               "Die Datenbankverbindung ist nicht initialisiert. "
                     + "Rufen Sie zuerst DbUtil.getConnection() auf.");
      }
   }

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
    * Schließt die Datenbankverbindung, wenn sie geöffnet ist. Setzt die
    * Verbindung auf null, um anzuzeigen, dass sie geschlossen wurde.
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
    * Stellt eine Verbindung zur Datenbank her, wenn sie noch nicht
    * besteht. Lädt die Datenbankkonfiguration aus der Datei db.properties.
    *
    * @return Eine aktive {@link Connection} zur Datenbank.
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
    * Gibt das Ergebnis eines ResulSets als formatierte Tabelle zurück.
    *
    * @param rs Das ResultSet, das die Abfrageergebnisse enthält.
    *
    * @return Ein String, der das Ergebnis der Abfrage in Tabellenform
    *         darstellt.
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
    * Gibt das Schema der Datenbank als String zurück, das die Tabellen und
    * deren Spalteninformationen enthält.
    *
    * @return Ein String, der das Schema der Datenbank beschreibt.
    *
    * @throws SQLException Wenn ein Fehler beim Zugriff auf die Metadaten
    *                      der Datenbank auftritt.
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
    * Gibt eine Liste der Tabellennamen im aktuellen Katalog und Schema
    * zurück.
    *
    * @return Eine Liste von Strings, die die Namen der Tabellen im
    *         aktuellen Katalog und Schema enthalten.
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

   private DbUtil() {
      // keine Instanzierung erlaubt
   }
}
