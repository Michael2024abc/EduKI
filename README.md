# EduKI

> Ein praxisnaher KI-Lernpfad für Fachinformatiker in der Anwendungsentwicklung.

![Lizenz: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)

<img src="images/edukik.png" alt="EduKI Logo" align="left" style="margin-right: 15px;" />

**Hey, schön, dass Ihr da seid!**

Mit **EduKI** startet Ihr ins Abenteuer Künstliche Intelligenz in der Softwareentwicklung – packt die Zukunft an und seid vorne mit dabei! Ob Ihr später als Entwickler durchstarten oder einfach moderne Tools verstehen wollt: Hier bekommt Ihr praxisnahe Infos und Beispiele, die Euch wirklich weiterbringen.

Ihr steckt mitten in der Ausbildung zum Fachinformatiker bzw. zur Fachinformatikerin für Anwendungsentwicklung? Super, dann seid Ihr hier goldrichtig! Alle Beispiele sind so aufgebaut, dass Ihr sie Schritt für Schritt nachbauen und dabei direkt ausprobieren könnt, wie KI und Java gemeinsam richtig was bewegen.

***

## Inhaltsverzeichnis
- [Verwendete Technologien](#verwendete-technologien)
- [Erste Schritte](#erste-schritte)
- [Warum KI? Weil Eure Zukunft es braucht!](#warum-ki-weil-eure-zukunft-es-braucht)
- [Was erwartet Euch?](#was-erwartet-euch)
- [Projekte](#projekte)
  - [DB-Genius – Dein eigener KI-Agent in Java](#db-genius--dein-eigener-ki-agent-in-java)
  - [Getränkemarkt SQL-Agent – Dein eigener KI-Agent in Java](#getränkemarkt-sql-agent--dein-eigener-ki-agent-in-java)
  - [KI-gestützter ERM-Editor](#ki-gestützter-erm-editor)
- [Über den Tellerrand hinaus schauen](#über-den-tellerrand-hinaus-schauen)
  - [KI-Agenten im Unterricht: Ein Praxisleitfaden für Lehrkräfte](#ki-agenten-im-unterricht-ein-praxisleitfaden-für-lehrkräfte)

***

## Verwendete Technologien

*   **Programmiersprache:** ![Java](https://img.shields.io/badge/Java-OpenJDK%2021-blue?logo=openjdk)
*   **KI-Framework:** `langchain4j`
*   **KI-Dienste:** Gemini, GitHub Copilot, Ollama
*   **Datenbank:** MariaDB

***

## Erste Schritte

Um die Projekte lokal auszuführen und damit zu experimentieren, benötigst du die folgende Software auf deinem System.

#### Voraussetzungen
*   Java Development Kit (OpenJDK 21 oder höher)
*   Apache Maven (zur Verwaltung der Projektabhängigkeiten)
*   Git (zur Versionskontrolle)

#### Installation
1.  Klone dieses Repository auf deinen Computer:
    ```bash
    git clone https://github.com/Michael2024abc/EduKI.git
    ```

2.  Navigiere in das heruntergeladene Projektverzeichnis:
    ```bash
    cd EduKI
    ```
3.  Jedes Projekt befindet sich in einem eigenen Unterordner (z.B. unter `/einheiten/`). Folge den spezifischen Anweisungen in der `README.md` des jeweiligen Projekt-Ordners, um es zu starten.

***

## Warum KI? Weil Eure Zukunft es braucht!

KI krempelt die IT gerade so richtig um. Wer als Entwickler nicht abgehängt werden will, muss
- checken, wie KI funktioniert,
- wissen, wo KI Sinn macht (und wo nicht!),
- immer bereit sein, Neues auszuprobieren und Bestehendes zu verbessern.

Mit **EduKI** seht Ihr ganz konkret, wie KI (z.B. Gemini oder Copilot) Euch Routineaufgaben abnimmt, kreative Lösungen liefert und Eure Arbeit im Team viel effizienter macht.
Solche Skills wollt Ihr auf dem Arbeitsmarkt haben – versprochen!

> *„Wer KI durchschaut und clever nutzt, gestaltet die digitale Zukunft. Nicht irgendwann – jetzt.“*

Macht **EduKI** zu Eurem Werkzeugkasten, mit dem Ihr nicht einfach nur Aufgaben erledigt, sondern aktiv Innovationen anstoßt!

***

## Was erwartet Euch?

Hier findet Ihr eine Übersicht über die Projekte, mit denen Ihr KI und Java im echten Berufsalltag trainiert – keine trockene Theorie, sondern direkt anwendbar.

### Projekte

#### **DB-Genius – Dein eigener KI-Agent in Java**

Habt Ihr Euch schon mal gewünscht, mit Eurer Datenbank einfach wie mit einem Kumpel zu reden?
Mit **DB-Genius** baut Ihr Schritt für Schritt einen Java-Agenten, der zwischen Euch und einer MariaDB-Datenbank vermittelt.

**Warum ist das cool?**
- Ihr könnt den Agenten ganz easy über die Konsole starten.
- Ihr stellt Eure Fragen direkt und auf Deutsch, z. B.:
  - „Wie viele Schüler haben wir?“
  - „Liste alle aus der Klasse FIAE-23a auf.“
- Der Agent versteht Euch, übersetzt das Ganze in kluge SQL-Befehle, holt sich die Infos aus der Datenbank und gibt sie Euch verständlich (und auf Deutsch) zurück.

Ihr erlebt am eigenen Projekt, wie KI Sprache versteht, clever übersetzt und Probleme löst, wie sie im (Schul-)Alltag wirklich vorkommen. Nebenbei werdet Ihr einfach besser in Java, SQL und modernen Schnittstellen.

➡️ [Hier geht's zum Quellcode](https://github.com/Michael2024abc/EduKI/tree/main/einheiten/ki_agent_einfach)


**📖 Dokumentation & Theorie**

Alle theoretischen Grundlagen, detaillierte Erklärungen zum Code und die Hintergründe zu den verwendeten KI-Konzepten findest du im ausführlichen Unterrichtsskript. Es ist die perfekte Ergänzung zu den praktischen Übungen in diesem Repository.

➡️ **[Hier geht's zum vollständigen Unterrichtsskript (PDF)](https://michael2024abc.github.io/EduKI/af_dbagent.pdf)**

➡️ **[Ergänzung: KI, Docker und MariaDB (PDF)](https://michael2024abc.github.io/EduKI/af_docker_db.pdf)**

#### **Getränkemarkt SQL-Agent – Dein eigener KI-Agent in Java**

Ein weiteres spannendes Projekt ist der 'Getränkemarkt SQL-Agent', der das Konzept des DB-Genius auf eine spezielle Domäne anwendet.
Wir entwickeln den "Getränkemarkt SQL-Agenten", eine Anwendung mit einer grafischen Benutzeroberfläche (GUI).

Ihr könnt ihm, wie gewohnt, Fragen in natürlicher Sprache stellen, zum Beispiel:
"Welche Artikel von der Marke 'Karlsquell' haben wir im Sortiment?".

Doch anstatt nur eine Antwort zu geben, geht dieser Agent einen entscheidenden Schritt weiter:
Er generiert den passenden SQL-Befehl und zeigt ihn euch an. Damit habt ihr die volle Kontrolle und könnt verschiedene Aktionen auslösen:

*   **SQL ausführen:**<br>
    Führt die Abfrage auf der MariaDB-Datenbank aus und zeigt die Ergebnisse direkt in einer übersichtlichen Tabelle an.
*   **SQL erklären lassen:**<br>
    Wenn ihr neugierig seid, wie der SQL-Befehl funktioniert, kann die KI ihn euch Schritt für Schritt auf Deutsch erläutern.
*   **Als CSV exportieren:**<br>
    Für den späteren Gebrauch in anderen Programmen wie Excel könnt ihr die Ergebnisse mit einem Klick als CSV-Datei speichern.

➡️ [Hier geht's zum Quellcode](https://github.com/Michael2024abc/EduKI/tree/main/einheiten/ki_agent_gm)

➡️ **[Skript öffnen (PDF)](https://michael2024abc.github.io/EduKI/af_agent_gm.pdf)**

➡️ **[Tabellenmodell öffnen (PDF)](https://michael2024abc.github.io/EduKI/tables_gm.pdf)**

---

#### **KI-gestützter ERM-Editor**

Dieses Projekt ist ein umfassendes Werkzeug, das euch durch den gesamten Prozess des Datenbank-Entwurfs führt – von der ersten Idee bis zum fertigen SQL-Code.

Der Editor nutzt KI-Agenten, um euch bei jedem Schritt zu unterstützen:
*   **Textanalyse:** Eine KI extrahiert aus einer einfachen, textuellen Beschreibung die relevanten Entitäten und Beziehungen.
*   **Modellerstellung:** Die Anwendung generiert automatisch ein konzeptionelles ER-Modell und überführt dieses in ein logisches Tabellenmodell.
*   **SQL-Generierung:** Am Ende wird das fertige SQL-DDL-Skript erzeugt, mit dem ihr die Datenbankstruktur aufbauen könnt.
*   **Interaktive Tutoren:** In jedem Schritt könnt ihr einen KI-Tutor um Hilfe bitten, der euch die Konzepte und den Code erklärt.

Dieses Projekt ist ideal, um den professionellen Workflow des Datenbank-Designs zu erlernen und die Zusammenarbeit zwischen Mensch und KI in einem komplexeren Entwicklungsprozess zu erleben.

➡️ [Hier geht's zum Quellcode](https://github.com/Michael2024abc/EduKI/tree/main/einheiten/ki_agent_erm)

➡️ **[Benutzerhandbuch öffnen (PDF)](https://michael2024abc.github.io/EduKI/af_ermagent.pdf)**

---
### Über den Tellerrand hinaus schauen

#### **KI-Agenten im Unterricht: Ein Praxisleitfaden für Lehrkräfte**

Dieses Skript richtet sich an Lehrkräfte und alle, die neugierig sind, wie KI den Unterricht revolutionieren kann. Entdeckt, wie Ihr mit dem KI-System "Le Chat" von Mistral AI maßgeschneiderte Unterrichtseinheiten für Fächer wie "Politik & Gesellschaft" erstellen könnt. Der Leitfaden zeigt praxisnah, wie man einen eigenen KI-Agenten konfiguriert, der nicht nur bei der Erstellung von Materialien hilft, sondern auch moderne didaktische Konzepte wie Escape Games vorschlägt.

Ein besonderer Fokus liegt auf dem verantwortungsvollen Einsatz von KI: **"Le Chat" ist DSGVO-konform** und kann ohne Anmeldung genutzt werden, wodurch der Schutz persönlicher Daten stets gewährleistet ist. Ein idealer Einstieg, um die Potenziale von KI im Bildungskontext sicher und kreativ zu erproben.

➡️ **[Hier geht's zum Skript für Lehrkräfte (PDF)](https://michael2024abc.github.io/EduKI/af_pug_agent.pdf)**

➡️ **Zugehörige Materialien:**
- **[System-Prompt (Anweisungen)](einheiten/prompts/pug_prompt.md)**
- **[Leitplanken (Richtlinien)](einheiten/prompts/pug_leitlinien.md)**

➡️ **[Le Chat direkt ausprobieren](https://chat.mistral.ai/chat)**



---

#### **Weitere Projekte & Aufgaben**

Das EduKI-Repository wächst ständig weiter.
Ihr bekommt regelmäßig neue Aufgaben und Challenges aus der echten Praxis – zum Ausprobieren, Mitdenken und Weiterentwickeln.
Langweilig wird’s hier bestimmt nicht. Bleibt dran und macht mit – Innovation fängt bei Euch an!

***

**Kurz gesagt:**
Mit EduKI holt Ihr Euch Skills, von denen andere nur träumen. Probiert Projekte aus, habt Spaß beim Coden – und gestaltet die digitale Welt von morgen. Los geht’s
