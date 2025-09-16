/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

import java.util.Date;

/**
 *
 * @author esteban
 * @since 15/9/25
 * 
 * - Requerimiento BNK-F-013
 * 
 */
public class Reclamo {
    
    private int id;
    private int clienteId;
    private String correoElectronico;
    private String descripcion;
    private Date fechaCreacion;
    private String estado;

    public Reclamo() {
    }

    public Reclamo(int id, int clienteId, String correoElectronico, String descripcion, Date fechaCreacion, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.correoElectronico = correoElectronico;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }

    // Método para generar número de ticket único
    public String getNumeroTicket() {
        return String.format("TKT-00" + this.id);
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getClienteId() {return clienteId;}
    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

    public String getCorreoElectronico() {return correoElectronico;}
    public void setCorreoElectronico(String correoElectronico) {this.correoElectronico = correoElectronico;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public Date getFechaCreacion() {return fechaCreacion;}
    public void setFechaCreacion(Date fechaCreacion) {this.fechaCreacion = fechaCreacion;}

    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}
     
}
