/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

import java.math.BigDecimal;

/**
 *
 * @author esteban
 * @since 15/9/25
 */
public class ReporteSugef {
    
    private int id;
    private int clienteId;
    private int cuentaId;
    private BigDecimal monto;
    private String numeroReferencia;
    private String tipoReporte;
    private String tipoOperacion;
    private String estado;
    

    public ReporteSugef() {
    }

    public ReporteSugef(int clienteId, int cuentaId, String tipoOperacion, BigDecimal monto, String tipoReporte, String estado) {
        this.clienteId = clienteId;
        this.cuentaId = cuentaId;
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
        this.tipoReporte = tipoReporte;
        this.estado = estado;
    }
    
    // Método para generar número de referencia único
    public void generarNumeroReferencia() {
        this.numeroReferencia = "ROS-" + this.clienteId + "-" + this.cuentaId;
    }

    public String getTipoReporte() {return tipoReporte;}
    public void setTipoReporte(String tipoReporte) {this.tipoReporte = tipoReporte;}

    public String getTipoOperacion() {return tipoOperacion;}
    public void setTipoOperacion(String tipoOperacion) {this.tipoOperacion = tipoOperacion;}
    
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getClienteId() {return clienteId;}
    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

    public int getCuentaId() {return cuentaId;}
    public void setCuentaId(int cuentaId) {this.cuentaId = cuentaId;}

    public BigDecimal getMonto() {return monto;}
    public void setMonto(BigDecimal monto) {this.monto = monto;}

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}

    public String getNumeroReferencia() {return numeroReferencia;}
    public void setNumeroReferencia(String numeroReferencia) {this.numeroReferencia = numeroReferencia;}
    
    
    
}
