package de.gc.agent.gm.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import de.gc.agent.gm.db.DbUtil;

/**
 * Ein Hilfsprogramm, um aus der Datenbank das Schema für den KI Agenten
 * zu extrahieren und in eine Datei zu schreiben.
 */
public class SchemaExtractor {

   /**
    * Erstellt aus der Datenbank das entsprechende Schema für den KI
    * Agenten.
    */
   public static void main(final String[] args)
         throws SQLException, IOException {

      final Connection con = DbUtil.getConnection();

      final Path path = Paths.get("src/main/resources/gmschema.txt");
      DbUtil.checkSyntheticSchema(path);

   }
}