package de.gc.agent.erm.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Repräsentiert eine Beziehung zwischen zwei Entitäten in einem
 * Datenmodell.
 *
 * Diese Klasse dient der Modellierung und Darstellung einer Beziehung
 * mit Property-Unterstützung für die Verwendung in
 * JavaFX-UI-Komponenten. Jede Beziehung beschreibt die beteiligten
 * Entitäten, deren Kardinalitäten, das Beziehungsverb, die Richtung und
 * kann direkt an UI gebunden werden.
 */
public class Relationship {

   /** Die erste Entität der Beziehung. */
   private final StringProperty entity1 = new SimpleStringProperty();

   /** Die Kardinalität auf Seite der ersten Entität. */
   private final StringProperty cardinality1 = new SimpleStringProperty();

   /** Das Verb, welches die Beziehung beschreibt. */
   private final StringProperty verb = new SimpleStringProperty();

   /** Die Kardinalität auf Seite der zweiten Entität. */
   private final StringProperty cardinality2 = new SimpleStringProperty();

   /** Die zweite Entität der Beziehung. */
   private final StringProperty entity2 = new SimpleStringProperty();

   /** Die Richtung der Beziehung. */
   private final StringProperty direction = new SimpleStringProperty();

   /**
    * WICHTIG: Leerer Konstruktor für Frameworks wie Jackson, notwendig für
    * Deserialisierung aus JSON.
    */
   public Relationship() {
   }

   /**
    * Voller Konstruktor zur Initialisierung aller Beziehungskomponenten.
    *
    * @param e1  Name der ersten Entität.
    * @param c1  Kardinalität an Entität1.
    * @param v   Verb der Beziehung.
    * @param c2  Kardinalität an Entität2.
    * @param e2  Name der zweiten Entität.
    * @param dir Richtung bzw. Richtungscode der Beziehung.
    */
   public Relationship(final String e1, final String c1, final String v,
         final String c2, final String e2, final String dir) {
      setEntity1(e1);
      setCardinality1(c1);
      setVerb(v);
      setCardinality2(c2);
      setEntity2(e2);
      setDirection(dir);
   }

   /** Property für Kardinalität von Entität1 (Bindung an UI). */
   public StringProperty cardinality1Property() {
      return cardinality1;
   }

   /** Property für Kardinalität von Entität2 (Bindung an UI). */
   public StringProperty cardinality2Property() {
      return cardinality2;
   }

   /** Property für die Richtung der Beziehung (Bindung an UI). */
   public StringProperty directionProperty() {
      return direction;
   }

   /** Property für Entität1 (Bindung an UI). */
   public StringProperty entity1Property() {
      return entity1;
   }

   /** Property für Entität2 (Bindung an UI). */
   public StringProperty entity2Property() {
      return entity2;
   }

   /** Liefert die Kardinalität an Entität1. */
   public String getCardinality1() {
      return cardinality1.get();
   }

   /** Liefert die Kardinalität an Entität2. */
   public String getCardinality2() {
      return cardinality2.get();
   }

   /** Liefert die Richtung der Beziehung. */
   public String getDirection() {
      return direction.get();
   }

   // --- Getter, Setter und Properties ---

   /** Liefert den Namen der ersten Entität. */
   public String getEntity1() {
      return entity1.get();
   }

   /** Liefert den Namen der zweiten Entität. */
   public String getEntity2() {
      return entity2.get();
   }

   /** Liefert das Beziehungsverb. */
   public String getVerb() {
      return verb.get();
   }

   /** Setzt die Kardinalität für Entität1. */
   public void setCardinality1(final String value) {
      cardinality1.set(value);
   }

   /** Setzt die Kardinalität für Entität2. */
   public void setCardinality2(final String value) {
      cardinality2.set(value);
   }

   /** Setzt die Richtung der Beziehung. */
   public void setDirection(final String value) {
      direction.set(value);
   }

   /** Setzt den Namen der ersten Entität. */
   public void setEntity1(final String value) {
      entity1.set(value);
   }

   /** Setzt den Namen der zweiten Entität. */
   public void setEntity2(final String value) {
      entity2.set(value);
   }

   /** Setzt das Beziehungsverb. */
   public void setVerb(final String value) {
      verb.set(value);
   }

   /** Property für das Beziehungsverb (Bindung an UI). */
   public StringProperty verbProperty() {
      return verb;
   }
}
