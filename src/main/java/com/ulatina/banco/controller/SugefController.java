/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import java.math.BigDecimal;
import com.ulatina.banco.service.SugefService;

/**
 *
 * @author esteban
 * @since 15/9/25
 * 
 * - Metodo para monitorear las transacciones de los clientes
 */
public class SugefController {
    
    private SugefService sugefService = new SugefService();
    
    // Reportes para 10 millones
    private static final BigDecimal REPORTE_LIMITE = new BigDecimal("10000000");
    
    public void monitorearTransaccion(int clienteId, int cuentaId, String tipoOperacion, BigDecimal monto) {
        try {
            // Verificar si el monto el limite
            if (monto.compareTo(REPORTE_LIMITE) >= 0) {
                
                // Crear y registrar reporte automáticamente
                sugefService.crearReporteSUGEF(clienteId, cuentaId, tipoOperacion, monto);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
