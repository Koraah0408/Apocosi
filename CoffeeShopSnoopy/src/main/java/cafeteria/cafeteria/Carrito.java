package cafeteria.cafeteria;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase estática que gestiona el carrito de compras.
 * Será accesible desde todos los controladores.
 */
public class Carrito {

    private static final List<PedidoItem> items = new ArrayList<>();

    public static void agregarItem(PedidoItem item) {
        items.add(item);
    }

    public static void removerItem(PedidoItem item) {
        items.remove(item);
    }

    public static List<PedidoItem> getItems() {
        return new ArrayList<>(items); // Devuelve una copia para evitar modificaciones
    }

    public static double getSubtotal() {
        double subtotal = 0;
        for (PedidoItem item : items) {
            subtotal += item.getPrecioTotalItem();
        }
        return subtotal;
    }

    public static int getCantidadItems() {
        return items.size();
    }

    public static void limpiarCarrito() {
        items.clear();
    }
}