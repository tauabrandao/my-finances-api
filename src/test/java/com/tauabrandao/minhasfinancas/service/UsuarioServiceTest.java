package com.tauabrandao.minhasfinancas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Usuario;
import com.tauabrandao.minhasfinancas.model.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@Autowired
	UsuarioService service;

	@Autowired
	UsuarioRepository repository;

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			repository.deleteAll();
			service.validarEmail("usuario@email.com");
		});
	}

	@Test
	public void deveLancarExcessaoAoValidarEmailQuandoExistirUsuarioComOEmailInformado() {
		String email = "email@email.com";
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			Usuario usuario = Usuario.builder().nome("nome do usuario").email(email).build();
			repository.save(usuario);
			service.validarEmail(email);
		});
	}

}
