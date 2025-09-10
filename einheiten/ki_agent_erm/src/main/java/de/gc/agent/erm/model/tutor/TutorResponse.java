package de.gc.agent.erm.model.tutor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Diese Klasse repräsentiert die strukturierte Antwort eines
 * Tutor-Agenten.
 *
 * Sie beinhaltet die textuelle Antwort des Lernassistenten sowie eine
 * Liste von Vorschlagsfragen für die weitere Navigation im Lernprozess.
 *
 * @param answer             Die ausführliche, textuelle Antwort des
 *                           Agenten.
 * @param suggestedQuestions Eine Liste von vorgeschlagenen Folgefragen.
 */
public record TutorResponse(
      @JsonProperty("answer") String answer,
      @JsonProperty("suggested_questions") List<String> suggestedQuestions) {
}
