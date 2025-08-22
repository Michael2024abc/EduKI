-- Ausführen als DB-Administrator, normalerweise 'root'.

-- Wechsel zur Hauptdatenbank für die Verwaltung.
USE mysql;

-- Erstellt eine neue Datenbank, falls sie noch existiert, vorher löschen.
DROP DATABASE IF EXISTS dbagent;
CREATE DATABASE dbagent CHARACTER SET utf8;

-- Erstellt einen neue User, falls diese noch existiert, vorher löschen.
-- User 'dbagent', PW ist leer
DROP USER IF EXISTS 'dbagent'@'localhost';
CREATE USER 'dbagent'@'localhost' IDENTIFIED BY '';

-- Rechte vergeben.
GRANT ALL PRIVILEGES ON dbagent.* TO 'dbagent'@'localhost';

-- Rechte aktualisieren.
FLUSH PRIVILEGES;

-- Wählt die neue Datenbank für die folgenden Befehle aus.
USE dbagent;

-- Erstellt die Tabelle 'schueler' mit den notwendigen Spalten.
CREATE TABLE schueler (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vorname VARCHAR(50),
    nachname VARCHAR(50),
    klasse VARCHAR(20),
    eintrittsdatum DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Fügt Beispieldaten in unsere Tabelle ein.
-- Diese Daten wird unser Agent später abfragen.
INSERT INTO schueler (vorname, nachname, klasse, eintrittsdatum) VALUES
('Anna', 'Schmidt', 'FIAE-23a', '2023-08-01'),
('Ben', 'Müller', 'FIAE-23b', '2023-08-01'),
('Carla', 'Weber', 'FIAE-23a', '2023-09-01'),
('David', 'Wagner', 'FISI-24', '2024-08-01'),
('Elena', 'Becker', 'FIAE-23b', '2023-08-01'),
('Felix', 'Hoffmann', 'FISI-24', '2024-09-01');

-- Überprüfen, ob die Daten korrekt eingefügt wurden.
SELECT * FROM schueler;

