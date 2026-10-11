package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Representa una factura inmutable. Para cambiar su estado se crea una nueva instancia.
 */
public record Factura(
        String codigo,
        LocalDate fecha,
        double total,
        EstadoFactura estadoFactura,
        MetodoPago metodoPago,
        Cliente cliente,
        ArrayList<DetalleFactura> listaDetallesFactura) {

    /** Valida los datos y calcula el total a partir de los detalles registrados. */
    public Factura {
        if (codigo == null || fecha == null || estadoFactura == null
                || metodoPago == null || cliente == null || listaDetallesFactura == null) {
            throw new IllegalArgumentException("Los datos de la factura están incompletos");
        }

        listaDetallesFactura = new ArrayList<>(listaDetallesFactura);
        total = listaDetallesFactura.stream()
                .mapToDouble(DetalleFactura::getSubTotal)
                .sum();
    }

    /** Calcula el total de los productos incluidos en la factura. */
    public double calcularTotal() {
        return listaDetallesFactura.stream()
                .mapToDouble(DetalleFactura::getSubTotal)
                .sum();
    }

    @Override
    public String toString() {
        return codigo + " | " + fecha
                + " | " + cliente.getNombreCompleto()
                + " | $" + String.format("%,.2f", total)
                + " | " + estadoFactura
                + " | " + metodoPago;
    }

    public boolean tieneClienteConR() {

        boolean resultado = false;
        resultado = cliente.verificarNombreConR();
        return resultado ;

    }

    public boolean tieneIphone() {

        boolean resultado = false;
        resultado = listaDetallesFactura.getLast().verificarIhpone();
        return resultado;

    }

    public boolean tieneClienteJuan() {
        boolean resultado = false;
        resultado = cliente.verificarNombreJuan();
        return resultado ;

    }

}
