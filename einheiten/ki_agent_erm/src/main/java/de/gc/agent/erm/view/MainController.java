package de.gc.agent.erm.view;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import de.gc.agent.erm.MainApp;
import de.gc.agent.erm.model.GenerationResult;
import de.gc.agent.erm.model.ProjectState;
import de.gc.agent.erm.model.Relationship;
import de.gc.agent.erm.model.tutor.ErmDiagramTutorStrategy;
import de.gc.agent.erm.model.tutor.ErmPlantUmlTutorStrategy;
import de.gc.agent.erm.model.tutor.InformationTutorStrategy;
import de.gc.agent.erm.model.tutor.LogicalModelPlantUmlTutorStrategy;
import de.gc.agent.erm.model.tutor.LogicalModelTutorStrategy;
import de.gc.agent.erm.model.tutor.RelationshipTutorStrategy;
import de.gc.agent.erm.model.tutor.SqlDdlTutorStrategy;
import de.gc.agent.erm.model.tutor.TutorStrategy;
import de.gc.agent.erm.service.ErmGeneratorService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.plantuml.FileFormat;

/**
 * Hauptcontroller für die ERM-Editor-Anwendung.
 *
 * Koordiniert alle UI-Komponenten, Aktionen, Tutor-Dialoge und die
 * Anbindung an den ErmGeneratorService. Stellt Import-, Export-, Help-
 * und Analysefunktionen für das gesamte Projekt und alle
 * Modellierungs-Schritte bereit.
 */
public class MainController {

   /** Der zentrale Service für KI-Analyse und Modellgenerierung. */
   private final ErmGeneratorService service;

   /** Observable-Tabelle für die Beziehungen in der UI. */
   private final ObservableList<Relationship> relationships = FXCollections
      .observableArrayList();

   /** Merkt die zuletzt gesetzten SplitPane-Positionen (Pane-Layout). */
   private double[] lastDividerPositions = { 0.25, 0.5, 0.75 };

   /** Sperrflag gegen rekursive Divider-Änderungen. */
   private boolean isUpdatingDividers = false;

   // --- FXML Components ---
   @FXML
   private SplitPane mainSplitPane;
   @FXML
   private TextArea descriptionTextArea;
   @FXML
   private TableView<Relationship> relationshipTableView;
   @FXML
   private TableColumn<Relationship, String> entity1Col;
   @FXML
   private TableColumn<Relationship, String> card1Col;
   @FXML
   private TableColumn<Relationship, String> verbCol;
   @FXML
   private TableColumn<Relationship, String> card2Col;
   @FXML
   private TableColumn<Relationship, String> entity2Col;
   @FXML
   private TableColumn<Relationship, String> directionCol;
   @FXML
   private ImageView ermDiagramImageView;
   @FXML
   private TextArea ermPlantUmlTextArea;
   @FXML
   private ImageView tableModelImageView;
   @FXML
   private TextArea tableModelPlantUmlTextArea;
   @FXML
   private TextArea sqlDdlTextArea;
   @FXML
   private ProgressIndicator progressIndicator;
   @FXML
   private Button analyzeButton;
   @FXML
   private Button createErmButton;
   @FXML
   private Button createTableModelButton;
   @FXML
   private Button createSqlDdlButton;

   /**
    * Konstruktor des MainController, injiziert den Service.
    *
    * @param service Der zentrale Generator-Service.
    */
   public MainController(final ErmGeneratorService service) {
      this.service = service;
   }

   /**
    * Hilfsmethode zum Erzeugen von FileChoosern für Dateioperationen.
    *
    * @param title   Dialogtitel.
    * @param filters Extension-Filter für zulässige Dateitypen.
    *
    * @return Konfigurierter FileChooser.
    */
   private FileChooser createFileChooser(final String title,
         final FileChooser.ExtensionFilter... filters) {
      final FileChooser fc = new FileChooser();
      fc.setTitle(title);
      fc.getExtensionFilters()
         .addAll(filters);
      return fc;
   }

   // --- Action Handlers ---

   /**
    * Exportiert ein Diagramm im gewählten Format aus dem Source-Textarea.
    *
    * @param imageView Ziel-ImageView (für spätere Aktualisierung).
    * @param textArea  Source-TextArea mit dem Modellcode.
    */
   private void exportDiagram(final ImageView imageView,
         final TextArea textArea) {
      final FileChooser fc = createFileChooser("Diagramm exportieren",
            new FileChooser.ExtensionFilter("PNG-Bild", "*.png"),
            new FileChooser.ExtensionFilter("SVG-Vektorgrafik", "*.svg"));
      final File file = fc.showSaveDialog(getWindow());
      if (file != null) {
         try {
            final String ext = fc.getSelectedExtensionFilter()
               .getExtensions()
               .get(0);
            final FileFormat format = ext.equals("*.svg") ? FileFormat.SVG
                  : FileFormat.PNG;
            service.exportDiagram(textArea.getText(), file, format);
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Exportieren: " + e.getMessage());
         }
      }
   }

   /**
    * Exportiert den PlantUML-Code aus dem angegebenen TextArea als Datei.
    *
    * @param textArea TextArea mit PlantUML-Code.
    */
   private void exportPuml(final TextArea textArea) {
      final FileChooser fc = createFileChooser("PlantUML-Code exportieren",
            new FileChooser.ExtensionFilter("PlantUML-Dateien", "*.puml"));
      final File file = fc.showSaveDialog(getWindow());
      if (file != null) {
         try (PrintWriter out = new PrintWriter(file)) {
            out.println(textArea.getText());
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Exportieren: " + e.getMessage());
         }
      }
   }

   /**
    * Liefert das Hauptfenster für Modaldialoge und Dateioperationen.
    *
    * @return Window-Handle des aktuellen Fensters.
    */
   private Window getWindow() {
      return mainSplitPane.getScene()
         .getWindow();
   }

   /**
    * Initialisiert die UI und TableView-Bindungen nach dem FXML-Laden.
    */
   @FXML
   public void initialize() {
      relationshipTableView.setItems(relationships);
      entity1Col.setCellValueFactory(cellData -> cellData.getValue()
         .entity1Property());
      card1Col.setCellValueFactory(cellData -> cellData.getValue()
         .cardinality1Property());
      verbCol.setCellValueFactory(cellData -> cellData.getValue()
         .verbProperty());
      card2Col.setCellValueFactory(cellData -> cellData.getValue()
         .cardinality2Property());
      entity2Col.setCellValueFactory(cellData -> cellData.getValue()
         .entity2Property());
      directionCol.setCellValueFactory(cellData -> cellData.getValue()
         .directionProperty());

      setColumnEditable(entity1Col);
      setColumnEditable(card1Col);
      setColumnEditable(verbCol);
      setColumnEditable(card2Col);
      setColumnEditable(entity2Col);
      setColumnEditable(directionCol);

      final ChangeListener<Number> dividerListener = (obs, oldVal, newVal) -> {
         if (!isUpdatingDividers) {
            lastDividerPositions = mainSplitPane.getDividerPositions();
         }
      };
      mainSplitPane.getDividers()
         .get(0)
         .positionProperty()
         .addListener(dividerListener);
      mainSplitPane.getDividers()
         .get(1)
         .positionProperty()
         .addListener(dividerListener);
      mainSplitPane.getDividers()
         .get(2)
         .positionProperty()
         .addListener(dividerListener);
   }

   // --- Tutor Handlers ---

   /**
    * Öffnet einen Tutor-Dialog im passenden Kontext und mit dynamischer
    * Größe.
    *
    * @param strategy Coach-Strategie für das Thema.
    * @param context  Kontextinformation für den Tutor.
    */
   private void launchTutorDialog(final TutorStrategy strategy,
         final String context) {
      try {
         final FXMLLoader loader = new FXMLLoader(
               MainApp.class.getResource("view/TutorDialog.fxml"));
         final Stage stage = new Stage();
         stage.setTitle(strategy.getDialogTitle());
         stage.initOwner(getWindow());

         // Dialoggröße an Bildschirm anpassen
         final Rectangle2D screenBounds = Screen.getPrimary()
            .getVisualBounds();
         final Scene scene = new Scene(loader.load(),
               screenBounds.getWidth() * 0.66, screenBounds.getHeight());
         scene.getStylesheets()
            .add(MainApp.class.getResource("view/styles.css")
               .toExternalForm());
         stage.setScene(scene);

         final TutorDialogController controller = loader.getController();
         controller.initialize(strategy, context);
         stage.show();
      } catch (final IOException e) {
         e.printStackTrace();
         showAlert(Alert.AlertType.ERROR,
               "Der Hilfe-Dialog konnte nicht geladen werden.");
      }
   }

   // --- Table Manipulation ---
   /**
    * Fügt eine neue Standardbeziehung zur Beziehungstabelle hinzu.
    */
   @FXML
   private void onAddRowClicked() {
      relationships.add(new Relationship("Neue Entität", "1",
            "bezieht sich auf", "1", "Andere Entität", ">"));
   }

   /**
    * Löst die Analyse des Beschreibungstextes und aktualisiert die
    * Beziehungstabelle.
    */
   @FXML
   private void onAnalyzeClicked() {
      final String description = descriptionTextArea.getText();
      if (description.isBlank()) {
         showAlert(Alert.AlertType.WARNING,
               "Bitte geben Sie eine Beschreibung ein.");
         return;
      }
      final Task<List<Relationship>> task = new Task<>() {
         @Override
         protected List<Relationship> call() {
            return service.analyzeDescription(description);
         }
      };
      task.setOnSucceeded(event -> relationships.setAll(task.getValue()));
      runTask(task);
   }

   /**
    * Kopiert die aktuell selektierte Beziehung und fügt sie als neue Zeile
    * hinzu.
    */
   @FXML
   private void onCopyRowClicked() {
      final Relationship selected = relationshipTableView.getSelectionModel()
         .getSelectedItem();
      if (selected != null) {
         relationships.add(new Relationship(selected.getEntity1(),
               selected.getCardinality1(), selected.getVerb(),
               selected.getCardinality2(), selected.getEntity2(),
               selected.getDirection()));
      }
   }

   /**
    * Erstellt aus den vorhandenen Beziehungen das ERM-Diagramm und rendert
    * das Bild.
    */
   @FXML
   private void onCreateErmClicked() {
      if (relationships.isEmpty()) {
         showAlert(Alert.AlertType.WARNING, "Die Beziehungstabelle ist leer.");
         return;
      }
      final List<Relationship> currentRelationships = List
         .copyOf(relationshipTableView.getItems());
      final Task<GenerationResult> task = new Task<>() {
         @Override
         protected GenerationResult call() throws IOException {
            final String plantUml = service
               .generatePlantUmlFromRelationships(currentRelationships);
            return new GenerationResult(plantUml,
                  service.renderPlantUml(plantUml));
         }
      };
      task.setOnSucceeded(event -> {
         final GenerationResult result = task.getValue();
         ermPlantUmlTextArea.setText(result.textContent());
         ermDiagramImageView.setImage(result.image());
      });
      runTask(task);
   }

   /**
    * Erstellt das SQL-DDL-Skript aus dem logischen Tabellenmodell.
    */
   @FXML
   private void onCreateSqlDdlClicked() {
      final String logicalPuml = tableModelPlantUmlTextArea.getText();
      if (logicalPuml.isBlank()) {
         showAlert(Alert.AlertType.WARNING,
               "Das logische Modell (Schritt 3) ist leer. Erstellen Sie es zuerst.");
         return;
      }
      final Task<String> task = new Task<>() {
         @Override
         protected String call() {
            return service.generateSqlDdl(logicalPuml);
         }
      };
      task.setOnSucceeded(event -> sqlDdlTextArea.setText(task.getValue()));
      runTask(task);
   }

   // --- Pane Toggling & Layout ---

   /**
    * Erstellt das logische Tabellenmodell aus dem PlantUML-Code des ERM
    * und rendert das Bild.
    */
   @FXML
   private void onCreateTableModelClicked() {
      final String ermPuml = ermPlantUmlTextArea.getText();
      if (ermPuml.isBlank()) {
         showAlert(Alert.AlertType.WARNING,
               "Das ERM-Diagramm (Schritt 2) ist leer. Erstellen Sie es zuerst.");
         return;
      }
      final Task<GenerationResult> task = new Task<>() {
         @Override
         protected GenerationResult call() throws IOException {
            return service.generateTableModel(ermPuml);
         }
      };
      task.setOnSucceeded(event -> {
         final GenerationResult result = task.getValue();
         tableModelPlantUmlTextArea.setText(result.textContent());
         tableModelImageView.setImage(result.image());
      });
      runTask(task);
   }

   /**
    * Löscht die aktuell selektierte Beziehung auf Wunsch.
    */
   @FXML
   private void onDeleteRowClicked() {
      final Relationship selected = relationshipTableView.getSelectionModel()
         .getSelectedItem();
      if (selected != null) {
         relationships.remove(selected);
      }
   }

   // --- Export & Save/Load ---
   @FXML
   private void onExportErmDiagramClicked() {
      exportDiagram(ermDiagramImageView, ermPlantUmlTextArea);
   }

   @FXML
   private void onExportErmPumlClicked() {
      exportPuml(ermPlantUmlTextArea);
   }

   @FXML
   private void onExportSqlClicked() {
      if (sqlDdlTextArea.getText()
         .isEmpty()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Kein SQL-Code zum Exportieren.");
         return;
      }
      final FileChooser fc = createFileChooser("SQL-Skript exportieren",
            new FileChooser.ExtensionFilter("SQL-Dateien", "*.sql"));
      final File file = fc.showSaveDialog(getWindow());
      if (file != null) {
         try (PrintWriter out = new PrintWriter(file)) {
            out.println(sqlDdlTextArea.getText());
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Exportieren: " + e.getMessage());
         }
      }
   }

   @FXML
   private void onExportTableModelDiagramClicked() {
      exportDiagram(tableModelImageView, tableModelPlantUmlTextArea);
   }

   @FXML
   private void onExportTableModelPumlClicked() {
      exportPuml(tableModelPlantUmlTextArea);
   }

   // --- Hilfe/Tutor-Funktionen ---

   @FXML
   private void onHelpErmDiagramClicked() {
      final String context = ermPlantUmlTextArea.getText();
      if (context.isBlank()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Bitte erstellen Sie zuerst das ERM-Diagramm (Schritt 2).");
         return;
      }
      launchTutorDialog(new ErmDiagramTutorStrategy(service), context);
   }

   @FXML
   private void onHelpErmPlantUmlClicked() {
      final String context = ermPlantUmlTextArea.getText();
      if (context.isBlank()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Bitte erstellen Sie zuerst das ERM-Diagramm (Schritt 2).");
         return;
      }
      launchTutorDialog(new ErmPlantUmlTutorStrategy(service), context);
   }

   @FXML
   private void onHelpLogicalModelClicked() {
      final String context = tableModelPlantUmlTextArea.getText();
      if (context.isBlank()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Bitte erstellen Sie zuerst das Tabellenmodell (Schritt 3).");
         return;
      }
      launchTutorDialog(new LogicalModelTutorStrategy(service), context);
   }

   @FXML
   private void onHelpLogicalModelPlantUmlClicked() {
      final String context = tableModelPlantUmlTextArea.getText();
      if (context.isBlank()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Bitte erstellen Sie zuerst das Tabellenmodell (Schritt 3).");
         return;
      }
      launchTutorDialog(new LogicalModelPlantUmlTutorStrategy(service),
            context);
   }

   @FXML
   private void onHelpRelationshipsTableClicked() {
      final String tableContext = relationships.stream()
         .map(r -> String.join("|", r.getEntity1(), r.getCardinality1(),
               r.getVerb(), r.getCardinality2(), r.getEntity2()))
         .collect(Collectors.joining("\n"));
      launchTutorDialog(new RelationshipTutorStrategy(service), tableContext);
   }

   @FXML
   private void onHelpSqlDdlClicked() {
      final String context = sqlDdlTextArea.getText();
      if (context.isBlank()) {
         showAlert(Alert.AlertType.INFORMATION,
               "Bitte erstellen Sie zuerst das SQL-Skript (Schritt 4).");
         return;
      }
      launchTutorDialog(new SqlDdlTutorStrategy(service), context);
   }

   @FXML
   private void onInformationTutorClicked() {
      // Universeller Tutor, kein spezifischer Kontext notwendig.
      launchTutorDialog(new InformationTutorStrategy(service), "");
   }

   // --- Save/Load/Export ---
   @FXML
   private void onLoadAnalysisClicked() {
      final FileChooser fc = createFileChooser("Analyse laden",
            new FileChooser.ExtensionFilter("JSON-Dateien", "*.json"));
      final File file = fc.showOpenDialog(getWindow());
      if (file != null) {
         try {
            relationships.setAll(service.loadRelationshipsFromJson(file));
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Laden: " + e.getMessage());
         }
      }
   }

   @FXML
   private void onLoadProjectClicked() {
      final FileChooser fc = createFileChooser("Gesamtes Projekt laden",
            new FileChooser.ExtensionFilter("ERM-Projektdateien", "*.ermp"));
      final File file = fc.showOpenDialog(getWindow());
      if (file != null) {
         try {
            final ProjectState loadedState = service.loadProjectState(file);

            // UI-Felder aus geladenem Zustand befüllen
            descriptionTextArea.setText(loadedState.description());
            relationships.setAll(loadedState.relationships());
            ermPlantUmlTextArea.setText(loadedState.ermPlantUml());
            tableModelPlantUmlTextArea
               .setText(loadedState.tableModelPlantUml());
            sqlDdlTextArea.setText(loadedState.sqlDdl());

            // Diagramme nach Ladevorgang neu rendern
            if (loadedState.ermPlantUml() != null
                  && !loadedState.ermPlantUml()
                     .isBlank()) {
               updateDiagramFromCode(ermPlantUmlTextArea, ermDiagramImageView);
            }
            if (loadedState.tableModelPlantUml() != null
                  && !loadedState.tableModelPlantUml()
                     .isBlank()) {
               updateDiagramFromCode(tableModelPlantUmlTextArea,
                     tableModelImageView);
            }

         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Laden des Projekts: " + e.getMessage());
         }
      }
   }

   /**
    * Setzt die SplitPane-Trenner auf die Standardausrichtung zurück.
    */
   @FXML
   private void onResetDividerPositions() {
      isUpdatingDividers = true;
      final double[] defaultPositions = { 0.25, 0.5, 0.75 };
      mainSplitPane.setDividerPositions(defaultPositions);
      lastDividerPositions = defaultPositions;
      isUpdatingDividers = false;
   }

   @FXML
   private void onSaveAnalysisClicked() {
      final FileChooser fc = createFileChooser("Analyse speichern",
            new FileChooser.ExtensionFilter("JSON-Dateien", "*.json"));
      final File file = fc.showSaveDialog(getWindow());
      if (file != null) {
         try {
            service.saveRelationshipsToJson(List.copyOf(relationships), file);
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Speichern: " + e.getMessage());
         }
      }
   }

   @FXML
   private void onSaveProjectClicked() {
      final FileChooser fc = createFileChooser("Gesamtes Projekt speichern",
            new FileChooser.ExtensionFilter("ERM-Projektdateien", "*.ermp"));
      final File file = fc.showSaveDialog(getWindow());
      if (file != null) {
         try {
            final ProjectState currentState = new ProjectState(
                  descriptionTextArea.getText(), List.copyOf(relationships),
                  ermPlantUmlTextArea.getText(),
                  tableModelPlantUmlTextArea.getText(),
                  sqlDdlTextArea.getText());
            service.saveProjectState(currentState, file);
         } catch (final IOException e) {
            showAlert(Alert.AlertType.ERROR,
                  "Fehler beim Speichern des Projekts: " + e.getMessage());
         }
      }
   }

   // --- Diagram Updates ---

   /**
    * Aktualisiert das ERM-Diagramm aus dem Quellcode-Feld.
    */
   @FXML
   private void onUpdateErmDiagramFromCodeClicked() {
      updateDiagramFromCode(ermPlantUmlTextArea, ermDiagramImageView);
   }

   /**
    * Aktualisiert das Tabellenmodell-Diagramm aus dem Quellcode-Feld.
    */
   @FXML
   private void onUpdateTableModelDiagramFromCodeClicked() {
      updateDiagramFromCode(tableModelPlantUmlTextArea, tableModelImageView);
   }

   // --- Hilfsmethoden für UI und Layout ---

   /**
    * Startet einen Task, verwaltet ProgressBar, Button-Status und
    * Fehlerhandling.
    *
    * @param task Der auszuführende Task.
    */
   private void runTask(final Task<?> task) {
      progressIndicator.visibleProperty()
         .bind(task.runningProperty());
      analyzeButton.disableProperty()
         .bind(task.runningProperty());
      createErmButton.disableProperty()
         .bind(task.runningProperty());
      createTableModelButton.disableProperty()
         .bind(task.runningProperty());
      createSqlDdlButton.disableProperty()
         .bind(task.runningProperty());
      task.setOnFailed(e -> {
         e.getSource()
            .getException()
            .printStackTrace();
         showAlert(Alert.AlertType.ERROR,
               "Ein Fehler ist aufgetreten: " + e.getSource()
                  .getException()
                  .getMessage());
      });
      new Thread(task).start();
   }

   /**
    * Setzt eine Spalte der Beziehungstabelle bearbeitbar.
    *
    * @param column Die Tabellenspalte.
    */
   private void setColumnEditable(
         final TableColumn<Relationship, String> column) {
      column.setCellFactory(TextFieldTableCell.forTableColumn());
   }

   /**
    * Zeigt eine Alert-Meldung im Benutzerinterface an.
    *
    * @param type    Art des Alerts.
    * @param message Meldungstext.
    */
   private void showAlert(final Alert.AlertType type, final String message) {
      new Alert(type, message).show();
   }

   /**
    * Schaltet die Sichtbarkeit/Position der Panes um.
    *
    * @param index Index des Panes.
    */
   private void togglePane(final int index) {
      isUpdatingDividers = true;
      final double[] positions = mainSplitPane.getDividerPositions();
      final double start = index == 0 ? 0 : positions[index - 1];
      final double end = index == 3 ? 1 : positions[index];
      final boolean isCollapsed = end - start < 0.02;

      if (isCollapsed) {
         mainSplitPane.setDividerPositions(lastDividerPositions);
      } else {
         if (index == 0) {
            mainSplitPane.setDividerPosition(0, 0.0);
         } else if (index == 1) {
            mainSplitPane.setDividerPosition(1, positions[0]);
         } else if (index == 2) {
            mainSplitPane.setDividerPosition(2, positions[1]);
         } else if (index == 3) {
            mainSplitPane.setDividerPosition(2, 1.0);
         }
      }
      isUpdatingDividers = false;
   }

   // Methoden für schnelle Pane-Umschaltung
   @FXML
   private void togglePane1() {
      togglePane(0);
   }

   @FXML
   private void togglePane2() {
      togglePane(1);
   }

   @FXML
   private void togglePane3() {
      togglePane(2);
   }

   @FXML
   private void togglePane4() {
      togglePane(3);
   }

   /**
    * Aktualisiert ein Diagramm anhand des PlantUML-Quelltexts in einem
    * TextArea.
    *
    * @param source Quell-TextArea mit PlantUML-Code.
    * @param target Ziel-ImageView für das gerenderte Diagramm.
    */
   private void updateDiagramFromCode(final TextArea source,
         final ImageView target) {
      try {
         target.setImage(service.renderPlantUml(source.getText()));
      } catch (final IOException e) {
         showAlert(Alert.AlertType.ERROR,
               "Fehler beim Rendern des Diagramms: " + e.getMessage());
      }
   }
}
