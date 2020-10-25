package com.tauabrandao.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.enums.StatusLancamento;
import com.tauabrandao.minhasfinancas.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager em;

	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();

		Lancamento lancamentoSalvo = repository.save(lancamento);

		Assertions.assertThat(lancamentoSalvo.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		lancamento = em.find(Lancamento.class, lancamento.getId());
		repository.delete(lancamento);

		Lancamento lancamentoInexistente = em.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		Optional<Lancamento> lancamentoSalvo = repository.findById(lancamento.getId());
		Assertions.assertThat(lancamentoSalvo.get().getValor()).isEqualTo(BigDecimal.valueOf(10));

		lancamento.setValor(BigDecimal.valueOf(20));
		Lancamento lancamentoAtualizado = repository.save(lancamento);
		Assertions.assertThat(lancamentoAtualizado.getValor()).isEqualTo(BigDecimal.valueOf(20));

	}

	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		em.persist(lancamento);
		return lancamento;
	}

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		Optional<Lancamento> lancamentoRetornado = repository.findById(lancamento.getId());
		Assertions.assertThat(lancamentoRetornado.isPresent());
		Assertions.assertThat(lancamentoRetornado.get().getDescricao()).isEqualTo("Lançamento qualquer");

	}

	public static Lancamento criarLancamento() {
		Lancamento lancamento = Lancamento.builder().ano(2020).mes(1).descricao("Lançamento qualquer")
				.valor(BigDecimal.valueOf(10)).tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now()).build();
		return lancamento;
	}

}
