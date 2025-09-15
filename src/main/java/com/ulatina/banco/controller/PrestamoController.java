/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Prestamo;
import com.ulatina.banco.service.PrestamoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author santiago
 * Controlador simplificado para calculadora de préstamos
 * No maneja datos en base de datos, solo cálculos en memoria
 */

@Named
@ViewScoped
public class PrestamoController implements Serializable {
    
    private PrestamoService prestamoService = new PrestamoService();
    
    private BigDecimal monto;
    private int anos;
    private BigDecimal tasaAnual;
    private List<Prestamo> tablaAmortizacion;
    

    public void calcularTabla() {
        try {
            // Validaciones básicas
            if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El monto debe ser mayor a cero");
                return;
            }
            
            if (anos <= 0 || anos > 30) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Los años deben estar entre 1 y 30");
                return;
            }
            
            if (tasaAnual == null || tasaAnual.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "La tasa debe ser mayor a cero");
                return;
            }
            
            // Generar tabla de amortización
            tablaAmortizacion = prestamoService.calcularTablaAmortizacion(monto, tasaAnual, anos);
            
            if (!tablaAmortizacion.isEmpty()) {
                BigDecimal cuotaMensual = tablaAmortizacion.get(0).getCuota();
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Cálculo exitoso", 
                    String.format("Tabla generada. Cuota mensual: ₡%,.2f", cuotaMensual));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error del sistema", 
                "Error en el cálculo: " + e.getMessage());
        }
    }
   
    public String getCuotaMensualCalculada() {
        if (monto != null && anos > 0 && tasaAnual != null) {
            try {
                BigDecimal cuota = prestamoService.obtenerCuotaMensual(monto, tasaAnual, anos);
                return String.format("₡%,.2f", cuota);
            } catch (Exception e) {
                return "Error en cálculo";
            }
        }
        return "Ingrese datos válidos";
    }
    
    public void limpiarFormulario() {
        monto = null;
        anos = 0;
        tasaAnual = null;
        tablaAmortizacion = null;
    }
    
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }
    
 
    public BigDecimal getMonto() { return monto; }    
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public int getAnos() { return anos; }   
    public void setAnos(int anos) { this.anos = anos; }
    
    public BigDecimal getTasaAnual() { return tasaAnual; }   
    public void setTasaAnual(BigDecimal tasaAnual) { this.tasaAnual = tasaAnual; }
    
    public List<Prestamo> getTablaAmortizacion() { return tablaAmortizacion; }  
    public void setTablaAmortizacion(List<Prestamo> tablaAmortizacion) { this.tablaAmortizacion = tablaAmortizacion; }
}
