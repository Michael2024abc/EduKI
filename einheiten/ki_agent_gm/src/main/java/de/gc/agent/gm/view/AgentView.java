package de.gc.agent.gm.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import de.gc.agent.gm.util.ShowFonts;

/**
 * Die View in der MVC-Architektur. Diese Klasse ist ausschließlich für
 * die Erstellung und Anzeige der grafischen Benutzeroberfläche (GUI)
 * mit Swing verantwortlich. Sie enthält keine Anwendungslogik.
 */
public class AgentView extends JFrame {

   // Fonts
   public static final Font defaultFont = ShowFonts.createSafeFont("Arial",
         "SansSerif", Font.PLAIN, 16);
   public static final Font dialogFont = ShowFonts.createSafeFont("Arial",
         "SansSerif", Font.PLAIN, 16);
   public static final Font monoFont = ShowFonts.createSafeFont("Monospaced",
         "Monospaced", Font.PLAIN, 15);

   // Deklaration der GUI-Komponenten als private Felder.
   private final JTextArea questionArea;
   private final JTextArea sqlArea;
   private final JButton sendButton;
   private final JButton clearButton;
   private final JButton executeButton;
   private final JButton explainSqlButton;
   private final JButton exportButton;
   private final JButton exitButton;
   private final JTable resultTable;
   private final JPanel resultPanel;
   private final TitledBorder resultBorder;

   /**
    * Konstruktor, der die GUI initialisiert und aufbaut.
    */
   public AgentView() {

      // --- Fenster-Setup ---
      setTitle("Getränkemarkt SQL-Agent");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000, 850);
      setLocationRelativeTo(null); // Fenster zentrieren

      // --- Komponenten initialisieren ---
      questionArea = new JTextArea(5, 50);
      questionArea.setFont(defaultFont);
      questionArea.setLineWrap(true);
      sqlArea = new JTextArea(8, 50);
      sqlArea.setFont(monoFont);
      sqlArea.setEditable(false);
      sqlArea.setLineWrap(false);

      sendButton = new JButton("Anfrage senden");
      clearButton = new JButton("Leeren");
      executeButton = new JButton("SQL ausführen");
      explainSqlButton = new JButton("SQL erklären");
      exportButton = new JButton("Als CSV exportieren");
      exitButton = new JButton("Beenden");

      resultTable = new JTable();
      resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      // benutzerdefiniertenrRenderer für verschiedene Datentypen
      resultTable.setDefaultRenderer(Object.class,
            new CustomTableCellRenderer());
      resultTable.setDefaultRenderer(Number.class,
            new CustomTableCellRenderer());
      resultTable.setDefaultRenderer(Boolean.class,
            new CustomTableCellRenderer());

      // --- Layout und Panels erstellen ---
      final JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
      mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      // Eingabe-Panel (oben)
      final JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
      inputPanel.setBorder(
            new TitledBorder("1. Stelle deine Frage in natürlicher Sprache"));
      inputPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);

      final JPanel inputButtonPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT));
      inputButtonPanel.add(sendButton);
      inputButtonPanel.add(clearButton);
      inputPanel.add(inputButtonPanel, BorderLayout.SOUTH);

      // SQL-Panel (mitte)
      final JPanel sqlPanel = new JPanel(new BorderLayout(5, 5));
      sqlPanel.setBorder(
            new TitledBorder("2. Überprüfe den generierten SQL-Befehl"));
      sqlPanel.add(new JScrollPane(sqlArea), BorderLayout.CENTER);

      final JPanel executeButtonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER));
      executeButtonPanel.add(executeButton);
      executeButtonPanel.add(explainSqlButton);
      executeButtonPanel.add(exportButton);
      sqlPanel.add(executeButtonPanel, BorderLayout.SOUTH);

      // Ergebnis-Panel (unten)
      resultPanel = new JPanel(new BorderLayout());

      // Erstellen und Zuweisen des TitledBorder als Feld
      resultBorder = new TitledBorder("3. Ergebnis der Datenbankabfrage");
      resultPanel.setBorder(resultBorder);
      resultPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

      // Exit-Button Panel (ganz unten)
      final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      southPanel.add(exitButton);

      // Panels dem Haupt-Panel hinzufügen
      final JPanel topContainer = new JPanel(new BorderLayout(10, 10));
      topContainer.add(inputPanel, BorderLayout.NORTH);
      topContainer.add(sqlPanel, BorderLayout.CENTER);

      mainPanel.add(topContainer, BorderLayout.NORTH);
      mainPanel.add(resultPanel, BorderLayout.CENTER);
      mainPanel.add(southPanel, BorderLayout.SOUTH);

      // Haupt-Panel dem Fenster hinzufügen
      add(mainPanel);

      // Initialer Zustand der Buttons
      setExecuteAndExportEnabled(false);
   }

   public JButton getClearButton() {
      return clearButton;
   }

   public JButton getExecuteButton() {
      return executeButton;
   }

   public JButton getExitButton() {
      return exitButton;
   }

   public JButton getExplainSqlButton() {
      return explainSqlButton;
   }

   public JButton getExportButton() {
      return exportButton;
   }

   public JTextArea getQuestionArea() {
      return questionArea;
   }

   public JButton getSendButton() {
      return sendButton;
   }

   public JTextArea getSqlArea() {
      return sqlArea;
   }

   public void setExecuteAndExportEnabled(final boolean enabled) {
      executeButton.setEnabled(enabled);
      explainSqlButton.setEnabled(enabled);
      exportButton.setEnabled(enabled);
   }

   public void setSqlText(final String sql) {
      sqlArea.setText(sql);
   }

   public void updateResultTable(final TableModel model) {
      resultTable.setModel(model);

      // Stellt sicher, dass jede Spalte mindestens so breit ist wie ihre
      // Überschrift.
      for (int i = 0; i < resultTable.getColumnCount(); i++) {
         final TableColumn column = resultTable.getColumnModel()
            .getColumn(i);

         // Hole die Renderer-Komponente für den Header.
         TableCellRenderer headerRenderer = column.getHeaderRenderer();
         if (headerRenderer == null) {
            headerRenderer = resultTable.getTableHeader()
               .getDefaultRenderer();
         }
         final Component headerComp = headerRenderer
            .getTableCellRendererComponent(resultTable, column.getHeaderValue(),
                  false, false, 0, i);

         // Berechne die bevorzugte Breite der Header-Komponente.
         int headerWidth = headerComp.getPreferredSize().width;
         headerWidth = Math.max(headerWidth, 50); // Mindestens 50 Pixel

         // Setze die bevorzugte Breite der Spalte. Ich füge 10 Pixel als Puffer
         // hinzu, damit der Text nicht direkt am Rand klebt.
         column.setPreferredWidth(headerWidth + 10);
      }
   }

   /**
    * Aktualisiert den Titel des Ergebnis-Panels mit der Anzahl der Zeilen.
    *
    * @param rowCount Die Anzahl der Zeilen im Ergebnis.
    */
   public void updateResultTitle(final int rowCount) {
      resultBorder.setTitle(
            "3. Ergebnis der Datenbankabfrage (" + rowCount + " Zeilen)");
      resultPanel.repaint();
   }
}