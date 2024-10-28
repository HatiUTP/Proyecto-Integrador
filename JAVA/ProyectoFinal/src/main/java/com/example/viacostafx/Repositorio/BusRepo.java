package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.BusModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepo extends JpaRepository<BusModel,Integer> {
}
