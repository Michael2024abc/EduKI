# Getränkemarkt SQL-Agent

> Ein KI-gestützter Agent mit grafischer Benutzeroberfläche zur Abfrage einer Datenbank in natürlicher Sprache.

Dieses Projekt ist Teil des [EduKI Lernpfads](https://github.com/Michael2024abc/EduKI), einem praxisnahen KI-Lernpfad für Fachinformatiker.

---

## Worum geht es?

Der "Getränkemarkt SQL-Agent" ist ein fortgeschrittenes Projekt, das das Konzept eines KI-Agenten auf eine spezielle Domäne anwendet: die Abfrage einer Getränkemarkt-Datenbank.

Wir entwickeln eine Anwendung mit einer grafischen Benutzeroberfläche (GUI), der man, wie gewohnt, Fragen in natürlicher Sprache stellen kann, zum Beispiel:
`"Welche Artikel von der Marke 'Karlsquell' haben wir im Sortiment?"`

Doch anstatt nur eine Antwort zu geben, geht dieser Agent einen entscheidenden Schritt weiter: Er generiert den passenden SQL-Befehl und zeigt ihn dem Benutzer an. Damit hat man die volle Kontrolle und kann verschiedene Aktionen auslösen.

### Features

*   **SQL ausführen:**
    Führt die generierte SQL-Abfrage direkt auf der MariaDB-Datenbank aus und zeigt die Ergebnisse in einer übersichtlichen Tabelle an.
*   **SQL erklären lassen:**
    Wenn man neugierig ist, wie der SQL-Befehl funktioniert, kann die KI ihn Schritt für Schritt auf Deutsch erläutern.
*   **Als CSV exportieren:**
    Für den späteren Gebrauch in anderen Programmen wie Excel kann man die Ergebnisse mit einem Klick als CSV-Datei speichern.

***

## Verwendete Technologien

*   **Programmiersprache:** ![Java](https://img.shields.io/badge/Java-OpenJDK%2021-blue?logo=openjdk)
*   **KI-Framework:** `langchain4j`
*   **KI-Dienste:** Gemini, GitHub Copilot, Ollama
*   **Datenbank:** MariaDB

***

## Dokumentation

Weiterführende Informationen, theoretische Grundlagen und eine detaillierte Erklärung des Projekts findest du in den folgenden Dokumenten:

➡️ **[Zum Unterrichtsskript (PDF)](https://michael2024abc.github.io/EduKI/af_agent_gm.pdf)**

➡️ **[Zum Tabellenmodell (PDF)](https://michael2024abc.github.io/EduKI/tables_gm.pdf)**