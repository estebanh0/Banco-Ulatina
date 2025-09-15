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
 * - Creación de dos enum que contienen los tipos de moneda ademas del estado correspondiente de la cuenta
 * - Método numeroMostrar, el cual basicamente por metodos de seguridad no nos mostrará todos los digitos de la cuenta, si no solo los ultimos 4
 * - Método saldoFormateado el cual se encarga de separar los numeros de una manera ordenada, por ejemplo
 *   150000 -> 150,000
 * 
 */
public class Cuenta {

    private int id;
    private String numeroCuenta;
    private int clienteId;
    private Moneda moneda;
    private EstadoCuenta estado;
    private BigDecimal saldo;

    //Enums moneda y estado de cuenta
    
    public enum Moneda {
        CRC("Colones Costarricenses", "₡"),
        USD("Dólares Estadounidenses", "$");

        private String descripcion;
        private String simbolo;

        Moneda(String descripcion, String simbolo) {
            this.descripcion = descripcion;
            this.simbolo = simbolo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getSimbolo() {
            return simbolo;
        }
    }

    public enum EstadoCuenta {
        
        ACTIVA("Activa"),
        BLOQUEADA("Bloqueada"),
        CERRADA("Cerrada");

        private String descripcion;

        EstadoCuenta(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    
    
    public Cuenta() {
    }

    public Cuenta(int id, String numeroCuenta, int clienteId, Moneda moneda, EstadoCuenta estado, BigDecimal saldo) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.clienteId = clienteId;
        this.moneda = moneda;
        this.estado = estado;
        this.saldo = saldo;
    }
    
    // Métodos

    public String getNumeroMostrar() {
        if (numeroCuenta != null && numeroCuenta.length() >= 4) {
            return "****" + numeroCuenta.substring(numeroCuenta.length() - 4);
        }
        return "****";
    }
    
    public String getSaldoFormateado() {
        return moneda.getSimbolo() + " " + String.format("%,.2f", saldo);
    }
    
    
    // GETTERS / SETTERS
    
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getNumeroCuenta() {return numeroCuenta;}
    public void setNumeroCuenta(String numeroCuenta) {this.numeroCuenta = numeroCuenta;}

    public int getClienteId() {return clienteId;}
    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

    public Moneda getMoneda() {return moneda;}
    public void setMoneda(Moneda moneda) {this.moneda = moneda;}

    public BigDecimal getSaldo() {return saldo;}
    public void setSaldo(BigDecimal saldo) {this.saldo = saldo;}

    public EstadoCuenta getEstado() {return estado;}
    public void setEstado(EstadoCuenta estado) {this.estado = estado;}

}
