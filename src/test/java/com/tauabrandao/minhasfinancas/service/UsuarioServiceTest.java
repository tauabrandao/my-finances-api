package com.tauabrandao.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.exception.ErroAutenticacao;
import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Usuario;
import com.tauabrandao.minhasfinancas.model.repository.UsuarioRepository;
import com.tauabrandao.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());

		Usuario usuario = Usuario.builder().id(1l).nome("nome").email("email@email.com").senha("senha").build();

		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		Assertions.assertDoesNotThrow(() -> {
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo).isNotNull();
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		});

	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.salvarUsuario(usuario);			
		});

		Mockito.verify(repository, Mockito.never()).save(usuario);

	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		String email = "email@email.com";
		String senha = "123456";

		Usuario usuario = Usuario.builder().nome(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		Assertions.assertDoesNotThrow(() -> {
			Usuario result = service.autenticar(email, senha);
			org.assertj.core.api.Assertions.assertThat(result).isNotNull();
		});
	}

	@Test
	public void deveLancarExcessaoQuandoTentaAutenticarUmUsuarioComEmailInvalido() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> {
			service.autenticar("email@email.com", "senha");
		});

		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
				.hasMessage("Usuário não encontrado.");
	}

	@Test
	public void deveLancarExcessaoQuandoTentaAutenticarUmUsuarioComSenhaInvalida() {
		String senha = "123456";
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> {
			service.autenticar(email, "123");
		});

		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
				.hasMessage("Senha inválida");

	}

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
			service.validarEmail("usuario@email.com");
		});
	}

	@Test
	public void deveLancarExcessaoAoValidarEmailQuandoExistirUsuarioComOEmailInformado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
			service.validarEmail("email@email.com");
		});
	}

}
