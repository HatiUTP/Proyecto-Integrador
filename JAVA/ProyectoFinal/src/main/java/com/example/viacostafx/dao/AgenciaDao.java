package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.AgenciaModel;
import com.example.viacostafx.Modelo.JPAUtils;
import com.example.viacostafx.Modelo.UbigeoModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgenciaDao {

    // Obtener provincias
    public static List<String> obtenerProvincias() {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<String> provincias = null;

        try {
            String jpql = "SELECT DISTINCT u.provincia FROM UbigeoModel u ORDER BY u.provincia";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            provincias = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return provincias;
    }

    //Obtener distritos en base a las agencias
    public static List<String> obtenerDistritosConAgencias() {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<String> distritos = null;

        try {
            String jpql = "SELECT DISTINCT a.ubigeo.distrito FROM AgenciaModel a WHERE a.isActive = true";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            distritos = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return distritos;
    }
}