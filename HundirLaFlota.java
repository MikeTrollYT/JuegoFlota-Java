import java.util.*;

public class HundirLaFlota {

    // Configuración general del tablero y de la flota.
    static final int TAMANO_TABLERO = 10;
    static final int[] TAMANOS = {5, 4, 3, 3, 2};
    static final String[] NOMBRES = {"Portaaviones", "Acorazado", "Crucero", "Submarino", "Destructor"};
    static final Scanner sc = new Scanner(System.in);
    static final Random rnd = new Random();

    // Representa un barco y controla los impactos que ha recibido.
    static class Barco {
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

    // Contiene el tablero, los barcos y los disparos recibidos por un jugador.
    static class Jugador {
        String nombre;
        int[][] barcoEn = new int[TAMANO_TABLERO][TAMANO_TABLERO];          // indice del barco en la casilla (-1 = agua)
        boolean[][] disparado = new boolean[TAMANO_TABLERO][TAMANO_TABLERO]; // casillas donde el RIVAL ya ha disparado
        List<Barco> barcos = new ArrayList<>();

        // Prepara el tablero como agua y coloca automaticamente la flota.
        Jugador(String nombre) {
            this.nombre = nombre;
            for (int[] fila : barcoEn) Arrays.fill(fila, -1);
            colocarBarcos();
        }

        // Coloca aleatoriamente todos los barcos sin que se solapen.
        void colocarBarcos() {
            for (int indiceBarco = 0; indiceBarco < TAMANOS.length; indiceBarco++) {
                Barco barco = new Barco(NOMBRES[indiceBarco], TAMANOS[indiceBarco]);
                barcos.add(barco);
                boolean colocado = false;
                while (!colocado) {
                    boolean horizontal = rnd.nextBoolean();
                    int fila = rnd.nextInt(TAMANO_TABLERO), columna = rnd.nextInt(TAMANO_TABLERO);
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
                    if (filaFinal >= TAMANO_TABLERO || columnaFinal >= TAMANO_TABLERO) continue;
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
                if (hayBarco) {
                    return 'X';
                }
                return 'O';
            }
            if (hayBarco) {
                return 'B';
            }
            return '~';
        }

        // Devuelve el simbolo que el rival puede ver en este tablero.
        char visibleRival(int fila, int columna) {
            if (!disparado[fila][columna]) return '~';
            int indiceBarco = barcoEn[fila][columna];
            if (indiceBarco == -1) return 'O';
            if (barcos.get(indiceBarco).hundido()) {
                return '#';
            }
            return 'X';
        }
    }

    // Limpia la consola para ocultar el tablero del turno anterior.
    static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int linea = 0; linea < 50; linea++) System.out.println(); // por si la consola no soporta ANSI
    }

    // Pausa el juego hasta que el jugador confirme que esta preparado.
    static void esperarEnter(String mensaje) {
        System.out.println(mensaje);
        sc.nextLine();
    }

    // Muestra el tablero propio y el tablero enemigo desde la perspectiva actual.
    static void mostrarTableros(Jugador actual, Jugador rival) {
        System.out.println("=== Turno de " + actual.nombre + " ===\n");
        System.out.println("      TU FLOTA                      TABLERO ENEMIGO");
        String cab = "    A B C D E F G H I J";
        System.out.println(cab + "      " + cab);
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            StringBuilder sb = new StringBuilder(String.format("%2d  ", fila + 1));
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                sb.append(actual.propio(fila, columna)).append(' ');
            }
            sb.append("     ").append(String.format("%2d  ", fila + 1));
            for (int columna = 0; columna < TAMANO_TABLERO; columna++) {
                sb.append(rival.visibleRival(fila, columna)).append(' ');
            }
            System.out.println(sb);
        }
        System.out.println("\nLeyenda: ~ agua | B barco | X tocado | O agua disparada | # hundido");
        System.out.println("Tus barcos restantes: " + actual.barcosRestantes()
                + " | Barcos enemigos restantes: " + rival.barcosRestantes() + "\n");
    }

    // Pide una coordenada valida y evita que se repita un disparo anterior.
    static int[] pedirDisparo(Jugador rival) {
        while (true) {
            System.out.print("Introduce coordenada a atacar (ej: B5): ");
            String coordenada = sc.nextLine().trim().toUpperCase();
            if (coordenada.length() < 2 || coordenada.length() > 3) {
                System.out.println("Coordenada no valida.");
                continue;
            }
            int columna = coordenada.charAt(0) - 'A';
            int fila;
            try {
                fila = Integer.parseInt(coordenada.substring(1)) - 1;
            } catch (NumberFormatException excepcion) {
                System.out.println("Coordenada no valida.");
                continue;
            }
            if (columna < 0 || columna >= TAMANO_TABLERO
                    || fila < 0 || fila >= TAMANO_TABLERO) {
                System.out.println("Fuera del tablero (columnas A-J, filas 1-10).");
                continue;
            }
            if (rival.disparado[fila][columna]) {
                System.out.println("Ya has disparado ahi. Elige otra casilla.");
                continue;
            }
            return new int[]{fila, columna};
        }
    }

    // Crea los jugadores y repite los turnos hasta que una flota quede destruida.
    public static void main(String[] args) {
        Jugador[] jugadores = {new Jugador("Jugador 1"), new Jugador("Jugador 2")};
        int turno = 0;

        while (true) {
            // 1. Se selecciona quien juega y quien recibe el disparo.
            Jugador actual = jugadores[turno];
            Jugador rival = jugadores[1 - turno];

            // 2. Se cambia de perspectiva para que cada jugador vea su tablero.
            limpiarPantalla();
            esperarEnter("Es el turno de " + actual.nombre + ".\n"
                    + "Que " + rival.nombre + " NO mire la pantalla.\n"
                    + "Pulsa ENTER cuando estes listo...");

            limpiarPantalla();
            mostrarTableros(actual, rival);

            // 3. El jugador elige una casilla y se registra el disparo.
            int[] disparo = pedirDisparo(rival);
            int fila = disparo[0], columna = disparo[1];
            rival.disparado[fila][columna] = true;

            String resultado;
            int indiceBarco = rival.barcoEn[fila][columna];
            if (indiceBarco == -1) {
                resultado = "AGUA...";
            } else {
                Barco barco = rival.barcos.get(indiceBarco);
                barco.impactos++;
                if (barco.hundido()) {
                    resultado = "¡TOCADO Y HUNDIDO! Has hundido el " + barco.nombre + ".";
                } else {
                    resultado = "¡TOCADO!";
                }
            }

            // 4. Se enseña el resultado y se comprueba si termina la partida.
            limpiarPantalla();
            mostrarTableros(actual, rival);
            System.out.println(">>> " + resultado + "\n");

            if (rival.barcosRestantes() == 0) {
                System.out.println("*****************************************");
                System.out.println("  " + actual.nombre.toUpperCase() + " HA HUNDIDO TODA LA FLOTA ENEMIGA. ¡GANA!");
                System.out.println("*****************************************");
                break;
            }

            // 5. Si la partida continua, comienza el turno del otro jugador.
            esperarEnter("Pulsa ENTER para terminar tu turno...");
            turno = 1 - turno;
        }
    }
}