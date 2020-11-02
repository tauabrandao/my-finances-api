package com.tauabrandao.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.entity.Usuario;
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

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		service.deletar(lancamento);
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueNaoExista() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		service.deletar(lancamento);
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), RegraNegocioException.class);
	}

	@Test
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		List<Lancamento> resultado = service.buscar(lancamento);

		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatusLancamento = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		service.atualizarStatus(lancamento, novoStatusLancamento);

		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatusLancamento);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		Mockito.when(repository.findById(1l)).thenReturn(Optional.of(lancamento));

		Optional<Lancamento> lancamentoRetornado = service.getById(1l);

		Assertions.assertThat(lancamentoRetornado.isPresent());
	}

	@Test
	public void deveRetornarVazioQuandoBuscaUmLancamentoQueNaoExistePorId() {
		Mockito.when(repository.findById(1l)).thenReturn(Optional.empty());
		Optional<Lancamento> lancamentoRetornado = service.getById(1l);
		Assertions.assertThat(!lancamentoRetornado.isPresent());
	}
	
	@Test
	public void deveLancarErroAoValidarLancamentoComAtributosInvalidos() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida");
		
		lancamento.setDescricao("salário");
		
		erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mes válido");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário válido");
		
		lancamento.setUsuario(Usuario.builder().id(1l).build());
		
		erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido");
		
		lancamento.setValor(BigDecimal.valueOf(10));
		
		erro = Assertions.catchThrowable(() -> service.validarLancamento(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo válido");
		

		
	}
}
