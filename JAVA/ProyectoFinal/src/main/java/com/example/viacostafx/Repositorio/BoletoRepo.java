package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.BoletoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoRepo extends JpaRepository<BoletoModel,Integer> {
}
