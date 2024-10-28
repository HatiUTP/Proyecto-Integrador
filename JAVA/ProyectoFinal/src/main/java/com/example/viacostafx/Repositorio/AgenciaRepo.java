package com.example.viacostafx.Repositorio;

import com.example.viacostafx.Modelo.AgenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AgenciaRepo  extends JpaRepository<AgenciaModel, Integer> {

}
