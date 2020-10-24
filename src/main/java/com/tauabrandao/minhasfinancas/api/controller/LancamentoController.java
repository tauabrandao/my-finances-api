package com.tauabrandao.minhasfinancas.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tauabrandao.minhasfinancas.api.dto.LancamentoDTO;
import com.tauabrandao.minhasfinancas.exception.RegraNegocioException;
import com.tauabrandao.minhasfinancas.model.entity.Lancamento;
import com.tauabrandao.minhasfinancas.model.entity.Usuario;
import com.tauabrandao.minhasfinancas.model.enums.StatusLancamento;
import com.tauabrandao.minhasfinancas.model.enums.TipoLancamento;
import com.tauabrandao.minhasfinancas.service.LancamentoService;
import com.tauabrandao.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

	@Autowired
	LancamentoService service;

	@Autowired
	UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity<Object> salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade.setStatus(StatusLancamento.PENDENTE);
			entidade = service.salvar(entidade);
			return new ResponseEntity<Object>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.getById(id).map(entity -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok().body(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "usuario") Long idUsuario
			) {
		
		Lancamento lancamentoFiltro = Lancamento.builder()
				.descricao(descricao).mes(mes).ano(ano).build();
		
		Optional<Usuario> usuario = usuarioService.getById(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@DeleteMapping("{id}")
	private ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.getById(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> 
			new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST)
		);
	}

	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setAno(dto.getAno());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setId(dto.getId());
		lancamento.setMes(dto.getMes());
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));			
		}
		if(dto.getStatus() !=null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));			
		}
		
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.getById(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado"));
		lancamento.setUsuario(usuario);

		return lancamento;

	}

}
