/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author santi
 */
public class ReporteGenerado {
    private int id;
    private int clienteId;
    private String nombreCliente;
    private String cedulaCliente;
    private String tipoReporte;
    private Date fechaGeneracion;
    private String rutaXML;
    private String estado;
    private BigDecimal totalActivos;
    private BigDecimal totalPasivos;
    private BigDecimal patrimonioNeto;
    private int numeroCuentas;

    public ReporteGenerado() {}

    public ReporteGenerado(int clienteId, String nombreCliente, String cedulaCliente, 
                          String tipoReporte, String estado) {
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.cedulaCliente = cedulaCliente;
        this.tipoReporte = tipoReporte;
        this.estado = estado;
        this.fechaGeneracion = new Date();
    }

    // Método para generar nombre único de archivo
    public String generarNombreArchivo(String extension) {
        return String.format("estado_financiero_%d_%d.%s", 
                clienteId, System.currentTimeMillis(), extension);
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }

    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }

    public Date getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Date fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getRutaXML() { return rutaXML; }
    public void setRutaXML(String rutaXML) { this.rutaXML = rutaXML; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getTotalActivos() { return totalActivos; }
    public void setTotalActivos(BigDecimal totalActivos) { this.totalActivos = totalActivos; }

    public BigDecimal getTotalPasivos() { return totalPasivos; }
    public void setTotalPasivos(BigDecimal totalPasivos) { this.totalPasivos = totalPasivos; }

    public BigDecimal getPatrimonioNeto() { return patrimonioNeto; }
    public void setPatrimonioNeto(BigDecimal patrimonioNeto) { this.patrimonioNeto = patrimonioNeto; }

    public int getNumeroCuentas() { return numeroCuentas; }
    public void setNumeroCuentas(int numeroCuentas) { this.numeroCuentas = numeroCuentas; }
}
