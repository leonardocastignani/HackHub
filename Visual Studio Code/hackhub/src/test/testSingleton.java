package test;

public class testSingleton {
    // 1. Variabile statica privata che contiene l'unica istanza
    private static testSingleton istanza;

    // 2. Costruttore privato (impedisce di fare "new TestSingleton()")
    private testSingleton() {
    }

    // 3. Metodo statico pubblico per ottenere l'istanza
    public static testSingleton getIstanza() {
        if (istanza == null) {
            istanza = new testSingleton();
        }
        return istanza;
    }
}
