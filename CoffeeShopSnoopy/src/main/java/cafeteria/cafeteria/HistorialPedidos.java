package cafeteria.cafeteria;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase estática que gestiona el historial de pedidos confirmados.
 */
public class HistorialPedidos {

    private static final List<PedidoCompleto> pedidos = new ArrayList<>();

    public static void agregarPedido(PedidoCompleto pedido) {
        // Agrega el pedido más reciente al inicio de la lista
        pedidos.add(0, pedido);
    }

    public static List<PedidoCompleto> getPedidos() {
        return new ArrayList<>(pedidos); // Devuelve una copia
    }
}