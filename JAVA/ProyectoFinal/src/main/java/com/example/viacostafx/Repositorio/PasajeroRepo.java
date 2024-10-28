package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.PasajeroModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasajeroRepo extends JpaRepository<PasajeroModel,Integer> {
}
