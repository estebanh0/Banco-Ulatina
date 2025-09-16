/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.Cliente;
import com.ulatina.banco.model.Cuenta;
import com.ulatina.banco.model.EstadoFinanciero;
import com.ulatina.banco.model.ReporteGenerado;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author esteban
 * @since 15/9/25
 * 
 * Servicio para generar reportes financieros y exportarlos en múltiples formatos
 */
public class ReporteService extends Service {
    
    private CuentaService cuentaService = new CuentaService();
    private ClienteService clienteService = new ClienteService();
    
    
    public EstadoFinanciero generarEstadoFinanciero(int clienteId) 
            throws SQLException, ClassNotFoundException {
        
        // Obtener información del cliente
        Cliente cliente = clienteService.buscarClientePorId(clienteId);
        
        // Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaService.obtenerCuentasPorClienteId(clienteId);
        
        // Crear y calcular estado financiero
        EstadoFinanciero estado = new EstadoFinanciero(cliente, cuentas);
        
        return estado;
    }
    
    public int guardarReporte(ReporteGenerado reporte) 
            throws SQLException, ClassNotFoundException {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conectarBD();
            
            String sql = "INSERT INTO reporte_generado (cliente_id, nombre_cliente, cedula_cliente, " +
                         "ruta_xml, total_activos, patrimonio_neto) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            ps = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reporte.getClienteId());
            ps.setString(2, reporte.getNombreCliente());
            ps.setString(3, reporte.getCedulaCliente());
            ps.setString(4, reporte.getRutaXML());
            ps.setBigDecimal(5, reporte.getTotalActivos());
            ps.setBigDecimal(6, reporte.getPatrimonioNeto());
            
            int resultado = ps.executeUpdate();
            
            if (resultado > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Retorna el ID generado
                }
            }
            
            return 0;
            
        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }
    
    public void exportarXML(EstadoFinanciero estado, String rutaArchivo) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<EstadoFinanciero>\n");
        xml.append("  <Cliente>\n");
        xml.append("    <Id>").append(estado.getCliente().getId()).append("</Id>\n");
        xml.append("    <Cedula>").append(estado.getCliente().getCedula()).append("</Cedula>\n");
        xml.append("    <Nombre>").append(estado.getCliente().getNombre()).append("</Nombre>\n");
        xml.append("    <Embargo>").append(estado.getCliente().isEmbargo()).append("</Embargo>\n");
        xml.append("  </Cliente>\n");
        xml.append("  <FechaGeneracion>").append(dateFormat.format(estado.getFechaGeneracion())).append("</FechaGeneracion>\n");
        xml.append("  <Resumen>\n");
        xml.append("    <TotalCuentas>").append(estado.getTotalCuentas()).append("</TotalCuentas>\n");
        xml.append("    <CuentasActivas>").append(estado.getCuentasActivas()).append("</CuentasActivas>\n");
        xml.append("    <CuentasBloqueadas>").append(estado.getCuentasBloqueadas()).append("</CuentasBloqueadas>\n");
        xml.append("    <CuentasCerradas>").append(estado.getCuentasCerradas()).append("</CuentasCerradas>\n");
        xml.append("  </Resumen>\n");
        xml.append("  <Activos>\n");
        xml.append("    <TotalActivosCRC>").append(estado.getTotalActivosCRC()).append("</TotalActivosCRC>\n");
        xml.append("    <TotalActivosUSD>").append(estado.getTotalActivosUSD()).append("</TotalActivosUSD>\n");
        xml.append("    <CuentasCorrientesCRC>").append(estado.getCuentasCorrientesCRC()).append("</CuentasCorrientesCRC>\n");
        xml.append("    <CuentasCorrientesUSD>").append(estado.getCuentasCorrientesUSD()).append("</CuentasCorrientesUSD>\n");
        xml.append("  </Activos>\n");
        xml.append("  <Patrimonio>\n");
        xml.append("    <PatrimonioNetoCRC>").append(estado.getPatrimonioNetoCRC()).append("</PatrimonioNetoCRC>\n");
        xml.append("    <PatrimonioNetoUSD>").append(estado.getPatrimonioNetoUSD()).append("</PatrimonioNetoUSD>\n");
        xml.append("  </Patrimonio>\n");
        xml.append("  <Cuentas>\n");
        
        for (Cuenta cuenta : estado.getCuentas()) {
            xml.append("    <Cuenta>\n");
            xml.append("      <Id>").append(cuenta.getId()).append("</Id>\n");
            xml.append("      <Numero>").append(cuenta.getNumeroCuenta()).append("</Numero>\n");
            xml.append("      <Moneda>").append(cuenta.getMoneda()).append("</Moneda>\n");
            xml.append("      <Estado>").append(cuenta.getEstado()).append("</Estado>\n");
            xml.append("      <Saldo>").append(cuenta.getSaldo()).append("</Saldo>\n");
            xml.append("    </Cuenta>\n");
        }
        
        xml.append("  </Cuentas>\n");
        xml.append("</EstadoFinanciero>");
        
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(xml.toString());
        }
    }
    
    public List<ReporteGenerado> obtenerReportesGenerados() 
            throws SQLException, ClassNotFoundException {
        
        List<ReporteGenerado> reportes = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conectarBD();
            
            String sql = "SELECT id, cliente_id, nombre_cliente, cedula_cliente, tipo_reporte, " +
                        "fecha_generacion, ruta_xml, " +
                        "total_activos, patrimonio_neto " +
                        "FROM reporte_generado ORDER BY fecha_generacion DESC";
            
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                ReporteGenerado reporte = new ReporteGenerado();
                reporte.setId(rs.getInt("id"));
                reporte.setClienteId(rs.getInt("cliente_id"));
                reporte.setNombreCliente(rs.getString("nombre_cliente"));
                reporte.setCedulaCliente(rs.getString("cedula_cliente"));
                reporte.setTipoReporte(rs.getString("tipo_reporte"));
                reporte.setFechaGeneracion(rs.getTimestamp("fecha_generacion"));
                reporte.setRutaXML(rs.getString("ruta_xml"));
                reporte.setTotalActivos(rs.getBigDecimal("total_activos"));
                reporte.setPatrimonioNeto(rs.getBigDecimal("patrimonio_neto"));
                reporte.setNumeroCuentas(0); // Valor por defecto ya que no está en BD
                reporte.setEstado("GENERADO"); // Valor por defecto ya que no está en BD
                
                reportes.add(reporte);
            }
            
        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
        
        return reportes;
    }
    
}