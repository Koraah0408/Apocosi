package cafeteria.cafeteria;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PedidoCompleto {
    private final List<PedidoItem> items;
    private final double total;
    private final String direccion;
    private String timestamp;
    private String estado;

    public PedidoCompleto(List<PedidoItem> items, double total, String direccion) {
        this.items = items;
        this.total = total;
        this.direccion = direccion;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.estado = "En preparación";
    }

    // Constructor para cargar desde BD
    public PedidoCompleto(List<PedidoItem> items, double total, String direccion, String timestamp, String estado) {
        this.items = items;
        this.total = total;
        this.direccion = direccion;
        this.timestamp = timestamp;
        this.estado = estado;
    }

    // Getters
    public List<PedidoItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getDireccion() { return direccion; }
    public String getTimestamp() { return timestamp; }
    public String getEstado() { return estado; }

    // Setters
    public void setEstado(String estado) { this.estado = estado; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] - %s - $%.2f (%d items)",
                timestamp, estado, total, items.size());
    }
}