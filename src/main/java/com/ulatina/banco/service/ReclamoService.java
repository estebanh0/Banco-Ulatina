/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.Reclamo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author esteban
 * @since 15/9/25
 * 
 */
public class ReclamoService extends Service{
    
    public boolean crearReclamo(int clienteId, String correoElectronico, String descripcion) 
            throws SQLException, ClassNotFoundException {
        
        PreparedStatement ps = null;
        
        try {
            conectarBD();
            
            String sql = "INSERT INTO reclamo (cliente_id, correo_electronico, descripcion, fecha_creacion, estado) "
                       + "VALUES (?, ?, ?, ?, ?)";
            
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, clienteId);
            ps.setString(2, correoElectronico);
            ps.setString(3, descripcion);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setString(5, "PENDIENTE");
            
            int resultado = ps.executeUpdate();
            
            return resultado > 0;
            
        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }
    
    public List<Reclamo> obtenerReclamosPorClienteId(int clienteId) 
            throws SQLException, ClassNotFoundException {
        
        List<Reclamo> reclamos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conectarBD();
            
            String sql = "SELECT id, cliente_id, correo_electronico, descripcion, fecha_creacion, estado "
                       + "FROM reclamo WHERE cliente_id = ? ORDER BY fecha_creacion DESC";
            
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Reclamo reclamo = new Reclamo();
                reclamo.setId(rs.getInt("id"));
                reclamo.setClienteId(rs.getInt("cliente_id"));
                reclamo.setCorreoElectronico(rs.getString("correo_electronico"));
                reclamo.setDescripcion(rs.getString("descripcion"));
                reclamo.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                reclamo.setEstado(rs.getString("estado"));
                
                reclamos.add(reclamo);
            }
            
        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
        
        return reclamos;
    }
    
}
