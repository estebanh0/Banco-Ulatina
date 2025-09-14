/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

import java.math.BigDecimal;

/**
 *
 * @author esteban
 * @since 12/9/25
 * 
 */
public class Reversa {
    private int id;
    private int clienteId;
    private int cuentaId;
    private String descripcion;
    private BigDecimal monto;
    private String estado;

    public Reversa() {}

    public Reversa(int clienteId, int cuentaId, String descripcion, BigDecimal monto, String estado) {
        this.clienteId = clienteId;
        this.cuentaId = cuentaId;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
    }

    public String getEstado() {return estado;}

    public void setEstado(String estado) {this.estado = estado;}

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public int getClienteId() {return clienteId;}

    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

    public int getCuentaId() {return cuentaId;}

    public void setCuentaId(int cuentaId) {this.cuentaId = cuentaId;}

    public String getDescripcion() {return descripcion;}

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public BigDecimal getMonto() {return monto;}

    public void setMonto(BigDecimal monto) {this.monto = monto;}
    
    
}
