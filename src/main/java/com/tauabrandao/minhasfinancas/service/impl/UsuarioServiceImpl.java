package com.tauabrandao.minhasfinancas.service.impl;

import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Usuario;
import com.tauabrandao.minhasfinancas.model.repository.UsuarioRepository;
import com.tauabrandao.minhasfinancas.service.UsuarioService;

public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validarEmail(String email) {
		boolean usuarioExiste = repository.existsByEmail(email);
		if(usuarioExiste) {
			throw new RegraNegocioException("Já existe um usuário cadasdtrado com este email.");
		}

	}

}
