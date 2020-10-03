package com.tauabrandao.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tauabrandao.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}