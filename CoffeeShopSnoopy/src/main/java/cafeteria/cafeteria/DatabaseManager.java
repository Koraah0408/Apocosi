package cafeteria.cafeteria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/cafeteria";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            inicializarBaseDatos();
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo cargar el driver MySQL");
            e.printStackTrace();
        }
    }

    private static void inicializarBaseDatos() {
        // Primero conectar sin especificar base de datos para crearla
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS cafeteria");

        } catch (SQLException e) {
            System.err.println("Error creando base de datos: " + e.getMessage());
        }

        // Ahora conectar a la base de datos específica
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String[] tablas = {
                    """
                CREATE TABLE IF NOT EXISTS Usuarios (
                    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(50) NOT NULL
                )
                """,
                    """
                CREATE TABLE IF NOT EXISTS Productos (
                    producto_id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL UNIQUE,
                    precio_base DECIMAL(10, 2) NOT NULL
                )
                """,
                    """
                CREATE TABLE IF NOT EXISTS Ventas (
                    venta_id INT AUTO_INCREMENT PRIMARY KEY,
                    total DECIMAL(10, 2) NOT NULL,
                    fecha_hora DATETIME NOT NULL,
                    direccion_entrega VARCHAR(255),
                    estado VARCHAR(50) NOT NULL,
                    usuario_id INT,
                    FOREIGN KEY (usuario_id) REFERENCES Usuarios(usuario_id)
                )
                """,
                    """
                CREATE TABLE IF NOT EXISTS DetalleVenta (
                    detalle_id INT AUTO_INCREMENT PRIMARY KEY,
                    venta_id INT NOT NULL,
                    nombre_bebida VARCHAR(100) NOT NULL,
                    tamano VARCHAR(50),
                    leche VARCHAR(50),
                    estado_temp VARCHAR(50),
                    cantidad INT NOT NULL,
                    precio_total_item DECIMAL(10, 2) NOT NULL,
                    FOREIGN KEY (venta_id) REFERENCES Ventas(venta_id)
                )
                """
            };

            for (String tabla : tablas) {
                stmt.execute(tabla);
            }

            // Insertar productos iniciales si no existen
            String verificarProductos = "SELECT COUNT(*) FROM Productos";
            ResultSet rs = stmt.executeQuery(verificarProductos);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertarProductos = """
                    INSERT INTO Productos (nombre, precio_base) VALUES
                    ('Chocolate caliente', 40.00),
                    ('Capuccino', 50.00),
                    ('Latte', 50.00),
                    ('Macchiato', 35.00),
                    ('Mocha', 50.00),
                    ('Americano', 40.00),
                    ('Flat White', 55.00)
                """;
                stmt.executeUpdate(insertarProductos);
                System.out.println("Productos iniciales insertados");
            }

        } catch (SQLException e) {
            System.err.println("Error inicializando base de datos: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Métodos para Usuarios
    public static boolean verificarCredenciales(String username, String password) {
        String sql = "SELECT * FROM Usuarios WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Error verificando credenciales: " + e.getMessage());
            return false;
        }
    }

    public static boolean registrarUsuario(String nombre, String username, String password) {
        String sql = "INSERT INTO Usuarios (nombre, username, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error registrando usuario: " + e.getMessage());
            return false;
        }
    }

    public static int obtenerUsuarioId(String username) {
        String sql = "SELECT usuario_id FROM Usuarios WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("usuario_id");
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario ID: " + e.getMessage());
        }
        return -1;
    }

    // Métodos para Ventas - CORREGIDO
    public static int guardarVenta(PedidoCompleto pedido, int usuarioId) {
        String sql = "INSERT INTO Ventas (total, fecha_hora, direccion_entrega, estado, usuario_id) VALUES (?, NOW(), ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, pedido.getTotal());
            pstmt.setString(2, pedido.getDireccion());
            pstmt.setString(3, pedido.getEstado());
            pstmt.setInt(4, usuarioId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error guardando venta: " + e.getMessage());
        }
        return -1;
    }

    public static void guardarDetalleVenta(int ventaId, PedidoItem item) {
        String sql = "INSERT INTO DetalleVenta (venta_id, nombre_bebida, tamano, leche, estado_temp, cantidad, precio_total_item) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            pstmt.setString(2, item.getNombreBebida());
            pstmt.setString(3, item.getTamano());
            pstmt.setString(4, item.getLeche());
            pstmt.setString(5, item.getEstado());
            pstmt.setInt(6, item.getCantidad());
            pstmt.setDouble(7, item.getPrecioTotalItem());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando detalle venta: " + e.getMessage());
        }
    }

    // Métodos para obtener historial
    public static List<PedidoCompleto> obtenerHistorialPedidos(int usuarioId) {
        List<PedidoCompleto> pedidos = new ArrayList<>();
        String sql = "SELECT v.venta_id, v.total, v.fecha_hora, v.direccion_entrega, v.estado FROM Ventas v WHERE v.usuario_id = ? ORDER BY v.fecha_hora DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int ventaId = rs.getInt("venta_id");
                double total = rs.getDouble("total");
                String direccion = rs.getString("direccion_entrega");
                String estado = rs.getString("estado");
                String fechaHora = rs.getString("fecha_hora");

                List<PedidoItem> items = obtenerItemsPedido(ventaId);
                PedidoCompleto pedido = new PedidoCompleto(items, total, direccion, fechaHora, estado);
                pedidos.add(pedido);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo historial: " + e.getMessage());
        }
        return pedidos;
    }

    private static List<PedidoItem> obtenerItemsPedido(int ventaId) {
        List<PedidoItem> items = new ArrayList<>();
        String sql = "SELECT nombre_bebida, tamano, leche, estado_temp, cantidad, precio_total_item FROM DetalleVenta WHERE venta_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ventaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombreBebida = rs.getString("nombre_bebida");
                String tamano = rs.getString("tamano");
                String leche = rs.getString("leche");
                String estadoTemp = rs.getString("estado_temp");
                int cantidad = rs.getInt("cantidad");
                double precioTotal = rs.getDouble("precio_total_item");

                PedidoItem item = new PedidoItem(nombreBebida, tamano, leche, estadoTemp, cantidad, precioTotal);
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo items pedido: " + e.getMessage());
        }
        return items;
    }
}