package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.DetalleCompraModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCompraRepo extends JpaRepository<DetalleCompraModel,Integer> {
}
