/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.model;

/**
 *
 * @author santiago
 */
import java.math.BigDecimal;

public class Prestamo {
    private int periodo;
    private BigDecimal interes;
    private BigDecimal amortizacionCapital;
    private BigDecimal cuota;
    private BigDecimal capitalPendiente;

    public Prestamo() {}

    public Prestamo(int periodo, BigDecimal interes, BigDecimal amortizacionCapital, 
                       BigDecimal cuota, BigDecimal capitalPendiente) {
        this.periodo = periodo;
        this.interes = interes;
        this.amortizacionCapital = amortizacionCapital;
        this.cuota = cuota;
        this.capitalPendiente = capitalPendiente;
    }

    public String getInteresFormateado() {
        return String.format("₡%,.2f", interes);
    }

    public String getAmortizacionFormateada() {
        return String.format("₡%,.2f", amortizacionCapital);
    }

    public String getCuotaFormateada() {
        return String.format("₡%,.2f", cuota);
    }

    public String getCapitalPendienteFormateado() {
        return String.format("₡%,.2f", capitalPendiente);
    }

    public int getPeriodo() { return periodo; }

    public void setPeriodo(int periodo) { this.periodo = periodo; }

    public BigDecimal getInteres() { return interes; }

    public void setInteres(BigDecimal interes) { this.interes = interes; }

    public BigDecimal getAmortizacionCapital() { return amortizacionCapital; }

    public void setAmortizacionCapital(BigDecimal amortizacionCapital) { this.amortizacionCapital = amortizacionCapital; }

    public BigDecimal getCuota() { return cuota; }

    public void setCuota(BigDecimal cuota) { this.cuota = cuota; }

    public BigDecimal getCapitalPendiente() { return capitalPendiente; }

    public void setCapitalPendiente(BigDecimal capitalPendiente) { this.capitalPendiente = capitalPendiente; }
}