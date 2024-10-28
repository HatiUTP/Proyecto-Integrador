package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.EmpleadosModel;
import jakarta.persistence.*;

public class EmpleadosDao {
    private EntityManagerFactory emf;

    public EmpleadosDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    // Obtener empleado por nombre de usuario
    public EmpleadosModel obtenerEmpleadoPorUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<EmpleadosModel> query = em.createQuery("SELECT e FROM EmpleadosModel e WHERE e.usuario = :username", EmpleadosModel.class);
            query.setParameter("username", username);
            return query.getSingleResult(); // Retorna un único resultado
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna null si hay otro error
        } finally {
            em.close();
        }
    }

    // Cerrar el EntityManagerFactory
    public void cerrar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
