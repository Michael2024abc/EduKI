package de.gc.agent.erm.model;

import javafx.scene.image.Image;

/**
 * Repräsentiert das Ergebnis eines Generierungsprozesses, das aus einem
 * Text und optional einem generierten Bild besteht.
 *
 * @param textContent Das generierte textuelle Ergebnis.
 * @param image       Die zugehörige, generierte Bildressource.
 */
public record GenerationResult(String textContent, Image image) {
}
