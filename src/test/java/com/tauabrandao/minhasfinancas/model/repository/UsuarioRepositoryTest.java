package com.tauabrandao.minhasfinancas.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.tauabrandao.minhasfinancas.model.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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
		boolean usuarioExiste = repository.existsByEmail(email);
		
		// verificação
		Assertions.assertThat(usuarioExiste).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmailInformado() {
		
		String email = "usuario@usuario.com.br";
		
		Usuario usuario = Usuario.builder().nome("nome do usuario").email(email).build();
		repository.save(usuario);
		
		boolean usuarioExiste = repository.existsByEmail("qualquer@usuario.com");
		
		Assertions.assertThat(usuarioExiste).isFalse();
		
	}

}
