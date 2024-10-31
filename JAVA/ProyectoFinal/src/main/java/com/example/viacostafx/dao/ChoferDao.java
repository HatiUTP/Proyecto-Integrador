package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.CategoriaModel;
import com.example.viacostafx.Modelo.ChoferModel;
import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ChoferDao {
    private EntityManagerFactory emf;

    public ChoferDao() {
        this.emf = Persistence.createEntityManagerFactory("viacostaFX");
    }

    public List<ChoferModel> obtenerTodosLosChoferes() {
        EntityManager em = emf.createEntityManager();
        List<ChoferModel> choferesActivos = null;

        try {
            // Consulta para traer solo los choferes con isActive = true
            choferesActivos = em.createQuery("SELECT c FROM ChoferModel c WHERE c.isActive = true", ChoferModel.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return choferesActivos;
    }


    // Metodo para agregar un nuevo chofer
    public void agregarChofer(ChoferModel chofer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(chofer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    //Metodo para editar un chofer
    public void editarChofer(ChoferModel chofer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(chofer); // Actualiza el chofer en la base de datos
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    //Metodo pra eliminar un chofer
    public void eliminarChofer(int choferId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ChoferModel chofer = em.find(ChoferModel.class, choferId); // Busca el chofer por su ID
            if (chofer != null) {
                em.remove(chofer); // Elimina el chofer de la base de datos
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void desactivarChofer(int idChofer) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Encontrar el chofer por su ID
            ChoferModel chofer = em.find(ChoferModel.class, idChofer);

            if (chofer != null) {
                // Cambiar isActive a false
                chofer.setIsActive(false);
                em.merge(chofer);  // Actualizar el chofer en la base de datos
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<ChoferModel> buscarChoferPorNombre(String nombre) {
        EntityManager em = emf.createEntityManager();
        List<ChoferModel> choferesFiltrados = null;

        try {
            // Consulta con LIKE para encontrar choferes por nombre
            choferesFiltrados = em.createQuery("SELECT c FROM ChoferModel c WHERE c.isActive = true AND c.nombre LIKE :nombre", ChoferModel.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return choferesFiltrados;
    }



    // Cerrar el EntityManagerFactory al final de la aplicación
    public void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
