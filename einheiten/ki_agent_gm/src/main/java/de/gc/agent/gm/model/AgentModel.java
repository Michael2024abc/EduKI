package de.gc.agent.gm.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gc.agent.gm.db.DbUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;

/**
 * Das Model in der MVC-Architektur. Es enthält die gesamte
 * Anwendungslogik:
 * <ul>
 * <li>Kommunikation mit dem Sprachmodell (LLM)</li>
 * <li>Laden des Datenbankschemas</li>
 * <li>Ausführen von SQL-Abfragen</li>
 * <li>Bereitstellung der Daten für die View (GUI)</li>
 * </ul>
 */
public class AgentModel {

   /**
    * Interface, das den CSV-Export-Agenten definiert. LangChain4j erstellt
    * daraus eine Implementierung, die das DatabaseTools-Werkzeug nutzen
    * kann.
    */
   interface CsvExportAgent {
      String chat(String userMessage);
   }

   private static final Logger logger = LoggerFactory
      .getLogger(AgentModel.class);
   private final ChatModel chatModel;
   private final String dbSchema;
   private final CsvExportAgent exportAgent;

   /**
    * Konstruktor für das AgentModel.
    *
    * @param chatModel Das initialisierte Sprachmodell (z.B. Ollama).
    */
   public AgentModel(final ChatModel chatModel) {
      this.chatModel = chatModel;
      this.dbSchema = loadSchemaFromFile("gmschema.txt");
      this.exportAgent = AiServices.builder(CsvExportAgent.class)
         .chatModel(chatModel)
         .tools(new DatabaseTools())
         .build();
   }

   private String cleanAndFormatSql(final String rawSql) {
      String cleanedSql = rawSql.replaceAll("(?i)```sql\\s*", "")
         .replaceAll("```", "");
      cleanedSql = cleanedSql.trim();
      cleanedSql = cleanedSql.replaceAll("\\s+", " ");
      cleanedSql = cleanedSql.replaceAll("(?i) FROM ", "\nFROM ")
         .replaceAll("(?i) WHERE ", "\nWHERE ")
         .replaceAll("(?i) GROUP BY ", "\nGROUP BY ")
         .replaceAll("(?i) HAVING ", "\nHAVING ")
         .replaceAll("(?i) ORDER BY ", "\nORDER BY ")
         .replaceAll("(?i) LEFT JOIN ", "\nLEFT JOIN ")
         .replaceAll("(?i) RIGHT JOIN ", "\nRIGHT JOIN ")
         .replaceAll("(?i) INNER JOIN ", "\nINNER JOIN ");
      return cleanedSql;
   }

   /**
    * Führt eine SQL-Abfrage aus und konvertiert das Ergebnis in ein
    * TableModel für die JTable.
    *
    * @param sqlQuery Die auszuführende SQL-Abfrage.
    *
    * @return Ein TableModel mit den Ergebnissen.
    *
    * @throws SQLException bei Fehlern während der Datenbankabfrage.
    */
   public TableModel executeSql(final String sqlQuery) throws SQLException {
      logger.info("Führe SQL-Abfrage aus: {}", sqlQuery);
      if (!sqlQuery.trim()
         .toLowerCase()
         .startsWith("select")) {
         throw new SQLException(
               "Nur SELECT-Abfragen sind zur Ausführung erlaubt.");
      }
      final Connection con = DbUtil.getConnection();
      try (Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery)) {
         return resultSetToTableModel(rs);
      }
   }

   /**
    * Lässt die KI einen gegebenen SQL-Befehl erklären. Die KI wird
    * angewiesen, die Ausgabe als HTML zu formatieren.
    *
    * @param sqlQuery Der zu erklärende SQL-Befehl.
    *
    * @return Eine natürlichsprachliche Erklärung des SQL-Befehls als
    *         HTML-String.
    */
   public String explainSql(final String sqlQuery) {
      logger.info("Erkläre SQL: '{}'", sqlQuery);
      final String template = """
            Erkläre den folgenden SQL-Befehl (MariaDB-Dialekt) Schritt für Schritt und
            in einfachen Worten auf Deutsch.
            Formatiere deine Antwort als einfaches HTML. Verwende Tags wie <b> für Hervorhebungen,
            <p> für Absätze, <ul> und <li> für Listen und <code> für Code-Ausschnitte.
            Gehe dabei auf jede Klausel (SELECT, FROM, WHERE, JOIN, etc.) einzeln ein.
            Nutze das folgende Schema als Kontext, um die Tabellen- und Spaltennamen korrekt zu interpretieren.

            Schema:
            {{schema}}

            SQL-Befehl:
            {{sql}}
            """;
      final PromptTemplate promptTemplate = PromptTemplate.from(template);
      final Map<String, Object> variables = new HashMap<>();
      variables.put("schema", dbSchema);
      variables.put("sql", sqlQuery);
      final Prompt prompt = promptTemplate.apply(variables);

      final String explanation = chatModel.chat(prompt.text());
      logger.info("SQL-Erklärung generiert.");
      return explanation;
   }

   /**
    * Generiert eine SQL-Abfrage aus einer natürlichsprachlichen Frage.
    *
    * @param question Die Frage des Benutzers.
    *
    * @return Der von der KI generierte und bereinigte SQL-String.
    */
   public String generateSql(final String question) {
      logger.info("Generiere SQL für die Frage: '{}'", question);
      final String template = """
            Basierend auf dem folgenden Datenbankschema, generiere eine SQL-Abfrage (nur MariaDB-Dialekt),
            um die unten stehende Frage zu beantworten.
            WICHTIG: Verwende immer die explizite `INNER JOIN`-Syntax anstelle des verkürzten `JOIN`.
            Gib NUR den SQL-Befehl ohne weitere Erklärung zurück.


            Schema:
            {{schema}}

            Frage:
            {{question}}
            """;
      final PromptTemplate promptTemplate = PromptTemplate.from(template);
      final Map<String, Object> variables = new HashMap<>();
      variables.put("schema", dbSchema);
      variables.put("question", question);
      final Prompt prompt = promptTemplate.apply(variables);

      final String rawSql = chatModel.chat(prompt.text());
      logger.info("Generiertes SQL (roh): {}", rawSql);

      final String cleanedSql = cleanAndFormatSql(rawSql);
      logger.info("Generiertes SQL (bereinigt): {}", cleanedSql);
      return cleanedSql;
   }

   public String getCsvExportResponse(final String userRequest) {
      logger.info("Sende Anfrage an CSV-Export-Agent: '{}'", userRequest);
      return exportAgent.chat(userRequest);
   }

   private String loadSchemaFromFile(final String fileName) {
      logger.info("Lade Datenbankschema aus Datei: {}", fileName);
      try (InputStream is = AgentModel.class.getClassLoader()
         .getResourceAsStream(fileName)) {
         if (is == null) {
            throw new IOException(
                  "Datei nicht im 'resources' Ordner gefunden: " + fileName);
         }
         return new String(is.readAllBytes(), StandardCharsets.UTF_8);
      } catch (final IOException e) {
         logger.error("Fehler beim Laden der Schemadatei.", e);
         throw new RuntimeException("Konnte Schema nicht laden!", e);
      }
   }

   /**
    * Eine Hilfsmethode, um ein JDBC ResultSet in ein für JTable
    * verständliches TableModel umzuwandeln. Überschreibt getColumnClass,
    * damit der Renderer die Datentypen korrekt erkennen kann.
    *
    * @param rs Das ResultSet von der Datenbank.
    *
    * @return Ein DefaultTableModel, das direkt in einer JTable angezeigt
    *         werden kann.
    *
    * @throws SQLException
    */
   private TableModel resultSetToTableModel(final ResultSet rs)
         throws SQLException {

      final ResultSetMetaData metaData = rs.getMetaData();
      final int columnCount = metaData.getColumnCount();
      final Vector<String> columnNames = new Vector<>();
      for (int column = 1; column <= columnCount; column++) {
         columnNames.add(metaData.getColumnLabel(column));
      }

      final Vector<Vector<Object>> data = new Vector<>();
      while (rs.next()) {
         final Vector<Object> vector = new Vector<>();
         for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            vector.add(rs.getObject(columnIndex));
         }
         data.add(vector);
      }

      return new DefaultTableModel(data, columnNames) {
         @Override
         public Class<?> getColumnClass(final int columnIndex) {
            if (getRowCount() == 0) {
               return Object.class;
            }
            final Object value = getValueAt(0, columnIndex);
            return value != null ? value.getClass() : Object.class;
         }
      };
   }
}