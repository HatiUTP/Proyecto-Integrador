package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.BusModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class BusDao {
    private EntityManagerFactory emf;

    public BusDao() {
        emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    public BusModel obtenerBusPorId(int busId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(BusModel.class, busId);
        } finally {
            em.close();
        }
    }

    // Cierra el EntityManagerFactory
    public void cerrar() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
