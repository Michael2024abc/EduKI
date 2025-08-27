package de.gc.agent.gm.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Ein kleines Hilfsprogramm, um verfügbare Schriftarten aufzulisten und
 * sichere Font-Objekte zu erstellen.
 */
public class ShowFonts {

   // Ein Set aller verfügbaren Schriftfamiliennamen
   // für eine schnelle Suche.
   private static final Set<String> AVAILABLE_FONT_FAMILIES;

   // Statischer Initialisierer, um die Liste der Schriftarten
   // nur einmal zu laden.
   static {
      final GraphicsEnvironment ge = GraphicsEnvironment
         .getLocalGraphicsEnvironment();
      final String[] fontNames = ge.getAvailableFontFamilyNames();
      AVAILABLE_FONT_FAMILIES = new HashSet<>(Arrays.asList(fontNames));
   }

   /**
    * Erstellt ein Font-Objekt mit der bevorzugten Schriftart. Wenn diese
    * nicht verfügbar ist, wird die Fallback-Schriftart verwendet.
    *
    * @param preferredFont Der Name der gewünschten Schriftart (z.B.
    *                      "Arial").
    * @param fallbackFont  Der Name der sicheren Fallback-Schriftart (z.B.
    *                      "SansSerif").
    * @param style         Der Schriftschnitt (z.B. Font.PLAIN).
    * @param size          Die Schriftgröße.
    *
    * @return Das erstellte Font-Objekt.
    */
   public static Font createSafeFont(final String preferredFont,
         final String fallbackFont,
         final int style, final int size) {
      if (AVAILABLE_FONT_FAMILIES.contains(preferredFont)) {
         // Bevorzugte Schriftart ist verfügbar
         return new Font(preferredFont, style, size);
      } else {
         // Fallback verwenden
         System.out.println("Warnung: Schriftart '" + preferredFont
               + "' nicht gefunden. Verwende Fallback '" + fallbackFont + "'.");
         return new Font(fallbackFont, style, size);
      }
   }

   public static void main(final String[] args) {

      AVAILABLE_FONT_FAMILIES.stream()
         .sorted(String::compareToIgnoreCase)
         .forEach(font -> {
            System.out.println(font);
         });

   }
}
