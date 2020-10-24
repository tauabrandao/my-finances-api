package com.tauabrandao.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	Lancamento atualizar(Lancamento lancamento);
	void deletar(Lancamento lancamento);
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	void atualzarStatus(Lancamento lancamento, StatusLancamento status);
	void validarLancamento(Lancamento lancamento);
	Optional<Lancamento> getById(Long id);
	BigDecimal obterSaldoPorUsuario(Long id);

}
