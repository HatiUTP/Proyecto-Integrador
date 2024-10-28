package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.EmpleadosModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadosRepo extends JpaRepository<EmpleadosModel,Integer> {
}
