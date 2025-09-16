/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 *
 * @author santi
 * Modelo para representar el estado financiero de un cliente
 */



public class EstadoFinanciero {
    private Cliente cliente;
    private List<Cuenta> cuentas;
    private Date fechaGeneracion;
    
    // Totales calculados
    private BigDecimal totalActivosCRC;
    private BigDecimal totalActivosUSD;
    private BigDecimal totalPasivosCRC;
    private BigDecimal totalPasivosUSD;
    private BigDecimal patrimonioNetoCRC;
    private BigDecimal patrimonioNetoUSD;
    
    // Detalles por tipo de cuenta
    private BigDecimal cuentasCorrientesCRC;
    private BigDecimal cuentasCorrientesUSD;
    private BigDecimal cuentasAhorroCRC;
    private BigDecimal cuentasAhorroUSD;
    
    // Estadísticas adicionales
    private int totalCuentas;
    private int cuentasActivas;
    private int cuentasBloqueadas;
    private int cuentasCerradas;

    public EstadoFinanciero() {
        this.fechaGeneracion = new Date();
        inicializarTotales();
    }

    public EstadoFinanciero(Cliente cliente, List<Cuenta> cuentas) {
        this.cliente = cliente;
        this.cuentas = cuentas;
        this.fechaGeneracion = new Date();
        inicializarTotales();
        calcularTotales();
    }

    private void inicializarTotales() {
        this.totalActivosCRC = BigDecimal.ZERO;
        this.totalActivosUSD = BigDecimal.ZERO;
        this.totalPasivosCRC = BigDecimal.ZERO;
        this.totalPasivosUSD = BigDecimal.ZERO;
        this.patrimonioNetoCRC = BigDecimal.ZERO;
        this.patrimonioNetoUSD = BigDecimal.ZERO;
        this.cuentasCorrientesCRC = BigDecimal.ZERO;
        this.cuentasCorrientesUSD = BigDecimal.ZERO;
        this.cuentasAhorroCRC = BigDecimal.ZERO;
        this.cuentasAhorroUSD = BigDecimal.ZERO;
    }

    public void calcularTotales() {
        if (cuentas == null || cuentas.isEmpty()) {
            return;
        }

        inicializarTotales();
        
        for (Cuenta cuenta : cuentas) {
            BigDecimal saldo = cuenta.getSaldo();
            
            // Contar por estado
            switch (cuenta.getEstado()) {
                case ACTIVA:
                    cuentasActivas++;
                    break;
                case BLOQUEADA:
                    cuentasBloqueadas++;
                    break;
                case CERRADA:
                    cuentasCerradas++;
                    break;
            }
            
            // Solo contar saldos de cuentas activas
            if (cuenta.getEstado() == Cuenta.EstadoCuenta.ACTIVA && saldo != null) {
                if (cuenta.getMoneda() == Cuenta.Moneda.CRC) {
                    totalActivosCRC = totalActivosCRC.add(saldo);
                    // Asumimos que todas son cuentas corrientes por simplicidad
                    cuentasCorrientesCRC = cuentasCorrientesCRC.add(saldo);
                } else if (cuenta.getMoneda() == Cuenta.Moneda.USD) {
                    totalActivosUSD = totalActivosUSD.add(saldo);
                    cuentasCorrientesUSD = cuentasCorrientesUSD.add(saldo);
                }
            }
        }
        
        // Por ahora no tenemos pasivos, así que patrimonio = activos
        patrimonioNetoCRC = totalActivosCRC.subtract(totalPasivosCRC);
        patrimonioNetoUSD = totalActivosUSD.subtract(totalPasivosUSD);
        
        totalCuentas = cuentas.size();
    }

    // Métodos de formato para reportes
    public String getTotalActivosFormateado() {
        String crc = String.format("₡%,.2f", totalActivosCRC);
        String usd = String.format("$%,.2f", totalActivosUSD);
        return String.format("CRC: %s | USD: %s", crc, usd);
    }

    public String getPatrimonioNetoFormateado() {
        String crc = String.format("₡%,.2f", patrimonioNetoCRC);
        String usd = String.format("$%,.2f", patrimonioNetoUSD);
        return String.format("CRC: %s | USD: %s", crc, usd);
    }

    public BigDecimal getTotalActivosConsolidado() {
        // Retorna total en colones (asumiendo tasa de cambio 1 USD = 500 CRC)
        BigDecimal tasaCambio = new BigDecimal("500");
        return totalActivosCRC.add(totalActivosUSD.multiply(tasaCambio));
    }

    public BigDecimal getPatrimonioNetoConsolidado() {
        BigDecimal tasaCambio = new BigDecimal("500");
        return patrimonioNetoCRC.add(patrimonioNetoUSD.multiply(tasaCambio));
    }

    // Getters y Setters
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<Cuenta> getCuentas() { return cuentas; }
    public void setCuentas(List<Cuenta> cuentas) { 
        this.cuentas = cuentas;
        calcularTotales();
    }

    public Date getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Date fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public BigDecimal getTotalActivosCRC() { return totalActivosCRC; }
    public void setTotalActivosCRC(BigDecimal totalActivosCRC) { this.totalActivosCRC = totalActivosCRC; }

    public BigDecimal getTotalActivosUSD() { return totalActivosUSD; }
    public void setTotalActivosUSD(BigDecimal totalActivosUSD) { this.totalActivosUSD = totalActivosUSD; }

    public BigDecimal getTotalPasivosCRC() { return totalPasivosCRC; }
    public void setTotalPasivosCRC(BigDecimal totalPasivosCRC) { this.totalPasivosCRC = totalPasivosCRC; }

    public BigDecimal getTotalPasivosUSD() { return totalPasivosUSD; }
    public void setTotalPasivosUSD(BigDecimal totalPasivosUSD) { this.totalPasivosUSD = totalPasivosUSD; }

    public BigDecimal getPatrimonioNetoCRC() { return patrimonioNetoCRC; }
    public void setPatrimonioNetoCRC(BigDecimal patrimonioNetoCRC) { this.patrimonioNetoCRC = patrimonioNetoCRC; }

    public BigDecimal getPatrimonioNetoUSD() { return patrimonioNetoUSD; }
    public void setPatrimonioNetoUSD(BigDecimal patrimonioNetoUSD) { this.patrimonioNetoUSD = patrimonioNetoUSD; }

    public BigDecimal getCuentasCorrientesCRC() { return cuentasCorrientesCRC; }
    public void setCuentasCorrientesCRC(BigDecimal cuentasCorrientesCRC) { this.cuentasCorrientesCRC = cuentasCorrientesCRC; }

    public BigDecimal getCuentasCorrientesUSD() { return cuentasCorrientesUSD; }
    public void setCuentasCorrientesUSD(BigDecimal cuentasCorrientesUSD) { this.cuentasCorrientesUSD = cuentasCorrientesUSD; }

    public BigDecimal getCuentasAhorroCRC() { return cuentasAhorroCRC; }
    public void setCuentasAhorroCRC(BigDecimal cuentasAhorroCRC) { this.cuentasAhorroCRC = cuentasAhorroCRC; }

    public BigDecimal getCuentasAhorroUSD() { return cuentasAhorroUSD; }
    public void setCuentasAhorroUSD(BigDecimal cuentasAhorroUSD) { this.cuentasAhorroUSD = cuentasAhorroUSD; }

    public int getTotalCuentas() { return totalCuentas; }
    public void setTotalCuentas(int totalCuentas) { this.totalCuentas = totalCuentas; }

    public int getCuentasActivas() { return cuentasActivas; }
    public void setCuentasActivas(int cuentasActivas) { this.cuentasActivas = cuentasActivas; }

    public int getCuentasBloqueadas() { return cuentasBloqueadas; }
    public void setCuentasBloqueadas(int cuentasBloqueadas) { this.cuentasBloqueadas = cuentasBloqueadas; }

    public int getCuentasCerradas() { return cuentasCerradas; }
    public void setCuentasCerradas(int cuentasCerradas) { this.cuentasCerradas = cuentasCerradas; }
}
