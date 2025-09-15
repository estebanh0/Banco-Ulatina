/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

/**
 *
 * @author esteban
 */
public class Cliente {
    private int id;
    private String cedula,nombre;
    private boolean embargo;

    public Cliente() {}

    public Cliente(int id, String cedula, String nombre, boolean embargo) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.embargo = embargo;
    }

    //Para validación del cliente en el ingreso
    public Cliente(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getCedula() {return cedula;}
    public void setCedula(String cedula) {this.cedula = cedula;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public boolean isEmbargo() {return embargo;}
    public void setEmbargo(boolean embargo) {this.embargo = embargo;}

}
