package com.tauabrandao.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.enums.StatusLancamento;
import com.tauabrandao.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = " select sum(l.valor) from Lancamento l join l.usuario u"
			+ " where u.id = :pIdUsuario and l.tipo = :pTipo and l.status = :status group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(@Param("pIdUsuario") Long idUsuario,
			@Param("pTipo") TipoLancamento tipo, @Param("status") StatusLancamento status);

}
