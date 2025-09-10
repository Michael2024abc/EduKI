package de.gc.agent.erm.view;

import java.util.List;

import de.gc.agent.erm.model.tutor.TutorResponse;
import de.gc.agent.erm.model.tutor.TutorStrategy;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller für den Tutor-Dialog.
 *
 * Koordiniert die Anzeige und Verarbeitung von Tutor-Konversationen
 * inklusive Antwortvorschlägen, History und UI-Interaktionen.
 */
public class TutorDialogController {

   /** Schrittweite für die Schriftgröße im Tutor-Chat. */
   private static final double FONT_STEP = 1.0;
   /** Minimale Schriftgröße für den Chat. */
   private static final double MIN_FONT_SIZE = 10.0;
   /** Maximale Schriftgröße für den Chat. */
   private static final double MAX_FONT_SIZE = 24.0;

   @FXML
   private Label titleLabel;
   @FXML
   private ListView<VBox> chatListView;

   @FXML
   private VBox suggestionsBox;
   @FXML
   private TextField inputTextField;

   @FXML
   private Button sendButton;
   /** Die Tutor-Strategie des aktuellen Dialogs. */
   private TutorStrategy tutorStrategy;
   /** Kontextdaten des Dialogs (z.B. Diagrammcode). */
   private String context;
   /** Aktuelle Schriftgröße im Chat-Bereich. */
   private double currentFontSize = 14.0;

   /**
    * Fügt eine Tutor-Nachricht zur Chat-Historie hinzu.
    *
    * @param message Die Tutor-Antwort.
    */
   private void addTutorMessage(final String message) {
      final VBox messageContainer = createMessageContainer("Tutor:", message,
            Pos.CENTER_LEFT);
      chatListView.getItems()
         .add(messageContainer);
      chatListView.scrollTo(chatListView.getItems()
         .size() - 1);
   }

   /**
    * Fügt eine Nutzer-Nachricht zur Chat-Historie hinzu.
    *
    * @param message Die Nutzer-Frage.
    */
   private void addUserMessage(final String message) {
      final VBox messageContainer = createMessageContainer("Du:", message,
            Pos.CENTER_RIGHT);
      chatListView.getItems()
         .add(messageContainer);
      chatListView.scrollTo(chatListView.getItems()
         .size() - 1);
   }

   /**
    * Passt die Höhe einer TextArea dynamisch an den Inhalt an.
    *
    * @param textArea Die zu bearbeitende TextArea.
    */
   private void adjustTextAreaHeight(final TextArea textArea) {
      Platform.runLater(() -> {
         final Node textNode = textArea.lookup(".text");
         if (textNode instanceof Text) {
            textArea.prefHeightProperty()
               .bind(Bindings.createDoubleBinding(
                     () -> textNode.getLayoutBounds()
                        .getHeight() + 20,
                     textNode.layoutBoundsProperty()));
         }
      });
   }

   /**
    * Erzeugt einen Nachricht-Container für Tutor/Schüler inklusive
    * Kopierfunktion.
    *
    * @param author    Absender ("Tutor:" oder "Du:")
    * @param content   Inhalt der Nachricht.
    * @param alignment Ausrichtung im Chat-Fenster.
    *
    * @return VBox für die Nachrichtanzeige.
    */
   private VBox createMessageContainer(final String author,
         final String content, final Pos alignment) {

      final TextArea contentArea = new TextArea(content);
      contentArea.setEditable(false);
      contentArea.setWrapText(true);
      contentArea.getStyleClass()
         .add("chat-message-area");

      final Node header;
      final VBox container;

      if ("Tutor:".equals(author)) {
         final Label authorLabel = new Label(author);
         authorLabel.setStyle("-fx-font-weight: bold;");

         final Button copyButton = new Button("Kopieren");
         copyButton.getStyleClass()
            .add("copy-button");
         copyButton.setTooltip(
               new Tooltip("Antworttext in die Zwischenablage kopieren"));
         copyButton.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);
         });

         final Pane spacer = new Pane();
         HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
         final HBox headerBox = new HBox(authorLabel, spacer, copyButton);
         headerBox.setAlignment(Pos.CENTER_LEFT);
         header = headerBox;

         container = new VBox(5, header, contentArea);
         container.getStyleClass()
            .add("tutor-row");

      } else { // User
         final Label authorLabel = new Label(author);
         authorLabel.setStyle("-fx-font-weight: bold;");
         header = authorLabel;

         container = new VBox(5, header, contentArea);
         container.getStyleClass()
            .add("user-row");
      }

      contentArea.textProperty()
         .addListener((obs, oldVal, newVal) -> adjustTextAreaHeight(
               contentArea));
      adjustTextAreaHeight(contentArea);

      container.setAlignment(alignment);
      container.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));
      return container;
   }

   /**
    * Initialisiert den Tutor-Dialog mit Strategie und Kontext.
    *
    * @param tutorStrategy Die zu verwendende Tutor-Strategie.
    * @param context       Kontextdaten (z.B. Diagramm-Code).
    */
   public void initialize(final TutorStrategy tutorStrategy,
         final String context) {
      this.tutorStrategy = tutorStrategy;
      this.context = context;

      titleLabel.setText(tutorStrategy.getDialogTitle());
      updateChatFontSize();

      inputTextField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
         if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
            onSendMessage();
         }
      });

      processResponse(tutorStrategy.getInitialResponse());
   }

   /**
    * Verkleinert die Schrift im Chat-Bereich.
    */
   @FXML
   private void onDecreaseFontSize() {
      if (currentFontSize > MIN_FONT_SIZE) {
         currentFontSize -= FONT_STEP;
         updateChatFontSize();
      }
   }

   /**
    * Vergrößert die Schrift im Chat-Bereich.
    */
   @FXML
   private void onIncreaseFontSize() {
      if (currentFontSize < MAX_FONT_SIZE) {
         currentFontSize += FONT_STEP;
         updateChatFontSize();
      }
   }

   /**
    * Sendet die Schülerfrage an den Tutor und verarbeitet die Antwort.
    */
   @FXML
   private void onSendMessage() {
      final String userInput = inputTextField.getText();
      if (userInput == null || userInput.isBlank()) {
         return;
      }
      addUserMessage(userInput);
      final String userQuestion = inputTextField.getText();
      inputTextField.clear();

      final Task<TutorResponse> task = new Task<>() {
         @Override
         protected TutorResponse call() throws Exception {
            return tutorStrategy.getResponse(context, userQuestion);
         }
      };

      chatListView.getScene()
         .cursorProperty()
         .bind(
               Bindings.when(task.runningProperty())
                  .then(Cursor.WAIT)
                  .otherwise(Cursor.DEFAULT));

      task.setOnSucceeded(event -> processResponse(task.getValue()));
      task.setOnFailed(event -> {
         task.getException()
            .printStackTrace();
         addTutorMessage("Entschuldigung, es ist ein Fehler aufgetreten: "
               + task.getException()
                  .getMessage());
      });

      new Thread(task).start();
   }

   /**
    * Verarbeitet die TutorResponse: Anzeige und Vorschlagsupdate.
    *
    * @param response TutorResponse mit Antwort und Fragenvorschlägen.
    */
   private void processResponse(final TutorResponse response) {
      addTutorMessage(response.answer());
      updateSuggestions(response.suggestedQuestions());
   }

   /**
    * Aktualisiert die Schriftgröße im ListView-Chatbereich.
    */
   private void updateChatFontSize() {
      chatListView.setStyle("-fx-font-size: " + currentFontSize + "px;");
   }

   /**
    * Aktualisiert die Anzeige der Vorschlagsfragen im Suggestion-Bereich.
    *
    * @param suggestions Liste der neuen Vorschlagsfragen.
    */
   private void updateSuggestions(final List<String> suggestions) {
      suggestionsBox.getChildren()
         .clear();
      if (suggestions == null) {
         return;
      }

      for (final String suggestion : suggestions) {
         final Button button = new Button(suggestion);
         button.setMaxWidth(Double.MAX_VALUE);
         button.setAlignment(Pos.CENTER_LEFT);
         button.setOnAction(e -> {
            inputTextField.setText(suggestion);
            onSendMessage();
         });
         suggestionsBox.getChildren()
            .add(button);
      }
   }
}
