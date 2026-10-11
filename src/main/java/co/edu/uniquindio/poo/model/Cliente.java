package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Representa un cliente registrado en TechStore y sus facturas asociadas. */
public class Cliente {

    private final String documentoIdentidad;
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private String ciudadResidencia;
    private final List<Factura> listaFacturas = new ArrayList<>();
    private final Tienda ownedByTienda;

    /** Crea un cliente con sus datos personales y la tienda a la que pertenece. */
    public Cliente(String documentoIdentidad, String nombreCompleto,
                   String telefono, String correo, String ciudadResidencia,
                   Tienda tienda) {
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
        this.ciudadResidencia = ciudadResidencia;
        this.ownedByTienda = tienda;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCiudadResidencia() {
        return ciudadResidencia;
    }

    public Tienda getOwnedByTienda() {
        return ownedByTienda;
    }

    public List<Factura> getListaFacturas() {
        return Collections.unmodifiableList(listaFacturas);
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setCiudadResidencia(String ciudadResidencia) {
        this.ciudadResidencia = ciudadResidencia;
    }

    /** Agrega una factura al historial del cliente. */
    public void agregarFactura(Factura factura) {
        listaFacturas.add(factura);
    }

    /** Sustituye una factura cuando cambia su estado. */
    public void reemplazarFactura(Factura anterior, Factura nueva) {
        int indice = listaFacturas.indexOf(anterior);

        if (indice >= 0) {
            listaFacturas.set(indice, nueva);
        }
    }

    /** Elimina una factura del historial del cliente. */
    public void eliminarFactura(Factura factura) {
        listaFacturas.remove(factura);
    }

    @Override
    public String toString() {
        return documentoIdentidad + " | " + nombreCompleto + " | " + telefono
                + " | " + correo + " | " + ciudadResidencia;
    }
}
