package de.gc.agent.gm.view;

import java.awt.Component;
import java.sql.Date;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Ein benutzerdefinierter TableCellRenderer, der die Ausrichtung von
 * Zelleninhalten basierend auf deren Datentyp steuert.
 * <ul>
 * <li>Zahlen werden rechtsbündig ausgerichtet.</li>
 * <li>Boolean-Werte (Checkboxen) werden zentriert.</li>
 * <li>Alle anderen Typen werden linksbündig ausgerichtet.</li>
 * </ul>
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {

   @Override
   public Component getTableCellRendererComponent(final JTable table,
         final Object value,
         final boolean isSelected, final boolean hasFocus, final int row,
         final int column) {

      // Zuerst die Standard-Komponente von der Superklasse holen.
      // Diese kümmert sich um Hintergrundfarben, Schriftarten etc.
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
            row, column);

      // Jetzt die Ausrichtung basierend auf dem Datentyp anpassen.
      if (value instanceof Number) {
         setHorizontalAlignment(SwingConstants.RIGHT);
      } else if (value instanceof Boolean || value instanceof Date) {
         setHorizontalAlignment(SwingConstants.CENTER);
      } else {
         setHorizontalAlignment(SwingConstants.LEFT);
      }

      return this;
   }
}