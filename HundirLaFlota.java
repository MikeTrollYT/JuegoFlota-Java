import java.util.*;

public class HundirLaFlota {

    // Configuración general del tablero y de la flota.
    static final int TAMANO_TABLERO = 10;
    static final int[] TAMANOS = {5, 4, 3, 3, 2};
    static final String[] NOMBRES = {"Portaaviones", "Acorazado", "Crucero", "Submarino", "Destructor"};
    static final Scanner sc = new Scanner(System.in);
    static final Random rnd = new Random();


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
        System.out.println("      TU TABLERO");
        String cab = "    A B C D E F G H I J";
        System.out.println(cab);

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

        System.out.println("\nLeyenda: ~ oculto | * mina | . seguro | 0-8 minas alrededor");
        System.out.println("Minas por descubrir: " + actual.barcosRestantes() + " | Minas enemigas por descubrir: " + rival.barcosRestantes() + "\n");
    }

    // Crea los jugadores y repite los turnos hasta que una flota quede destruida.
    public static void main(String[] args) {
        Jugador[] jugadores = {new Jugador("Jugador 1"), new Jugador("Jugador 2")};
        int turno = 0;

        while (true) {
            Jugador actual = jugadores[turno];
            Jugador rival = jugadores[1 - turno];

            limpiarPantalla();
            esperarEnter("Es el turno de " + actual.nombre + ".\n"
                    + "Que " + rival.nombre + " NO mire la pantalla.\n"
                    + "Pulsa ENTER cuando estes listo...");

            limpiarPantalla();
            mostrarTableros(actual, rival);

            int[] disparo = pedirDisparo(rival);
            int fila = disparo[0], columna = disparo[1];
            rival.disparado[fila][columna] = true;

            String resultado;
            int indiceBarco = rival.barcoEn[fila][columna];
            if (indiceBarco == -1) {
                int minas = rival.minasAlrededor(fila, columna);
                resultado = "CASILLA SEGURA. Hay " + minas + " minas alrededor.";
            } else {
                resultado = "¡MINA! Has explotado una casilla con bomba.";
            }

            limpiarPantalla();
            mostrarTableros(actual, rival);
            System.out.println(">>> " + resultado + "\n");

            if (rival.barcosRestantes() == 0) {
                System.out.println("*****************************************");
                System.out.println("  " + actual.nombre.toUpperCase() + " HA DESCUBIERTO TODA LA MINA ENEMIGA. ¡GANA!");
                System.out.println("*****************************************");
                break;
            }

            esperarEnter("Pulsa ENTER para terminar tu turno...");
            turno = 1 - turno;
        }
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
}