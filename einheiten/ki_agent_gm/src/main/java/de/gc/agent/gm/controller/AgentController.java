package de.gc.agent.gm.controller;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import de.gc.agent.gm.model.AgentModel;
import de.gc.agent.gm.view.AgentView;

/**
 * Der Controller in der MVC-Architektur. Er fungiert als Bindeglied
 * zwischen der View (Benutzeroberfläche) und dem Model
 * (Anwendungslogik). Er reagiert auf Benutzeraktionen und steuert den
 * Datenfluss.
 */
public class AgentController {

   private final AgentModel model;
   private final AgentView view;

   /**
    * Konstruktor, der Model und View miteinander verbindet.
    *
    * @param model Die Instanz des Models.
    * @param view  Die Instanz der View.
    */
   public AgentController(final AgentModel model, final AgentView view) {
      this.model = model;
      this.view = view;
   }

   /**
    * Leert das Eingabefeld für die Frage und setzt den Fokus darauf.
    */
   private void clearInput() {
      view.getQuestionArea()
         .setText("");
      view.getQuestionArea()
         .requestFocusInWindow();
   }

   /**
    * Wird aufgerufen, wenn der "SQL ausführen"-Button geklickt wird. Holt
    * das SQL aus der View, gibt es an das Model zur Ausführung weiter und
    * zeigt das Ergebnis in der JTable der View an.
    */
   private void executeSql() {
      final String sqlQuery = view.getSqlArea()
         .getText();
      if (sqlQuery.isBlank()) {
         showError("Kein SQL-Befehl zum Ausführen vorhanden.");
         return;
      }
      try {
         final TableModel resultModel = model.executeSql(sqlQuery);
         view.updateResultTable(resultModel);
         view.updateResultTitle(resultModel.getRowCount());
      } catch (final Exception e) {
         showError("Fehler bei der SQL-Ausführung: " + e.getMessage());
      }
   }

   /**
    * Wird aufgerufen, wenn der "SQL erklären"-Button geklickt wird. Holt
    * das SQL aus der View, lässt es vom Model erklären und zeigt das
    * Ergebnis in einem Dialog an.
    */
   private void explainSql() {
      final String sqlQuery = view.getSqlArea()
         .getText();
      if (sqlQuery.isBlank()) {
         showError("Kein SQL-Befehl zum Erklären vorhanden.");
         return;
      }
      try {
         String explanation = model.explainSql(sqlQuery);
         explanation = explanation.replaceAll("(?i)```html\\s*", "")
            .replaceAll("```", ""); // Entferne Markdown-Tags
         showExplanationDialog(explanation);
      } catch (final Exception e) {
         showError("Fehler bei der SQL-Erklärung: " + e.getMessage());
      }
   }

   /**
    * Wird aufgerufen, wenn der "Als CSV exportieren"-Button geklickt wird.
    * Fragt den Benutzer nach einem Dateinamen und startet den
    * Export-Agenten.
    */
   private void exportToCsv() {
      final String sqlQuery = view.getSqlArea()
         .getText();
      if (sqlQuery.isBlank()) {
         showError("Kein SQL-Befehl zum Exportieren vorhanden.");
         return;
      }
      String fileName = JOptionPane.showInputDialog(view,
            "Geben Sie den Dateinamen für den CSV-Export an:",
            "Exportieren als CSV", JOptionPane.PLAIN_MESSAGE);
      if (fileName != null && !fileName.isBlank()) {
         if (!fileName.toLowerCase()
            .endsWith(".csv")) {
            fileName += ".csv";
         }
         final String request = String.format(
               "Exportiere die folgende Abfrage als CSV-Datei mit dem Namen '%s': %s",
               fileName, sqlQuery);
         try {
            final String response = model.getCsvExportResponse(request);
            showMessage(response);
         } catch (final Exception e) {
            showError(
                  "Fehler beim Aufruf des Export-Agenten: " + e.getMessage());
         }
      }
   }

   /**
    * Wird aufgerufen, wenn der "Anfrage senden"-Button geklickt wird. Holt
    * die Frage aus der View, gibt sie an das Model weiter, um SQL zu
    * generieren, und zeigt das Ergebnis in der View an.
    */
   private void generateSql() {

      final TableModel emptyModel = new DefaultTableModel();
      view.updateResultTable(emptyModel);
      view.updateResultTitle(emptyModel.getRowCount());

      final String question = view.getQuestionArea()
         .getText();
      if (question.isBlank()) {
         showError("Bitte geben Sie zuerst eine Frage ein.");
         return;
      }
      try {
         final String generatedSql = model.generateSql(question);
         view.setSqlText(generatedSql);
         view.setExecuteAndExportEnabled(true);
      } catch (final Exception e) {
         showError("Fehler bei der SQL-Generierung: " + e.getMessage());
      }
   }

   /**
    * Initialisiert den Controller. Hier werden die ActionListener für die
    * Buttons in der View registriert.
    */
   public void initController() {
      view.getSendButton()
         .addActionListener(e -> generateSql());
      view.getExecuteButton()
         .addActionListener(e -> executeSql());
      view.getExplainSqlButton()
         .addActionListener(e -> explainSql());
      view.getExportButton()
         .addActionListener(e -> exportToCsv());
      view.getClearButton()
         .addActionListener(e -> clearInput());
      view.getExitButton()
         .addActionListener(e -> System.exit(0));
   }

   private void showError(final String message) {
      JOptionPane.showMessageDialog(view, message, "Fehler",
            JOptionPane.ERROR_MESSAGE);
   }

   /**
    * Zeigt einen Dialog mit der Erklärung des SQL-Befehls an. Die
    * Erklärung wird in einer JEditorPane innerhalb einer JScrollPane
    * angezeigt, um den HTML-formatierten Text darzustellen.
    *
    * @param explanationHtml Die zu anzeigende Erklärung im HTML-Format.
    */
   private void showExplanationDialog(final String explanationHtml) {

      final JEditorPane editorPane = new JEditorPane();
      editorPane.setContentType("text/html");
      editorPane.setText(explanationHtml);
      editorPane.setEditable(false);

      // Bie text/html muss der Font über CSS gesetzt werden
      final HTMLEditorKit kit = (HTMLEditorKit) editorPane.getEditorKit();
      final StyleSheet styleSheet = kit.getStyleSheet();

      final String fontname = AgentView.dialogFont.getFontName();
      final int fontsize = AgentView.dialogFont.getSize();

      final String cssRule = String.format(
            """
                  body { font-family: %s; font-size: %dpt; }
                  code { font-family: Monospaced; background-color: #f4f4f4; color: #D63369; font-size: %dpt; }
                  """,
            fontname, fontsize, fontsize - 1 // Code oft einen Punkt kleiner
      );
      styleSheet.addRule(cssRule);

      final JScrollPane scrollPane = new JScrollPane(editorPane);
      scrollPane.setPreferredSize(new Dimension(900, 650));

      JOptionPane.showMessageDialog(view, scrollPane,
            "Erklärung des SQL-Befehls", JOptionPane.INFORMATION_MESSAGE);
   }

   private void showMessage(final String message) {
      JOptionPane.showMessageDialog(view, message, "Information",
            JOptionPane.INFORMATION_MESSAGE);
   }
}