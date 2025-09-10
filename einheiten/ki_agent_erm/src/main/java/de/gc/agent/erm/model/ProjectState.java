package de.gc.agent.erm.model;

import java.util.List;

/**
 * Ein unveränderlicher Datencontainer, der den gesamten Zustand des
 * ERM-Editor-Projekts zu einem bestimmten Zeitpunkt repräsentiert. Dies
 * wird für das Speichern und Laden des gesamten Arbeitsstandes
 * verwendet.
 *
 * Die ProjectState-Instanz hält alle wesentlichen Informationsbausteine
 * eines Modellierungsprojekts, darunter die textuelle Beschreibung, die
 * extrahierten Beziehungen, den konzeptionellen und logischen
 * PlantUML-Code sowie das generierte SQL-DDL-Skript.
 *
 * @param description        Der Text aus dem Beschreibungsfeld.
 * @param relationships      Die Liste der analysierten Beziehungen.
 * @param ermPlantUml        Der PlantUML-Code für das konzeptionelle
 *                           ERM-Diagramm.
 * @param tableModelPlantUml Der PlantUML-Code für das logische
 *                           Tabellenmodell.
 * @param sqlDdl             Das generierte SQL-DDL-Skript.
 */
public record ProjectState(
      String description,
      List<Relationship> relationships,
      String ermPlantUml,
      String tableModelPlantUml,
      String sqlDdl) {
}
