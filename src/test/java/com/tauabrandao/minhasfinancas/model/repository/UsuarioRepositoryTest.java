package com.tauabrandao.minhasfinancas.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.model.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {

		String email = "usuario@email.com";

		// cenario
		Usuario usuario = Usuario.builder().nome("nome do usuario").email(email).build();
		repository.save(usuario);
		// Ação / Execução
		boolean result = repository.existsByEmail(email);

		// verificação

		Assertions.assertThat(result).isTrue();

	}

}
