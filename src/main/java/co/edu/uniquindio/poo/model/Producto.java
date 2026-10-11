package co.edu.uniquindio.poo.model;

/** Representa un producto tecnológico disponible en el inventario de la tienda. */
public class Producto {

    private final String nombre;
    private final String codigo;
    private final String descripcion;
    private int cantidadDisponible;
    private double valor;
    private final Categoria categoria;
    private final Tienda ownedByTienda;

    /** Crea un producto y valida que su precio y existencia no sean negativos. */
    public Producto(String nombre, String codigo, String descripcion,
                    int cantidadDisponible, double valor,
                    Categoria categoria, Tienda tienda) {
        if (cantidadDisponible < 0 || valor < 0) {
            throw new IllegalArgumentException("Cantidad y precio no pueden ser negativos");
        }

        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadDisponible = cantidadDisponible;
        this.valor = valor;
        this.categoria = categoria;
        this.ownedByTienda = tienda;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public double getValor() {
        return valor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Tienda getOwnedByTienda() {
        return ownedByTienda;
    }

    /** Actualiza la existencia disponible del producto. */
    public void setCantidadDisponible(int cantidadDisponible) {
        if (cantidadDisponible < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        this.cantidadDisponible = cantidadDisponible;
    }

    /** Actualiza el precio del producto. */
    public void setValor(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.valor = valor;
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | $" + String.format("%,.2f", valor)
                + " | Stock: " + cantidadDisponible + " | " + categoria;
    }
}
