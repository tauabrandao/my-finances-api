package com.tauabrandao.minhasfinancas.service;

import java.util.Optional;

import com.tauabrandao.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> getById(Long id);

}
