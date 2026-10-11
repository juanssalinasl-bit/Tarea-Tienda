package co.edu.uniquindio.poo.app;

import co.edu.uniquindio.poo.model.Categoria;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.DetalleFactura;
import co.edu.uniquindio.poo.model.EstadoFactura;
import co.edu.uniquindio.poo.model.Factura;
import co.edu.uniquindio.poo.model.MetodoPago;
import co.edu.uniquindio.poo.model.Producto;
import co.edu.uniquindio.poo.model.Tienda;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Punto de entrada de TechStore. Presenta los menús mediante ventanas de JOptionPane.
 *
 * @author TechStore
 * @version 1.0
 */
public class Main {

    private static final Tienda TIENDA = new Tienda(
            "TechStore",
            "901234567-1",
            "6061234567",
            "Armenia, Quindío"
    );

    /** Inicia la aplicación y carga registros de ejemplo. */
    public static void main(String[] args) {
        cargarDatosIniciales();
        mostrarMenuPrincipal();
    }

    /** Muestra las opciones principales hasta que el usuario decida salir. */
    private static void mostrarMenuPrincipal() {
        String[] opciones = {"Clientes", "Productos", "Facturas", "Reportes", "Salir"};
        int opcion;

        do {
            opcion = seleccionarOpcion(
                    "TECHSTORE\nGestión de ventas e inventario",
                    opciones
            );

            switch (opcion) {
                case 0 -> menuClientes();
                case 1 -> menuProductos();
                case 2 -> menuFacturas();
                case 3 -> menuReportes();
                case 4, -1 -> opcion = 4;
                default -> mostrarMensaje("Selecciona una opción válida.");
            }
        } while (opcion != 4);

        mostrarMensaje("Gracias por utilizar TechStore.");
    }

    /** Registra datos de ejemplo para probar las opciones desde el primer inicio. */
    private static void cargarDatosIniciales() {
        Cliente cliente1 = new Cliente(
                "1001", "Laura Gómez", "3001112233",
                "laura@gmail.com", "Armenia", TIENDA
        );
        Cliente cliente2 = new Cliente(
                "1002", "Andrés López", "3102223344",
                "andres@gmail.com", "Pereira", TIENDA
        );

        TIENDA.registrarCliente(cliente1);
        TIENDA.registrarCliente(cliente2);

        TIENDA.registrarProducto(new Producto(
                "Portátil Lenovo", "P001", "Portátil 16 GB RAM",
                8, 2500000, Categoria.COMPUTADORES, TIENDA
        ));
        TIENDA.registrarProducto(new Producto(
                "Celular Samsung", "P002", "Celular 128 GB",
                12, 1200000, Categoria.CELULARES, TIENDA
        ));
        TIENDA.registrarProducto(new Producto(
                "Audífonos", "P003", "Audífonos inalámbricos",
                4, 150000, Categoria.ACCESORIOS, TIENDA
        ));
        TIENDA.registrarProducto(new Producto(
                "Control videojuegos", "P004", "Control inalámbrico",
                15, 230000, Categoria.VIDEO_JUEGOS, TIENDA
        ));

        ArrayList<DetalleFactura> detalles = new ArrayList<>();
        detalles.add(new DetalleFactura(
                1,
                1200000,
                TIENDA.buscarProducto("P002").orElseThrow()
        ));

        Factura facturaDemo = new Factura(
                "1000",
                LocalDate.now().minusDays(1),
                0,
                EstadoFactura.PAGADA,
                MetodoPago.TARJETA_CREDITO,
                cliente1,
                detalles
        );

        TIENDA.registrarFactura(facturaDemo);
        TIENDA.buscarProducto("P002").orElseThrow().setCantidadDisponible(11);
    }

    /** Presenta las operaciones CRUD de clientes. */
    private static void menuClientes() {
        String[] opciones = {
                "Registrar cliente", "Buscar cliente", "Listar clientes",
                "Actualizar cliente", "Eliminar cliente", "Volver"
        };

        boolean volver = false;
        while (!volver) {
            int opcion = seleccionarOpcion("GESTIÓN DE CLIENTES", opciones);

            switch (opcion) {
                case 0 -> registrarCliente();
                case 1 -> buscarCliente();
                case 2 -> mostrarLista("Clientes registrados", TIENDA.listarClientes());
                case 3 -> actualizarCliente();
                case 4 -> eliminarCliente();
                default -> volver = true;
            }
        }
    }

    private static void registrarCliente() {
        String documento = pedirTexto("Documento de identidad:");
        if (documento == null) return;

        String nombre = pedirTexto("Nombre completo:");
        String telefono = pedirTexto("Teléfono:");
        String correo = pedirTexto("Correo electrónico:");
        String ciudad = pedirTexto("Ciudad de residencia:");

        if (nombre == null || telefono == null || correo == null || ciudad == null) return;

        Cliente cliente = new Cliente(documento, nombre, telefono, correo, ciudad, TIENDA);
        mostrarMensaje(TIENDA.registrarCliente(cliente));
    }

    private static void buscarCliente() {
        String documento = pedirTexto("Documento del cliente:");
        if (documento == null) return;

        Optional<Cliente> cliente = TIENDA.buscarCliente(documento);
        mostrarMensaje(cliente.map(Object::toString).orElse("Cliente no encontrado."));
    }

    private static void actualizarCliente() {
        String documento = pedirTexto("Documento del cliente que deseas actualizar:");
        if (documento == null) return;

        String nombre = pedirTexto("Nuevo nombre completo:");
        String telefono = pedirTexto("Nuevo teléfono:");
        String correo = pedirTexto("Nuevo correo:");
        String ciudad = pedirTexto("Nueva ciudad:");

        if (nombre == null || telefono == null || correo == null || ciudad == null) return;

        mostrarMensaje(TIENDA.actualizarCliente(documento, nombre, telefono, correo, ciudad));
    }

    private static void eliminarCliente() {
        String documento = pedirTexto("Documento del cliente que deseas eliminar:");
        if (documento == null) return;

        if (confirmar("¿Deseas eliminar el cliente " + documento + "?")) {
            mostrarMensaje(TIENDA.eliminarCliente(documento));
        }
    }

    /** Presenta las operaciones CRUD y las consultas de inventario. */
    private static void menuProductos() {
        String[] opciones = {
                "Registrar producto", "Buscar producto", "Listar productos",
                "Actualizar producto", "Eliminar producto", "Productos con bajo inventario",
                "Productos con inventario suficiente", "Volver"
        };

        boolean volver = false;
        while (!volver) {
            int opcion = seleccionarOpcion("GESTIÓN DE PRODUCTOS", opciones);

            switch (opcion) {
                case 0 -> registrarProducto();
                case 1 -> buscarProducto();
                case 2 -> mostrarLista("Productos registrados", TIENDA.listarProductos());
                case 3 -> actualizarProducto();
                case 4 -> eliminarProducto();
                case 5 -> mostrarLista(
                        "Productos con cinco unidades o menos",
                        TIENDA.obtenerProductosBajoInventario(5)
                );
                case 6 -> mostrarLista(
                        "Productos con diez unidades o más",
                        TIENDA.obtenerProductosConInventarioAlto(10)
                );
                default -> volver = true;
            }
        }
    }

    private static void registrarProducto() {
        String codigo = pedirTexto("Código del producto:");
        if (codigo == null) return;

        String nombre = pedirTexto("Nombre del producto:");
        String descripcion = pedirTexto("Descripción:");
        Integer cantidad = pedirEntero("Cantidad disponible:");
        Double precio = pedirDouble("Precio del producto:");
        Categoria categoria = seleccionarCategoria();

        if (nombre == null || descripcion == null || cantidad == null
                || precio == null || categoria == null) return;

        try {
            Producto producto = new Producto(
                    nombre, codigo, descripcion, cantidad, precio, categoria, TIENDA
            );
            mostrarMensaje(TIENDA.registrarProducto(producto));
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    private static void buscarProducto() {
        String codigo = pedirTexto("Código del producto:");
        if (codigo == null) return;

        mostrarMensaje(
                TIENDA.buscarProducto(codigo)
                        .map(Object::toString)
                        .orElse("Producto no encontrado.")
        );
    }

    private static void actualizarProducto() {
        String codigo = pedirTexto("Código del producto que deseas actualizar:");
        if (codigo == null) return;

        String descripcion = pedirTexto("Nueva descripción:");
        Double precio = pedirDouble("Nuevo precio:");
        Integer cantidad = pedirEntero("Nueva cantidad disponible:");

        if (descripcion == null || precio == null || cantidad == null) return;

        try {
            mostrarMensaje(TIENDA.actualizarProducto(codigo, descripcion, precio, cantidad));
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    private static void eliminarProducto() {
        String codigo = pedirTexto("Código del producto que deseas eliminar:");
        if (codigo == null) return;

        if (confirmar("¿Deseas eliminar el producto " + codigo + "?")) {
            mostrarMensaje(TIENDA.eliminarProducto(codigo));
        }
    }

    /** Presenta la creación, consulta y administración de facturas. */
    private static void menuFacturas() {
        String[] opciones = {
                "Crear factura con productos", "Buscar factura por código", "Listar facturas",
                "Confirmar venta", "Facturas de un cliente",
                "Facturas por fecha", "Eliminar factura", "Volver"
        };

        boolean volver = false;
        while (!volver) {
            int opcion = seleccionarOpcion("GESTIÓN DE FACTURAS", opciones);

            switch (opcion) {
                case 0 -> crearFactura();
                case 1 -> buscarFactura();
                case 2 -> mostrarLista("Facturas registradas", TIENDA.listarFacturas());
                case 3 -> confirmarVenta();
                case 4 -> consultarFacturasCliente();
                case 5 -> consultarFacturasFecha();
                case 6 -> eliminarFactura();
                default -> volver = true;
            }
        }
    }

    /** Crea una factura y exige agregar al menos un producto antes de registrarla. */
    private static void crearFactura() {
        String documento = pedirTexto("Documento del cliente:");
        if (documento == null) return;

        Optional<Cliente> cliente = TIENDA.buscarCliente(documento);
        if (cliente.isEmpty()) {
            mostrarMensaje("No existe un cliente con ese documento.");
            return;
        }

        if (TIENDA.listarProductos().isEmpty()) {
            mostrarMensaje("Primero debes registrar productos en el inventario.");
            return;
        }

        ArrayList<DetalleFactura> detalles = new ArrayList<>();
        boolean terminar = false;

        while (!terminar) {
            List<Producto> productosDisponibles = TIENDA.listarProductos().stream()
                    .filter(producto -> producto.getCantidadDisponible() > 0)
                    .toList();

            if (productosDisponibles.isEmpty()) {
                mostrarMensaje("No hay productos disponibles en el inventario.");
                return;
            }

            String[] opcionesProductos = new String[productosDisponibles.size() + 1];
            for (int i = 0; i < productosDisponibles.size(); i++) {
                Producto producto = productosDisponibles.get(i);
                opcionesProductos[i] = producto.getCodigo() + " - "
                        + producto.getNombre() + " | Disponible: "
                        + producto.getCantidadDisponible() + " | $"
                        + formatoDinero(producto.getValor());
            }
            opcionesProductos[productosDisponibles.size()] = "Terminar de agregar productos";

            int seleccion = seleccionarOpcion(
                    "PRODUCTOS PARA LA FACTURA\nSelecciona el número del producto:",
                    opcionesProductos
            );

            if (seleccion == -1) return;
            if (seleccion == productosDisponibles.size()) {
                terminar = true;
                continue;
            }

            Producto producto = productosDisponibles.get(seleccion);
            Integer cantidad = pedirEntero("Cantidad de " + producto.getNombre() + ":");
            if (cantidad == null) return;
            if (cantidad <= 0) {
                mostrarMensaje("La cantidad debe ser mayor que cero.");
                continue;
            }

            int cantidadYaAgregada = detalles.stream()
                    .filter(detalle -> detalle.getProducto().getCodigo().equals(producto.getCodigo()))
                    .mapToInt(DetalleFactura::getCantidadComprada)
                    .sum();

            if (cantidad + cantidadYaAgregada > producto.getCantidadDisponible()) {
                mostrarMensaje("No hay suficientes unidades disponibles. Cantidad disponible: "
                        + (producto.getCantidadDisponible() - cantidadYaAgregada));
                continue;
            }

            detalles.add(new DetalleFactura(cantidad, producto.getValor(), producto));
            mostrarMensaje("Producto agregado a la factura. Subtotal: $"
                    + formatoDinero(cantidad * producto.getValor()));
        }

        if (detalles.isEmpty()) {
            mostrarMensaje("La factura no se creó porque debes agregar al menos un producto.");
            return;
        }

        MetodoPago metodoPago = seleccionarMetodoPago();
        if (metodoPago == null) return;

        Factura factura = new Factura(
                TIENDA.generarCodigoFactura(),
                LocalDate.now(),
                0,
                EstadoFactura.GENERADA,
                metodoPago,
                cliente.get(),
                detalles
        );

        String resultado = TIENDA.registrarFactura(factura);
        mostrarMensaje("Código de factura: " + factura.codigo() + "\n"
                + "Cliente: " + cliente.get().getNombreCompleto() + "\n"
                + "Productos: " + detalles.size() + "\n"
                + "Total: $" + formatoDinero(factura.total()) + "\n"
                + "Estado: " + factura.estadoFactura() + "\n\n"
                + resultado);
    }

    private static void buscarFactura() {
        String codigo = pedirTexto("Código de la factura:");
        if (codigo == null) return;

        Optional<Factura> factura = TIENDA.buscarFactura(codigo);
        if (factura.isEmpty()) {
            mostrarMensaje("Factura no encontrada.");
            return;
        }

        Factura encontrada = factura.get();
        StringBuilder detalle = new StringBuilder(enmarcar("FACTURA"));
        detalle.append(encontrada).append("\n\nPRODUCTOS\n");

        for (DetalleFactura item : encontrada.listaDetallesFactura()) {
            detalle.append(item).append('\n');
        }

        mostrarMensaje(detalle.toString());
    }

    private static void confirmarVenta() {
        String codigo = pedirTexto("Código de la factura que deseas confirmar:");
        if (codigo == null) return;

        if (confirmar("¿Confirmas la venta de la factura " + codigo + "?")) {
            mostrarMensaje(TIENDA.confirmarVenta(codigo));
        }
    }

    private static void consultarFacturasCliente() {
        String documento = pedirTexto("Documento del cliente:");
        if (documento == null) return;

        mostrarLista(
                "Facturas del cliente " + documento,
                TIENDA.consultarFacturasCliente(documento)
        );
    }

    private static void consultarFacturasFecha() {
        LocalDate fecha = pedirFecha("Fecha (AAAA-MM-DD):");
        if (fecha == null) return;

        mostrarLista("Facturas del " + fecha, TIENDA.consultarFacturasFecha(fecha));
    }

    private static void eliminarFactura() {
        String codigo = pedirTexto("Código de la factura que deseas eliminar:");
        if (codigo == null) return;

        if (confirmar("¿Deseas eliminar la factura " + codigo + "?")) {
            mostrarMensaje(TIENDA.eliminarFactura(codigo));
        }
    }

    /** Presenta los reportes disponibles de ventas e inventario. */
    private static void menuReportes() {
        String[] opciones = {
                "Total vendido hoy", "Total vendido por fecha",
                "Productos con bajo inventario", "Reporte general de ventas",
                "Producto más vendido", "Volver"
        };

        boolean volver = false;
        while (!volver) {
            int opcion = seleccionarOpcion("REPORTES", opciones);

            switch (opcion) {
                case 0 -> mostrarMensaje(
                        "Total vendido hoy: $" + formatoDinero(
                                TIENDA.obtenerTotalVendido(LocalDate.now())
                        )
                );
                case 1 -> {
                    LocalDate fecha = pedirFecha("Fecha (AAAA-MM-DD):");
                    if (fecha != null) {
                        mostrarMensaje("Total vendido: $" + formatoDinero(
                                TIENDA.obtenerTotalVendido(fecha)
                        ));
                    }
                }
                case 2 -> mostrarLista(
                        "Productos con bajo inventario",
                        TIENDA.obtenerProductosBajoInventario(5)
                );
                case 3 -> mostrarMensaje(TIENDA.generarReporteVentas());
                case 4 -> mostrarMensaje(
                        TIENDA.obtenerProductoMasVendido()
                                .map(Producto::toString)
                                .orElse("Todavía no hay productos vendidos.")
                );
                default -> volver = true;
            }
        }
    }

    /** Permite seleccionar una categoría escribiendo su número. */
    private static Categoria seleccionarCategoria() {
        Categoria[] categorias = Categoria.values();
        String[] opciones = new String[categorias.length];

        for (int i = 0; i < categorias.length; i++) {
            opciones[i] = categorias[i].name().replace('_', ' ');
        }

        int seleccion = seleccionarOpcion("CATEGORÍAS DE PRODUCTOS", opciones);
        if (seleccion == -1) {
            return null;
        }

        return categorias[seleccion];
    }

    /** Permite seleccionar el método de pago escribiendo su número. */
    private static MetodoPago seleccionarMetodoPago() {
        MetodoPago[] metodos = MetodoPago.values();
        String[] opciones = new String[metodos.length];

        for (int i = 0; i < metodos.length; i++) {
            opciones[i] = metodos[i].name().replace('_', ' ');
        }

        int seleccion = seleccionarOpcion("MÉTODOS DE PAGO", opciones);
        if (seleccion == -1) {
            return null;
        }

        return metodos[seleccion];
    }

    /** Muestra un menú numerado y devuelve la opción seleccionada (desde cero). */
    private static int seleccionarOpcion(String titulo, String[] opciones) {
        StringBuilder menu = new StringBuilder(titulo).append("\n\n");

        for (int i = 0; i < opciones.length; i++) {
            menu.append(i + 1).append(". ").append(opciones[i]).append('\n');
        }

        menu.append("\nEscribe el número de la opción:");
        String respuesta = JOptionPane.showInputDialog(
                null, menu.toString(), "TechStore", JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == null) {
            return -1;
        }

        try {
            int opcion = Integer.parseInt(respuesta.trim());
            if (opcion >= 1 && opcion <= opciones.length) {
                return opcion - 1;
            }
        } catch (NumberFormatException e) {
            // Se muestra el mensaje de validación que aparece debajo.
        }

        mostrarMensaje("Opción inválida. Ingresa un número entre 1 y " + opciones.length + ".");
        return seleccionarOpcion(titulo, opciones);
    }

    private static String pedirTexto(String mensaje) {
        String valor = JOptionPane.showInputDialog(null, mensaje, "TechStore", JOptionPane.QUESTION_MESSAGE);
        if (valor == null) return null;

        valor = valor.trim();
        if (valor.isEmpty()) {
            mostrarMensaje("Este campo no puede estar vacío.");
            return pedirTexto(mensaje);
        }

        return valor;
    }

    private static Integer pedirEntero(String mensaje) {
        String valor = pedirTexto(mensaje);
        if (valor == null) return null;

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un número entero válido.");
            return pedirEntero(mensaje);
        }
    }

    private static Double pedirDouble(String mensaje) {
        String valor = pedirTexto(mensaje);
        if (valor == null) return null;

        try {
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un número válido. Puedes usar punto o coma decimal.");
            return pedirDouble(mensaje);
        }
    }

    private static LocalDate pedirFecha(String mensaje) {
        String valor = pedirTexto(mensaje);
        if (valor == null) return null;

        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Fecha inválida. Usa el formato AAAA-MM-DD, por ejemplo 2026-10-10.");
            return pedirFecha(mensaje);
        }
    }

    private static boolean confirmar(String mensaje) {
        return JOptionPane.showConfirmDialog(
                null,
                mensaje,
                "Confirmar operación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    private static void mostrarLista(String titulo, List<?> lista) {
        if (lista.isEmpty()) {
            mostrarMensaje("No hay registros para mostrar.");
            return;
        }

        StringBuilder contenido = new StringBuilder(enmarcar(titulo));
        for (Object elemento : lista) {
            if (elemento instanceof Factura factura) {
                contenido.append("Código: ").append(factura.codigo())
                        .append(" | Fecha: ").append(factura.fecha())
                        .append(" | Cliente: ").append(factura.cliente().getNombreCompleto())
                        .append(" | Total: $").append(formatoDinero(factura.total()))
                        .append(" | Estado: ").append(factura.estadoFactura())
                        .append(" | Pago: ").append(factura.metodoPago()).append('\n');
            } else {
                contenido.append(elemento).append('\n');
            }
        }

        JOptionPane.showMessageDialog(
                null,
                contenido.toString(),
                "TechStore",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "TechStore",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static String enmarcar(String titulo) {
        return "===== " + titulo.toUpperCase() + " =====\n";
    }

    private static String formatoDinero(double valor) {
        return String.format("%,.2f", valor);
    }
}
