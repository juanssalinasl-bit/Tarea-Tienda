package co.edu.uniquindio.poo.model;

/** Representa un producto, su cantidad y el precio aplicado en una factura. */
public class DetalleFactura {

    private final int cantidadComprada;
    private final double precioAplicado;
    private final double subTotal;
    private final Producto producto;

    /** Crea un detalle y calcula su subtotal. */
    public DetalleFactura(int cantidadComprada, double precioAplicado,
                          Producto producto) {
        if (cantidadComprada <= 0 || precioAplicado < 0 || producto == null) {
            throw new IllegalArgumentException("Cantidad, precio o producto inválido");
        }

        this.cantidadComprada = cantidadComprada;
        this.precioAplicado = precioAplicado;
        this.subTotal = cantidadComprada * precioAplicado;
        this.producto = producto;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public double getPrecioAplicado() {
        return precioAplicado;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public Producto getProducto() {
        return producto;
    }

    @Override
    public String toString() {
        return producto.getNombre() + " x " + cantidadComprada
                + " | $" + String.format("%,.2f", precioAplicado) + " c/u"
                + " | Subtotal: $" + String.format("%,.2f", subTotal);
    }
}
