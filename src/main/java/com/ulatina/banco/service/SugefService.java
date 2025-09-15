/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.ReporteSugef;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author esteban
 * @since 15/9/25
 */
public class SugefService extends Service{
    
    public boolean crearReporteSUGEF(int clienteId, int cuentaId, String tipoOperacion, BigDecimal monto) 
            throws SQLException, ClassNotFoundException {
        
        PreparedStatement ps = null;
        
        try {
            conectarBD();
            
            // Crear objeto reporte
            ReporteSugef reporte = new ReporteSugef(clienteId, cuentaId, tipoOperacion, monto, "ROS", "GENERADO");
            reporte.generarNumeroReferencia();
            
            String sql = "INSERT INTO reporte_sugef (cliente_id, cuenta_id, tipo_operacion, monto, tipo_reporte, numero_referencia, estado) " +
                                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, reporte.getClienteId());
            ps.setInt(2, reporte.getCuentaId());
            ps.setString(3, reporte.getTipoOperacion());
            ps.setBigDecimal(4, reporte.getMonto());
            ps.setString(5, reporte.getTipoReporte());
            ps.setString(6, reporte.getNumeroReferencia());
            ps.setString(7, reporte.getEstado());
            
            int resultado = ps.executeUpdate();
            
            return resultado > 0;
            
        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }
    
    
}
