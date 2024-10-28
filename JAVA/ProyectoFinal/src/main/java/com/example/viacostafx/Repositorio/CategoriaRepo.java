package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepo extends JpaRepository<CategoriaModel,Integer> {
}
