package com.example.viacostafx.dao;
import com.example.viacostafx.Modelo.AsientoModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AsientoDao {
    private EntityManagerFactory emf;

    public AsientoDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    public void crearAsiento(AsientoModel asiento) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(asiento);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public AsientoModel obtenerAsientoPorId(int asientoId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AsientoModel.class, asientoId);
        } finally {
            em.close();
        }
    }

    public List<AsientoModel> obtenerTodosLosAsientos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<AsientoModel> query = em.createQuery("SELECT a FROM AsientoModel a", AsientoModel.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<AsientoModel> obtenerAsientosPorBus(int busId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<AsientoModel> query = em.createQuery("SELECT a FROM AsientoModel a WHERE a.bus.id = :busId", AsientoModel.class);
            query.setParameter("busId", busId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean actualizarAsiento(AsientoModel asientoActualizado) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AsientoModel asiento = em.find(AsientoModel.class, asientoActualizado.getId());
            if (asiento != null) {
                asiento.setNumero(asientoActualizado.getNumero());
                asiento.setEstado(asientoActualizado.getEstado());
                asiento.setBus(asientoActualizado.getBus());
                asiento.setBoletos(asientoActualizado.getBoletos());
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean eliminarAsiento(int asientoId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AsientoModel asiento = em.find(AsientoModel.class, asientoId);
            if (asiento != null) {
                em.remove(asiento);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
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
