import java.util.*;

// Contiene el tablero, los barcos y los disparos recibidos por un jugador.
class Jugador {
    String nombre;
    int[][] barcoEn = new int[HundirLaFlota.TAMANO_TABLERO][HundirLaFlota.TAMANO_TABLERO]; // indice del barco en la casilla (-1 = agua)
    boolean[][] disparado = new boolean[HundirLaFlota.TAMANO_TABLERO][HundirLaFlota.TAMANO_TABLERO]; // casillas donde el RIVAL ya ha disparado
    List<Barco> barcos = new ArrayList<>();

    // Prepara el tablero como agua y coloca automaticamente la flota.
    Jugador(String nombre) {
        this.nombre = nombre;
        for (int[] fila : barcoEn) Arrays.fill(fila, -1);
        colocarBarcos();
    }

    // Coloca aleatoriamente todos los barcos sin que se solapen.
    void colocarBarcos() {
        for (int indiceBarco = 0; indiceBarco < HundirLaFlota.TAMANOS.length; indiceBarco++) {

            Barco barco = new Barco(HundirLaFlota.NOMBRES[indiceBarco], HundirLaFlota.TAMANOS[indiceBarco]);
            barcos.add(barco);
            boolean colocado = false;

            while (!colocado) {
                boolean horizontal = HundirLaFlota.rnd.nextBoolean();
                int fila = HundirLaFlota.rnd.nextInt(HundirLaFlota.TAMANO_TABLERO);
                int columna = HundirLaFlota.rnd.nextInt(HundirLaFlota.TAMANO_TABLERO);
                int desplazamientoFila;
                int desplazamientoColumna;

                if (horizontal) {
                    desplazamientoFila = 0;
                    desplazamientoColumna = 1;
                } else {
                    desplazamientoFila = 1;
                    desplazamientoColumna = 0;
                }

                int filaFinal = fila + desplazamientoFila * (barco.tamano - 1);
                int columnaFinal = columna + desplazamientoColumna * (barco.tamano - 1);

                if (filaFinal >= HundirLaFlota.TAMANO_TABLERO || columnaFinal >= HundirLaFlota.TAMANO_TABLERO) continue;
                boolean libre = true;

                for (int segmento = 0; segmento < barco.tamano; segmento++) {
                    if (barcoEn[fila + desplazamientoFila * segmento][columna + desplazamientoColumna * segmento] != -1) libre = false;
                }

                if (!libre) continue;
                for (int segmento = 0; segmento < barco.tamano; segmento++) {
                    barcoEn[fila + desplazamientoFila * segmento][columna + desplazamientoColumna * segmento] = indiceBarco;
                }
                
                colocado = true;
            }
        }
    }

    // Cuenta cuantos barcos del jugador siguen sin estar hundidos.
    int barcosRestantes() {
        int numeroBarcos = 0;
        for (Barco barco : barcos) if (!barco.hundido()) numeroBarcos++;
        return numeroBarcos;
    }

    // Devuelve el simbolo que el jugador ve en su propio tablero.
    char propio(int fila, int columna) {
        boolean hayBarco = barcoEn[fila][columna] != -1;
        if (disparado[fila][columna]) {
            if (hayBarco) return 'X';
            return 'O';
        }
        if (hayBarco) return 'B';
        return '~';
    }

    // Devuelve el simbolo que el rival puede ver en este tablero.
    char visibleRival(int fila, int columna) {
        if (!disparado[fila][columna]) return '~';
        int indiceBarco = barcoEn[fila][columna];
        if (indiceBarco == -1) return 'O';
        if (barcos.get(indiceBarco).hundido()) return '#';
        return 'X';
    }
}