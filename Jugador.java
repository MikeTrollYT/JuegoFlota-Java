import java.util.*;

// Versión adaptada a Buscaminas: cada casilla puede contener una mina, y solo se revela la casilla elegida.
class Jugador {
    String nombre;
    int[][] barcoEn = new int[HundirLaFlota.TAMANO_TABLERO][HundirLaFlota.TAMANO_TABLERO]; // -1 = vacio, 0 = mina
    boolean[][] disparado = new boolean[HundirLaFlota.TAMANO_TABLERO][HundirLaFlota.TAMANO_TABLERO]; // casillas ya descubiertas
    List<Barco> barcos = new ArrayList<>();

    // Prepara el tablero y coloca minas aleatoriamente.
    Jugador(String nombre) {
        this.nombre = nombre;
        for (int[] fila : barcoEn) Arrays.fill(fila, -1);
        colocarMinas();
    }

    // Coloca un número fijo de minas en posiciones aleatorias.
    void colocarMinas() {
        int minas = 10;
        int colocadas = 0;
        while (colocadas < minas) {
            int fila = HundirLaFlota.rnd.nextInt(HundirLaFlota.TAMANO_TABLERO);
            int columna = HundirLaFlota.rnd.nextInt(HundirLaFlota.TAMANO_TABLERO);
            if (barcoEn[fila][columna] != -1) continue;
            barcoEn[fila][columna] = 0;
            colocadas++;
        }
    }

    // Cuenta cuántas minas quedan sin ser descubiertas.
    int barcosRestantes() {
        int minasRestantes = 0;
        for (int fila = 0; fila < HundirLaFlota.TAMANO_TABLERO; fila++) {
            for (int columna = 0; columna < HundirLaFlota.TAMANO_TABLERO; columna++) {
                if (barcoEn[fila][columna] == 0 && !disparado[fila][columna]) {
                    minasRestantes++;
                }
            }
        }
        return minasRestantes;
    }

    // Cuenta cuántas minas hay alrededor de una casilla.
    int minasAlrededor(int fila, int columna) {
        int contador = 0;
        for (int f = fila - 1; f <= fila + 1; f++) {
            for (int c = columna - 1; c <= columna + 1; c++) {
                if (f < 0 || f >= HundirLaFlota.TAMANO_TABLERO || c < 0 || c >= HundirLaFlota.TAMANO_TABLERO) continue;
                if (f == fila && c == columna) continue;
                if (barcoEn[f][c] == 0) contador++;
            }
        }
        return contador;
    }

    // Devuelve el simbolo que el jugador ve en su propio tablero.
    char propio(int fila, int columna) {
        if (!disparado[fila][columna]) return '~';
        if (barcoEn[fila][columna] == 0) return '*';
        int minas = minasAlrededor(fila, columna);
        return minas == 0 ? '.' : (char) ('0' + minas);
    }

    // Devuelve el simbolo que el rival puede ver en este tablero.
    char visibleRival(int fila, int columna) {
        if (!disparado[fila][columna]) return '~';
        if (barcoEn[fila][columna] == 0) return '*';
        int minas = minasAlrededor(fila, columna);
        return minas == 0 ? '.' : (char) ('0' + minas);
    }
}