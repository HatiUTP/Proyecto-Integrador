package com.example.viacostafx.dao;

import com.example.viacostafx.Modelo.JPAUtils;
import com.example.viacostafx.Repositorio.AgenciaRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenciaDao  {
    private final AgenciaRepo agenciaRepo;

    @Autowired
    public AgenciaDao(AgenciaRepo agenciaRepo) {
        this.agenciaRepo = agenciaRepo;
    }

    public static List<String> obtenerDistritosConAgencias() {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        List<String> distritos = null;

        try {
            // Consulta JPQL para obtener distritos únicos de agencias activas
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
