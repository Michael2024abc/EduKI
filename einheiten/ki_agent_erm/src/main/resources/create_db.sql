-- Ausführen als DB-Administrator, normalerweise 'root'.

-- Wechsel zur Hauptdatenbank für die Verwaltung.
USE mysql;

-- Erstellt eine neue Datenbank, falls sie noch existiert, vorher löschen.
DROP DATABASE IF EXISTS ermagent;
CREATE DATABASE ermagent CHARACTER SET utf8;

-- Erstellt einen neue User, falls diese noch existiert, vorher löschen.
-- User 'dbagent', PW ist leer
DROP USER IF EXISTS 'ermagent'@'localhost';
CREATE USER 'ermagent'@'localhost' IDENTIFIED BY '';

-- Rechte vergeben.
GRANT ALL PRIVILEGES ON ermagent.* TO 'ermagent'@'localhost';

-- Rechte aktualisieren.
FLUSH PRIVILEGES;

-- Wählt die neue Datenbank für die folgenden Befehle aus.
USE ermagent;
