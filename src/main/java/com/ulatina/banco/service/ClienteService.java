/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author esteban
 */
public class ClienteService extends Service {
    
    
    // Método para buscar el cliente por medio de su cedula y nombre, control de excepciones SQL
    public Cliente buscarClientePorCedulaYNombre(String cedula, String nombre)
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente cliente = null;

        try {
            
            conectarBD();
            String sql = "SELECT id, cedula, nombre, embargo FROM cliente WHERE cedula=? AND nombre=?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(cedula));
            ps.setString(2, nombre);
            rs = ps.executeQuery();

            if (rs.next()) {
                cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("cedula"), // Convertimos int de BD a String
                        rs.getString("nombre"),
                        rs.getBoolean("embargo")
                );
            }

        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }

        return cliente;
    }
    
    public Cliente buscarClientePorId(int clienteId) 
            throws SQLException, ClassNotFoundException {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente cliente = null;
        
        try {
            conectarBD();
            String sql = "SELECT id, cedula, nombre, embargo FROM cliente WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("cedula"),
                    rs.getString("nombre"),
                    rs.getBoolean("embargo")
                );
            }
            
        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
        
        return cliente;
    }
    
    public List<Cliente> obtenerTodosLosClientes() 
            throws SQLException, ClassNotFoundException {
        
        List<Cliente> clientes = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conectarBD();
            String sql = "SELECT id, cedula, nombre, embargo FROM cliente ORDER BY nombre";
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("id"),
                    rs.getString("cedula"),
                    rs.getString("nombre"),
                    rs.getBoolean("embargo")
                );
                clientes.add(cliente);
            }
            
        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
        
        return clientes;
    }
    
}
