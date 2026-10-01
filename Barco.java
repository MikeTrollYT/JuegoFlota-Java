// Representa un barco y controla los impactos que ha recibido.
class Barco {
    String nombre;
    int tamano, impactos = 0;

    // Crea un barco con su nombre y el numero de casillas que ocupa.
    Barco(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
    }

    // Indica si todos los segmentos del barco han sido alcanzados.
    boolean hundido() {
        return impactos >= tamano;
    }
}