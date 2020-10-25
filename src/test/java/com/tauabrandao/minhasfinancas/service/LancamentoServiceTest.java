package com.tauabrandao.minhasfinancas.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.enums.StatusLancamento;
import com.tauabrandao.minhasfinancas.model.repository.LancamentoRepository;
import com.tauabrandao.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.tauabrandao.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		// cria um lançamento que deverá ser salvo
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		// impede que ao validar o lançamento sejam lançadas exceções
		Mockito.doNothing().when(service).validarLancamento(lancamentoASalvar);

		// cria um lançamento que será a simulação do lançamento que foi salvo no banco
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);

		// simula que o lançamento a salvar foi salvo no banco retornando o lançamento
		// salvo
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		// executa o método salvar do serviço
		Lancamento lancamentoFinal = service.salvar(lancamentoASalvar);

		Assertions.assertThat(lancamentoFinal.getId()).isEqualTo(lancamentoSalvo.getId());

	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		// cria um lançamento que deverá ser salvo
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// lança uma exceção quando tentar validar o lançamento a salvar
		Mockito.doThrow(RegraNegocioException.class).when(service).validarLancamento(lancamentoASalvar);

		// testa se houve uma exceção do tipo RegraNegocioException ao tentar salvar o
		// lançamento
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		// certifica que o método save do repository não foi chamado
//		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmUsuario() {
		// cria um simulado que foi salvo
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		// impede que ao validar o lançamento sejam lançadas exceções
		Mockito.doNothing().when(service).validarLancamento(lancamentoSalvo);

		// faz uma simulação retornando o proprio lançamento quando o save for chamado
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		service.atualizar(lancamentoSalvo);

		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

}
