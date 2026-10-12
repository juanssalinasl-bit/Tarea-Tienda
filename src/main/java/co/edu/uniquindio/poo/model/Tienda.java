package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;

/**
 * Administra los clientes, productos, facturas, inventario y reportes de TechStore.
 */
public class Tienda {

    private int siguienteCodigoFactura = 1001;

    private final String nombre;
    private final String nit;
    private String telefono;
    private String direccion;

    private final List<Cliente> listaClientes = new ArrayList<>();
    private final List<Factura> listaFacturas = new ArrayList<>();
    private final Map<String, Producto> listaProductos = new HashMap<>();

    /**
     * Crea la tienda con sus datos básicos.
     */
    public Tienda(String nombre, String nit, String telefono, String direccion) {
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // -------------------- GETTERS Y SETTERS --------------------

    public String getNombre() {
        return nombre;
    }

    public String getNit() {
        return nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Cliente> getListaClientes() {
        return Collections.unmodifiableList(listaClientes);
    }

    public List<Factura> getListaFacturas() {
        return Collections.unmodifiableList(listaFacturas);
    }

    public Map<String, Producto> getListaProductos() {
        return Collections.unmodifiableMap(listaProductos);
    }

    // -------------------- CRUD CLIENTE --------------------

    /**
     * Registra un cliente si su documento todavía no existe.
     */
    public String registrarCliente(Cliente cliente) {
        if (cliente == null) {
            return "Cliente inválido";
        }

        Optional<Cliente> clienteEncontrado = buscarCliente(cliente.getDocumentoIdentidad());

        if (clienteEncontrado.isEmpty()) {
            listaClientes.add(cliente);
            return "Cliente registrado correctamente";
        }

        return "Ya existe un cliente con ese documento";
    }

    /**
     * Busca un cliente por su documento de identidad.
     */
    public Optional<Cliente> buscarCliente(String documento) {
        return listaClientes.stream()
                .filter(cliente -> cliente.getDocumentoIdentidad().equals(documento))
                .findFirst();
    }

    /**
     * Actualiza los datos modificables de un cliente.
     */
    public String actualizarCliente(String documento, String nombre,
                                    String telefono, String correo, String ciudad) {
        Optional<Cliente> clienteEncontrado = buscarCliente(documento);

        if (clienteEncontrado.isEmpty()) {
            return "Cliente no encontrado";
        }

        Cliente cliente = clienteEncontrado.get();
        cliente.setNombreCompleto(nombre);
        cliente.setTelefono(telefono);
        cliente.setCorreo(correo);
        cliente.setCiudadResidencia(ciudad);

        return "Cliente actualizado correctamente";
    }

    /**
     * Elimina un cliente que no tenga facturas asociadas.
     */
    public String eliminarCliente(String documento) {
        Optional<Cliente> clienteEncontrado = buscarCliente(documento);

        if (clienteEncontrado.isEmpty()) {
            return "Cliente no encontrado";
        }

        boolean tieneFacturas = listaFacturas.stream()
                .anyMatch(factura -> factura.cliente().equals(clienteEncontrado.get()));

        if (tieneFacturas) {
            return "No se puede eliminar: el cliente tiene facturas asociadas";
        }

        listaClientes.remove(clienteEncontrado.get());
        return "Cliente eliminado correctamente";
    }

    /**
     * Devuelve una copia de los clientes registrados.
     */
    public List<Cliente> listarClientes() {
        return new ArrayList<>(listaClientes);
    }

    // -------------------- CRUD PRODUCTO --------------------

    /**
     * Registra un producto con un código único.
     */
    public String registrarProducto(Producto producto) {
        if (producto == null) {
            return "Producto inválido";
        }

        if (listaProductos.containsKey(producto.getCodigo())) {
            return "Ya existe un producto con ese código";
        }

        listaProductos.put(producto.getCodigo(), producto);
        return "Producto registrado correctamente";
    }

    /**
     * Busca un producto por su código.
     */
    public Optional<Producto> buscarProducto(String codigo) {
        return Optional.ofNullable(listaProductos.get(codigo));
    }

    /**
     * Actualiza la descripción, el precio y la cantidad disponible.
     */
    public String actualizarProducto(String codigo, String descripcion,
                                     double precio, int cantidad) {
        Optional<Producto> productoEncontrado = buscarProducto(codigo);

        if (productoEncontrado.isEmpty()) {
            return "Producto no encontrado";
        }

        if (precio < 0 || cantidad < 0) {
            return "El precio y la cantidad no pueden ser negativos";
        }

        Producto producto = productoEncontrado.get();
        producto.setValor(precio);
        producto.setCantidadDisponible(cantidad);
        return "Producto actualizado correctamente";
    }

    /**
     * Elimina un producto que no esté incluido en facturas existentes.
     */
    public String eliminarProducto(String codigo) {
        Optional<Producto> productoEncontrado = buscarProducto(codigo);

        if (productoEncontrado.isEmpty()) {
            return "Producto no encontrado";
        }

        boolean apareceEnFactura = listaFacturas.stream()
                .flatMap(factura -> factura.listaDetallesFactura().stream())
                .anyMatch(detalle -> detalle.getProducto().equals(productoEncontrado.get()));

        if (apareceEnFactura) {
            return "No se puede eliminar: el producto aparece en una factura";
        }

        listaProductos.remove(codigo);
        return "Producto eliminado correctamente";
    }

    /**
     * Devuelve los productos registrados.
     */
    public List<Producto> listarProductos() {
        return new ArrayList<>(listaProductos.values());
    }

    // -------------------- CRUD FACTURA --------------------

    /**
     * Registra una factura que tenga código único, cliente válido y productos.
     */
    public String registrarFactura(Factura factura) {
        if (factura == null) {
            return "Factura inválida";
        }

        if (buscarFactura(factura.codigo()).isPresent()) {
            return "Ya existe una factura con ese código";
        }

        if (factura.listaDetallesFactura().isEmpty()) {
            return "La factura debe contener al menos un producto";
        }

        if (buscarCliente(factura.cliente().getDocumentoIdentidad()).isEmpty()) {
            return "El cliente no está registrado en la tienda";
        }

        listaFacturas.add(factura);
        factura.cliente().agregarFactura(factura);
        return "Factura registrada correctamente";
    }

    /**
     * Busca una factura por su código.
     */
    public Optional<Factura> buscarFactura(String codigo) {
        return listaFacturas.stream()
                .filter(factura -> factura.codigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    /**
     * Devuelve una copia de las facturas registradas.
     */
    public List<Factura> listarFacturas() {
        return new ArrayList<>(listaFacturas);
    }

    /**
     * Elimina una factura si no ha sido pagada.
     */
    public String eliminarFactura(String codigo) {
        Optional<Factura> facturaEncontrada = buscarFactura(codigo);

        if (facturaEncontrada.isEmpty()) {
            return "Factura no encontrada";
        }

        Factura factura = facturaEncontrada.get();
        if (factura.estadoFactura() == EstadoFactura.PAGADA) {
            return "No se puede eliminar una factura pagada";
        }

        listaFacturas.remove(factura);
        factura.cliente().eliminarFactura(factura);
        return "Factura eliminada correctamente";
    }

    /**
     * Cambia el estado creando una nueva instancia porque Factura es un record.
     * Las facturas pagadas solo pueden cambiarse mediante el proceso de venta.
     */
    public String cambiarEstadoFactura(String codigo, EstadoFactura estado) {
        Optional<Factura> facturaEncontrada = buscarFactura(codigo);

        if (facturaEncontrada.isEmpty()) {
            return "Factura no encontrada";
        }

        Factura factura = facturaEncontrada.get();
        if (estado == EstadoFactura.PAGADA) {
            return confirmarVenta(codigo);
        }

        if (factura.estadoFactura() == EstadoFactura.PAGADA) {
            return "No se puede cambiar el estado de una factura pagada";
        }

        Factura nuevaFactura = new Factura(
                factura.codigo(), factura.fecha(), factura.total(), estado,
                factura.metodoPago(), factura.cliente(), factura.listaDetallesFactura()
        );

        reemplazarFactura(factura, nuevaFactura);
        return "Estado actualizado correctamente";
    }

    /**
     * Confirma una venta y descuenta del inventario las cantidades facturadas.
     */
    public String confirmarVenta(String codigo) {
        Optional<Factura> facturaEncontrada = buscarFactura(codigo);

        if (facturaEncontrada.isEmpty()) {
            return "Factura no encontrada";
        }

        Factura factura = facturaEncontrada.get();
        if (factura.estadoFactura() != EstadoFactura.GENERADA) {
            return "Solo se pueden confirmar facturas generadas";
        }

        Map<Producto, Integer> cantidadesPorProducto = new HashMap<>();
        for (DetalleFactura detalle : factura.listaDetallesFactura()) {
            cantidadesPorProducto.merge(
                    detalle.getProducto(), detalle.getCantidadComprada(), Integer::sum
            );
        }

        for (Map.Entry<Producto, Integer> entrada : cantidadesPorProducto.entrySet()) {
            if (entrada.getValue() > entrada.getKey().getCantidadDisponible()) {
                return "Inventario insuficiente para " + entrada.getKey().getNombre();
            }
        }

        for (Map.Entry<Producto, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Producto producto = entrada.getKey();
            producto.setCantidadDisponible(
                    producto.getCantidadDisponible() - entrada.getValue()
            );
        }

        Factura facturaPagada = new Factura(
                factura.codigo(), factura.fecha(), factura.total(), EstadoFactura.PAGADA,
                factura.metodoPago(), factura.cliente(), factura.listaDetallesFactura()
        );

        reemplazarFactura(factura, facturaPagada);
        return "Venta confirmada. Inventario actualizado correctamente";
    }

    /**
     * Reemplaza una factura en la tienda y en el historial del cliente.
     */
    private void reemplazarFactura(Factura anterior, Factura nueva) {
        int indice = listaFacturas.indexOf(anterior);

        if (indice >= 0) {
            listaFacturas.set(indice, nueva);
        }

        anterior.cliente().reemplazarFactura(anterior, nueva);
    }

    // -------------------- CONSULTAS Y REPORTES --------------------

    /**
     * Consulta las facturas asociadas al documento de un cliente.
     */
    public List<Factura> consultarFacturasCliente(String documento) {
        return listaFacturas.stream()
                .filter(factura -> factura.cliente().getDocumentoIdentidad().equals(documento))
                .toList();
    }

    /**
     * Consulta las facturas generadas en una fecha específica.
     */
    public List<Factura> consultarFacturasFecha(LocalDate fecha) {
        return listaFacturas.stream()
                .filter(factura -> factura.fecha().equals(fecha))
                .toList();
    }

    /**
     * Calcula el valor vendido en una fecha, contando únicamente facturas pagadas.
     */
    public double obtenerTotalVendido(LocalDate fecha) {
        return listaFacturas.stream()
                .filter(factura -> factura.fecha().equals(fecha)
                        && factura.estadoFactura() == EstadoFactura.PAGADA)
                .mapToDouble(Factura::total)
                .sum();
    }

    /**
     * Calcula el total vendido de todas las facturas pagadas.
     */
    public double obtenerTotalVendido() {
        return listaFacturas.stream()
                .filter(factura -> factura.estadoFactura() == EstadoFactura.PAGADA)
                .mapToDouble(Factura::total)
                .sum();
    }

    /**
     * Devuelve los productos cuya cantidad es igual o inferior al límite indicado.
     */
    public List<Producto> obtenerProductosBajoInventario(int minimo) {
        return listaProductos.values().stream()
                .filter(producto -> producto.getCantidadDisponible() <= minimo)
                .toList();
    }

    /**
     * Devuelve los productos que alcanzan la cantidad mínima indicada.
     */
    public List<Producto> obtenerProductosConInventarioAlto(int minimo) {
        return listaProductos.values().stream()
                .filter(producto -> producto.getCantidadDisponible() >= minimo)
                .toList();
    }

    /**
     * Cuenta las facturas pagadas.
     */
    public long contarFacturasPagadas() {
        return listaFacturas.stream()
                .filter(factura -> factura.estadoFactura() == EstadoFactura.PAGADA)
                .count();
    }

    /**
     * Encuentra el producto con más unidades vendidas.
     */
    public Optional<Producto> obtenerProductoMasVendido() {
        Map<Producto, Integer> cantidadesVendidas = new HashMap<>();

        for (Factura factura : listaFacturas) {
            if (factura.estadoFactura() == EstadoFactura.PAGADA) {
                for (DetalleFactura detalle : factura.listaDetallesFactura()) {
                    cantidadesVendidas.merge(
                            detalle.getProducto(), detalle.getCantidadComprada(), Integer::sum
                    );
                }
            }
        }

        return cantidadesVendidas.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Genera un resumen de las ventas y del inventario.
     */
    public String generarReporteVentas() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("REPORTE DE VENTAS - ").append(nombre).append('\n');
        reporte.append("Facturas registradas: ").append(listaFacturas.size()).append('\n');
        reporte.append("Facturas pagadas: ").append(contarFacturasPagadas()).append('\n');
        reporte.append("Total vendido: $")
                .append(String.format("%,.2f", obtenerTotalVendido())).append('\n');
        reporte.append("Productos con inventario bajo (<= 5):\n");

        for (Producto producto : obtenerProductosBajoInventario(5)) {
            reporte.append(producto).append('\n');
        }

        reporte.append("Producto más vendido: ")
                .append(obtenerProductoMasVendido()
                        .map(Producto::getNombre)
                        .orElse("Sin ventas"));

        return reporte.toString();
    }

    /**
     * Genera un código de factura único.
     */
    public String generarCodigoFactura() {
        String codigo;

        do {
            codigo = String.valueOf(siguienteCodigoFactura);
            siguienteCodigoFactura++;
        } while (buscarFactura(codigo).isPresent());

        return codigo;
    }


    // Punto 1: Productos con cantidad disponible mayor o igual a 10

    public List<Producto> obtenerMayoresDiez() {
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : listaProductos.values()) {
            if (producto.getCantidadDisponible() >= 10) {
                resultado.add(producto);
            }
        }
        return resultado;
    }


    // Punto 2: Códigos de productos dentro del rango de cantidades

    public ArrayList<String> obtenerCodigosProductosAgotados(int limiteInferior, int limiteSuperior) {
        ArrayList<String> resultado = new ArrayList<>();
        for (Producto producto : listaProductos.values()) {
            if (producto.getCantidadDisponible() >= limiteInferior
                    && producto.getCantidadDisponible() < limiteSuperior) {
                resultado.add(producto.getCodigo());
            }
        }
        return resultado;
    }

    //3. Obtener la lista de clientes que hayan comprado el 07 de octubre de 2026

    public ArrayList<Cliente> obtenerClientesCompras2(LocalDate fechaConsulta) {
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        for (Factura factura : listaFacturas) {
            if (factura.fecha().isEqual(fechaConsulta)) {
                listaClientes.add(factura.cliente());
            }
        }
        return listaClientes;
    }

    //punto 4:   Obtener las facturas que tenga un cliente donde su nombre empiece por R

    public ArrayList<Factura> obtenerFacturasClienteConR() {
        ArrayList<Factura> resultado = new ArrayList<>();

        for (Factura factura : listaFacturas) {
            if (factura.tieneClienteConR()) {
                resultado.add(factura);
            }
        }
        return resultado;

    }

    //punto 5:   Obtener las facturas donde se haya comprado un celular de marca Iphone 16 pro max

    public ArrayList<Factura> obtenerFacturaIphone16() {
        ArrayList<Factura> resultado = new ArrayList<>();

        for (Factura factura : listaFacturas) {
            if (factura.tieneIphone()) {
                resultado.add(factura);
            }
        }
        return resultado;
    }

    //punto 6:   Obtener las facturas que tenga un cliente
    // donde su nombre sea juan y haya comprado un celular de marca Iphone 16 pro max

    public ArrayList<Factura> obtenerFacturasClienteJuanYIphone() {
        ArrayList<Factura> resultado = new ArrayList<>();

        for (Factura factura : listaFacturas) {
            if (factura.tieneClienteJuan() && factura.tieneIphone()) {
                resultado.add(factura);
            }
        }
        return resultado;

    }


    // Punto 7: Implementar un método que reciba una categoría
    //  y retorne todos los productos registrados que pertenezcan a ella.

    public ArrayList<Producto> productosPorCategoria(Categoria categoria) {
        ArrayList<Producto> resultado = new ArrayList<>();

        for (Producto producto : listarProductos()) {
            if (producto.getCategoria() == categoria) {
                resultado.add(producto);
            }
        }
        return resultado;
    }


    //punto 8 : Implementar un método que reciba un precio mínimo y un precio máximo, y retorne
    // los productos cuyo precio se encuentre dentro de ese rango, incluyendo ambos límites.

    public ArrayList<Producto> obtenerProductosPorPrecio(double precioMin, double precioMax) {
        ArrayList<Producto> resultado = new ArrayList<>();
        for (Producto producto : listarProductos()) {
            if (producto.getValor() >= precioMin && producto.getValor() <= precioMax) {
                resultado.add(producto);
            }
        }
        return resultado;
    }


    //punto 9: Implementar un método que retorne todos los productos
    // registrados en la tienda, ordenados de menor a mayor según su precio.

    public ArrayList<Producto> productosOrdenados() {
        ArrayList<Producto> resultado = new ArrayList<>();
        for (Producto producto : listarProductos()) {
            resultado.add(producto);
        }
        resultado.sort(Comparator.comparingDouble(Producto::getValor));
        return resultado;
    }

    //punto 10: Implementar un método que identifique el producto con el precio más alto de la tienda.
    // Si no existen productos registrados, el método debe retornar un Optional vacío.

    public Optional<Producto> productoMasCaro() {
        return listaProductos.values().stream()
                .max(Comparator.comparingDouble(Producto::getValor));
    }

    //Punto 11: Implementar un método que reciba el nombre de una ciudad y retorne
    // todos los clientes que residan en ella.
    // La búsqueda debe realizarse sin diferenciar entre mayúsculas y minúsculas.

    public ArrayList<Cliente> obtenerClientesPorCiudad(String ciudad) {
        ArrayList<Cliente> resultado = new ArrayList<>();
        for (Cliente cliente : listaClientes) {
            if (cliente.getCiudadResidencia().equalsIgnoreCase(ciudad)) {
                resultado.add(cliente);
            }
        }
            return resultado;

    }

    public List<Cliente> obtenerClientesPorCiudad2(String ciudad) {
        return listaClientes.stream()
                .filter(cliente -> cliente.getCiudadResidencia()
                        .equalsIgnoreCase(ciudad))
                .toList();
    }
}
