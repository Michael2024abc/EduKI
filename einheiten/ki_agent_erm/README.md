# KI-gestützter ERM-Editor

> Ein umfassendes Werkzeug mit grafischer Benutzeroberfläche, das den gesamten Prozess des Datenbank-Entwurfs mit Hilfe von KI-Agenten unterstützt.

Dieses Projekt ist Teil des [EduKI Lernpfads](https://github.com/Michael2024abc/EduKI), einem praxisnahen KI-Lernpfad für Fachinformatiker.

---

## Worum geht es?

Der KI-gestützte ERM-Editor ist ein didaktisches Werkzeug, das Schüler durch den professionellen Workflow des Datenbank-Designs führt – von der ersten textuellen Idee bis zum fertigen, lauffähigen SQL-Skript.

Die Anwendung nutzt eine Kette von spezialisierten KI-Agenten, um den Benutzer bei jedem Schritt aktiv zu unterstützen.

### Features

*   **Textanalyse:** Eine KI extrahiert aus einer einfachen, in natürlicher Sprache verfassten Beschreibung die relevanten Entitäten, Attribute und Beziehungen.
*   **Modellerstellung:** Die Anwendung generiert aus den analysierten Daten automatisch ein konzeptionelles ER-Modell (ERM) und überführt dieses anschließend in ein detailliertes, logisches Tabellenmodell.
*   **SQL-Generierung:** Aus dem logischen Modell wird auf Knopfdruck das finale SQL-DDL-Skript erzeugt, mit dem die Datenbankstruktur auf einem Server (z.B. MariaDB) aufgebaut werden kann.
*   **Interaktive Tutoren:** In jedem Schritt kann ein kontextsensitiver KI-Tutor um Hilfe gebeten werden, der die aktuellen Diagramme, den Code oder allgemeine Konzepte erklärt.
*   **Flexible Konfiguration:** Die Anwendung kann über die Kommandozeile mit verschiedenen KI-Anbietern (Gemini, Ollama, GitHub Copilot) und Konfigurationen gestartet werden.

***

## Verwendete Technologien

*   **Programmiersprache:** ![Java](https://img.shields.io/badge/Java-OpenJDK%2021-blue?logo=openjdk)
*   **UI-Framework:** ![JavaFX](https://img.shields.io/badge/JavaFX-21-orange?logo=openjfx)
*   **KI-Framework:** `langchain4j`
*   **KI-Dienste:** Gemini, GitHub Copilot, Ollama
*   **Build-Tool:** Apache Maven

***

## Dokumentation

Eine vollständige Anleitung zur Installation, Konfiguration, Bedienung und zur technischen Architektur des Projekts findest du im ausführlichen Benutzerhandbuch.

➡️ **[Zum Benutzerhandbuch (PDF)](https://michael2024abc.github.io/EduKI/af_ermagent.pdf)**

