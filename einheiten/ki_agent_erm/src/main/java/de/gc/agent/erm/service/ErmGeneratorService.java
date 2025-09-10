package de.gc.agent.erm.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import de.gc.agent.erm.model.GenerationResult;
import de.gc.agent.erm.model.ProjectState;
import de.gc.agent.erm.model.Relationship;
import de.gc.agent.erm.model.agent.AnalysisAgent;
import de.gc.agent.erm.model.agent.ErmDiagramTutorAgent;
import de.gc.agent.erm.model.agent.ErmPlantUmlTutorAgent;
import de.gc.agent.erm.model.agent.InformationTutorAgent;
import de.gc.agent.erm.model.agent.LogicalModelPlantUmlTutorAgent;
import de.gc.agent.erm.model.agent.LogicalModelTutorAgent;
import de.gc.agent.erm.model.agent.SqlDdlAgent;
import de.gc.agent.erm.model.agent.SqlDdlTutorAgent;
import de.gc.agent.erm.model.agent.TableModelAgent;
import de.gc.agent.erm.model.agent.TutorAgent;
import de.gc.agent.erm.model.tutor.TutorResponse;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import javafx.scene.image.Image;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * Serviceklasse für die Generierung, Analyse und Verwaltung aller
 * Modelle und Tutor-Interaktionen im ERM-Editor.
 *
 * Diese Klasse kapselt die Anbindung an alle KI-Agenten (Analyse,
 * Modellumwandlung, Tutor-Dialoge), die Umwandlung von
 * Relationship-Listen in PlantUML, die Bildgenerierung sowie das
 * Speichern/Laden des Projektzustands.
 */
public class ErmGeneratorService {

   /** Agent für die semantische Analyse von Beschreibungen */
   private final AnalysisAgent analysisAgent;

   /**
    * Agent für die Umwandlung von ERM-Diagrammen ins logische
    * Tabellenmodell
    */
   private final TableModelAgent tableModelAgent;

   /** Agent zur Umwandlung logischer Modelle in SQL-DDL */
   private final SqlDdlAgent sqlDdlAgent;

   /** Tutor-Agent für Beziehungen */
   private final TutorAgent relationshipTutorAgent;

   /** Tutor-Agent für konzeptionelle ERM-Diagramme */
   private final ErmDiagramTutorAgent ermDiagramTutorAgent;

   /** Tutor-Agent für PlantUML-Syntax konzeptioneller Modelle */
   private final ErmPlantUmlTutorAgent ermPlantUmlTutorAgent;

   /** Tutor-Agent für logische Modelle */
   private final LogicalModelTutorAgent logicalModelTutorAgent;

   /** Tutor-Agent für PlantUML logischer Modelle */
   private final LogicalModelPlantUmlTutorAgent logicalModelPlantUmlTutorAgent;

   /** Tutor-Agent für SQL-DDL */
   private final SqlDdlTutorAgent sqlDdlTutorAgent;

   /** Allgemeiner Wissens-Tutor-Agent */
   private final InformationTutorAgent informationTutorAgent;

   /** Jackson-Objekt-Mapper für (De-)Serialisierung */
   private final ObjectMapper objectMapper = new JsonMapper();

   /**
    * Initialisiert den Service und alle KI-Agenten mit den
    * Modell-Konfigurationen.
    *
    * @param analysisModel Modell für die Beziehungsanalyse.
    * @param tableModel    Modell für die Tabellenmodellierung.
    * @param sqlModel      Modell für die SQL-DDL-Erstellung.
    * @param tutorModel    Gemeinsames Modell für alle Tutor-Agenten.
    */
   public ErmGeneratorService(final ChatModel analysisModel,
         final ChatModel tableModel,
         final ChatModel sqlModel,
         final ChatModel tutorModel) {
      this.analysisAgent = AiServices.create(AnalysisAgent.class,
            analysisModel);
      this.tableModelAgent = AiServices.create(TableModelAgent.class,
            tableModel);
      this.sqlDdlAgent = AiServices.create(SqlDdlAgent.class, sqlModel);

      // Alle Tutor-Agenten können das gleiche Modell nutzen
      this.relationshipTutorAgent = AiServices.create(TutorAgent.class,
            tutorModel);
      this.ermDiagramTutorAgent = AiServices.create(ErmDiagramTutorAgent.class,
            tutorModel);
      this.ermPlantUmlTutorAgent = AiServices
         .create(ErmPlantUmlTutorAgent.class, tutorModel);
      this.logicalModelTutorAgent = AiServices
         .create(LogicalModelTutorAgent.class, tutorModel);
      this.logicalModelPlantUmlTutorAgent = AiServices
         .create(LogicalModelPlantUmlTutorAgent.class, tutorModel);
      this.sqlDdlTutorAgent = AiServices.create(SqlDdlTutorAgent.class,
            tutorModel);
      this.informationTutorAgent = AiServices
         .create(InformationTutorAgent.class, tutorModel);
   }

   /**
    * Analysiert einen Beschreibungstext und erzeugt daraus die
    * priorisierte und bereinigte Liste der Beziehungen.
    *
    * @param description Frei formulierter Beschreibungstext.
    *
    * @return Liste von Relationship-Objekten.
    */
   public List<Relationship> analyzeDescription(final String description) {
      final String rawAnalysis = analysisAgent
         .analyzeRelationships(description);
      final List<Relationship> parsedList = parseAnalysisResult(rawAnalysis);
      return prioritizeAndDeduplicateRelationships(parsedList);
   }

   /**
    * Bereinigt einen Raw-JSON-String, indem falsch eingefügte Markdown-Wrapper entfernt werden.
    *
    * @param rawJson Ursprünglicher JSON-String (ggf. mit Markdown-Wrappern).
    * @return Der bereinigte, reine JSON-String.
    */
   private String cleanJsonString(final String rawJson) {
      if (rawJson == null) {
         return "";
      }
      String cleaned = rawJson.trim();
      if (cleaned.startsWith("```json")) {
         cleaned = cleaned.substring(7);
      }
      if (cleaned.startsWith("```sql")) {
         cleaned = cleaned.substring(6);
      }
      if (cleaned.startsWith("```")) {
         cleaned = cleaned.substring(3);
      }
      if (cleaned.endsWith("```")) {
         cleaned = cleaned.substring(0, cleaned.length() - 3);
      }
      return cleaned.trim();
   }

   /**
    * Exportiert ein PlantUML-Diagramm als Bilddatei mit gewünschtem
    * Format.
    *
    * @param plantUmlSource Der PlantUML-Quellcode des Diagramms.
    * @param file           Zieldatei für das Bild.
    * @param format         Das PlantUML-Ausgabeformat.
    *
    * @throws IOException Bei Datei- oder Bildgenerierungsfehlern.
    */
   public void exportDiagram(final String plantUmlSource, final File file,
         final FileFormat format) throws IOException {
      try (FileOutputStream fos = new FileOutputStream(file)) {
         final SourceStringReader reader = new SourceStringReader(
               plantUmlSource);
         reader.outputImage(fos, new FileFormatOption(format));
      }
   }

   /**
    * Wandelt eine Liste von Beziehungen automatisch in einen
    * PlantUML-Diagrammcode um.
    *
    * @param relationships Liste der Relationships.
    *
    * @return PlantUML-Code für das daraus generierte Diagramm.
    */
   public String generatePlantUmlFromRelationships(
         final List<Relationship> relationships) {
      if (relationships == null || relationships.isEmpty()) {
         return "@startuml\n@enduml";
      }
      final StringBuilder sb = new StringBuilder("@startuml\n\n");
      final Set<String> entities = new HashSet<>();
      for (final Relationship rel : relationships) {
         entities.add(rel.getEntity1());
         entities.add(rel.getEntity2());
      }
      for (final String entity : entities) {
         sb.append(String.format("entity %s {}\n", entity));
      }
      sb.append("\n");
      for (final Relationship rel : relationships) {
         sb.append(String.format("%s \"%s\" -- \"%s\" %s : %s %s\n",
               rel.getEntity1(), rel.getCardinality1(), rel.getCardinality2(),
               rel.getEntity2(), rel.getVerb(), rel.getDirection()));
      }
      sb.append("\n@enduml");
      return sb.toString();
   }

   /**
    * Generiert ein SQL-DDL-Skript (MariaDB) aus einem logischen
    * PlantUML-Modell.
    *
    * @param logicalModelPuml PlantUML-Code für das logische Modell.
    *
    * @return SQL-DDL als String.
    */
   public String generateSqlDdl(final String logicalModelPuml) {
      return sqlDdlAgent.generateSqlDdl(logicalModelPuml);
   }

   /**
    * Wandelt einen konzeptionellen PlantUML-ERM-Code in ein logisches
    * Tabellenmodell um und generiert das Diagrammbild.
    *
    * @param ermPuml PlantUML-Quelltext des konzeptionellen Modells.
    *
    * @return Ein Container mit PlantUML-Code und generiertem Bild.
    *
    * @throws IOException Bei Fehlern in der Bildgenerierung.
    */
   public GenerationResult generateTableModel(final String ermPuml)
         throws IOException {
      final String tableModelPuml = tableModelAgent.generateTableModel(ermPuml);
      return new GenerationResult(tableModelPuml,
            renderPlantUml(tableModelPuml));
   }

   /**
    * Ruft die Antwort eines Tutor-Agenten für konzeptionelle ERM-Diagramme
    * ab.
    *
    * @param context  Kontextinformation.
    * @param question Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Vorschlägen.
    */
   public TutorResponse getErmDiagramTutorResponse(final String context,
         final String question) {
      final String rawJson = ermDiagramTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort eines Tutor-Agenten für konzeptionelle
    * PlantUML-Diagramme ab.
    *
    * @param context  Kontextinformation.
    * @param question Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Vorschlägen.
    */
   public TutorResponse getErmPlantUmlTutorResponse(final String context,
         final String question) {
      final String rawJson = ermPlantUmlTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort des universellen Wissens-Tutor-Agenten ab.
    *
    * @param context  Kontextinformation (meist leer).
    * @param question Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   public TutorResponse getInformationTutorResponse(final String context,
         final String question) {
      final String rawJson = informationTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort eines Tutor-Agenten für logische
    * PlantUML-Tabellenmodelle ab.
    *
    * @param context  Kontextinformation.
    * @param question Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   public TutorResponse getLogicalModelPlantUmlTutorResponse(
         final String context, final String question) {
      final String rawJson = logicalModelPlantUmlTutorAgent.chat(context,
            question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort eines Tutor-Agenten für logische Modelle ab.
    *
    * @param context  Kontextinformation.
    * @param question Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   public TutorResponse getLogicalModelTutorResponse(final String context,
         final String question) {
      final String rawJson = logicalModelTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort des Tutor-Agenten für SQL-DDL-Fragen ab.
    *
    * @param context  SQL-DDL-Kontext.
    * @param question Die Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   public TutorResponse getSqlDdlTutorResponse(final String context,
         final String question) {
      final String rawJson = sqlDdlTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Ruft die Antwort des Beziehungstutor-Agenten ab.
    *
    * @param context  Inhalt der Beziehungstabelle.
    * @param question Die Schülerfrage.
    *
    * @return TutorResponse mit Erklärung und Fragenvorschlägen.
    */
   public TutorResponse getTutorResponse(final String context,
         final String question) {
      final String rawJson = relationshipTutorAgent.chat(context, question);
      return parseTutorResponse(rawJson);
   }

   /**
    * Gibt zurück, ob eine der beiden Kardinalitäten einer Beziehung
    * "viele" ist.
    *
    * @param rel Beziehung zum Prüfen.
    *
    * @return true, falls eine Kardinalität "*" enthält.
    */
   private boolean isMany(final Relationship rel) {
      return rel.getCardinality1()
         .contains("*")
            || rel.getCardinality2()
               .contains("*");
   }

   /**
    * Lädt den aktuellen Projektzustand aus einer Datei.
    *
    * @param file Dateipfad zur Projektdatei.
    *
    * @return Deserialisierter ProjectState.
    *
    * @throws IOException Bei Datei- oder Deserialisierungsfehlern.
    */
   public ProjectState loadProjectState(final File file) throws IOException {
      return objectMapper.readValue(file, ProjectState.class);
   }

   /**
    * Lädt eine Relationship-Liste aus einer JSON-Datei.
    *
    * @param file JSON-Datei mit gespeichertem Relationship-Array.
    *
    * @return Liste von Relationship-Objekten.
    *
    * @throws IOException Bei Datei- oder Deserialisierungsfehlern.
    */
   public List<Relationship> loadRelationshipsFromJson(final File file)
         throws IOException {
      return objectMapper.readValue(file,
            new TypeReference<List<Relationship>>() {
            });
   }

   /**
    * Parst das Ergebnis der Beziehungsanalyse (Text) in
    * Relationship-Objekte.
    *
    * @param rawAnalysis Analyse-Output als Text (Pipe-getrennte Felder).
    *
    * @return Liste von Relationship-Objekten.
    */
   private List<Relationship> parseAnalysisResult(final String rawAnalysis) {
      final List<Relationship> relationships = new ArrayList<>();
      if (rawAnalysis == null || rawAnalysis.isBlank()) {
         return relationships;
      }
      final String[] lines = rawAnalysis.split("\n");
      for (final String line : lines) {
         if (line.isBlank()) {
            continue;
         }
         final String[] parts = line.split("\\|");
         if (parts.length == 5) {
            relationships.add(new Relationship(parts[0].trim(), parts[1].trim(),
                  parts[2].trim(), parts[3].trim(), parts[4].trim(), ">"));
         } else {
            System.err.println("Skipping malformed line from AI: " + line);
         }
      }
      return relationships;
   }

   /**
    * Parst eine Tutor-Agenten-Antwort (JSON-String) in ein
    * TutorResponse-Objekt. Bereinigt zunächst String-Wrapper und liest
    * dann die Daten.
    *
    * @param rawJson Roher JSON-Antwortstring.
    *
    * @return TutorResponse-Objekt oder Fehlerantwort.
    */
   private TutorResponse parseTutorResponse(final String rawJson) {
      final String cleanedJson = cleanJsonString(rawJson);
      try {
         return objectMapper.readValue(cleanedJson, TutorResponse.class);
      } catch (final IOException e) {
         e.printStackTrace();
         return new TutorResponse(
               "Entschuldigung, bei der Verarbeitung der Antwort ist ein Fehler aufgetreten. Die Rohdaten waren:\n\n"
                     + rawJson,
               List.of());
      }
   }

   /**
    * Priorisiert und dedupliziert die analysierten Beziehungen anhand der
    * Entitätsnamen. Bevorzugt Beziehungen mit Kardinalität "*".
    *
    * @param rawList Ungefilterte Relationship-Liste.
    *
    * @return Bereinigte, priorisierte Relationship-Liste.
    */
   private List<Relationship> prioritizeAndDeduplicateRelationships(
         final List<Relationship> rawList) {
      if (rawList == null) {
         return new ArrayList<>();
      }
      final Map<String, Relationship> bestRelationships = new LinkedHashMap<>();
      for (final Relationship currentRel : rawList) {
         final String[] entities = { currentRel.getEntity1(),
               currentRel.getEntity2() };
         Arrays.sort(entities);
         final String canonicalKey = entities + "-" + entities;
         if (!bestRelationships.containsKey(canonicalKey) || isMany(currentRel)
               && !isMany(bestRelationships.get(canonicalKey))) {
            bestRelationships.put(canonicalKey, currentRel);
         }
      }
      return new ArrayList<>(bestRelationships.values());
   }

   /**
    * Rendern eines PlantUML-Diagrammquelltexts als JavaFX-Image.
    *
    * @param plantUmlSource PlantUML-Quelltext.
    *
    * @return Das gerenderte Diagramm als Image.
    *
    * @throws IOException Bei Bildgenerierungsfehlern.
    */
   public Image renderPlantUml(final String plantUmlSource) throws IOException {
      if (plantUmlSource == null || plantUmlSource.trim()
         .isEmpty()) {
         throw new IOException("Leere PlantUML-Eingabe.");
      }
      final ByteArrayOutputStream os = new ByteArrayOutputStream();
      final SourceStringReader reader = new SourceStringReader(plantUmlSource);
      reader.outputImage(os);
      os.close();
      final byte[] imageBytes = os.toByteArray();
      if (imageBytes.length == 0) {
         throw new IOException(
               "PlantUML konnte kein Bild generieren. Prüfen Sie die Syntax.");
      }
      return new Image(new ByteArrayInputStream(imageBytes));
   }

   /**
    * Speichert den aktuellen Projektzustand als JSON in eine Datei.
    *
    * @param projectState Der Zustand des Projekts.
    * @param file         Datei für die Speicherung.
    *
    * @throws IOException Bei Datei- oder Serialisierungsfehlern.
    */
   public void saveProjectState(final ProjectState projectState,
         final File file) throws IOException {
      objectMapper.writerWithDefaultPrettyPrinter()
         .writeValue(file, projectState);
   }

   /**
    * Speichert die Liste der Beziehungen als JSON in eine Datei.
    *
    * @param relationships Die Liste der Relationship-Objekte.
    * @param file          Datei für die Speicherung.
    *
    * @throws IOException Bei Datei- oder Serialisierungsfehlern.
    */
   public void saveRelationshipsToJson(final List<Relationship> relationships,
         final File file) throws IOException {
      objectMapper.writerWithDefaultPrettyPrinter()
         .writeValue(file, relationships);
   }
}
