package de.gc.agent.erm;

/**
 * Eine separate Launcher-Klasse, die als Haupteinstiegspunkt für das
 * ausführbare "fat jar" dient.
 * <p>
 * Dies ist ein Standard-Workaround für JavaFX-Anwendungen, die als "fat
 * jar" verpackt werden. Der direkte Aufruf einer Klasse, die von
 * {@code javafx.application.Application} erbt, kann zu Problemen mit
 * dem Classloader führen ("JavaFX runtime components are missing").
 * <p>
 * Dieser Launcher umgeht das Problem, indem er die eigentliche
 * main-Methode der JavaFX-Anwendung aufruft.
 */
public class Launcher {

   /**
    * Einstiegspunkt des Programms für das "fat jar".
    *
    * @param args Kommandozeilenargumente.
    */
   public static void main(final String[] args) {
      MainApp.main(args);
   }
}
