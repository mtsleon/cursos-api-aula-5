package br.unicamp.educorp.microservices.cursos.api.aula5.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.unicamp.educorp.microservices.cursos.api.aula5.model.Curso;
import br.unicamp.educorp.microservices.cursos.api.aula5.repository.CursosRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class CursosController {

	private static final Logger log = LoggerFactory.getLogger(CursosController.class);

	@Autowired
	private CursosRepository repository;

	@Autowired
	public Environment environment;

	private String getHostPort() {
		return environment.getProperty("local.server.port");
	}

	@ApiOperation(value = "Listar cursos", notes = "Listar todos os cursos oferecidos pela Educorp", nickname = "Listar cursos")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Listando todos os cursos", response = Curso.class, responseContainer = "List") })
	@GetMapping("/v6/cursos")
	public ResponseEntity<List<Curso>> getAllCursos() {
		log.info("buscando todos os cursos na porta {}", getHostPort());
		return new ResponseEntity<List<Curso>>(repository.findAll(), HttpStatus.OK);
	}

	@ApiOperation(value = "Buscar curso por id", notes = "Buscar o curso oferecido pela Educorp, por id", nickname = "Buscar curso")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Curso encontrado", response = Curso.class),
			@ApiResponse(code = 404, message = "Nenhum curso encontrado") })
	@GetMapping("/v6/cursos/{id}")
	public ResponseEntity<Curso> getCurso(@PathVariable Integer id) {
		log.info("buscando o curso {} na porta {}", id, getHostPort());
		return new ResponseEntity<Curso>(repository.findById(id).get(), HttpStatus.OK);
	}

	@ApiOperation(value = "Salvar novo curso", notes = "Salvar um novo curso na Educorp", nickname = "Salvar novo curso")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Novo curso salvo", response = Curso.class),
			@ApiResponse(code = 500, message = "Erro ao salvar novo curso") })
	@PostMapping("/v6/cursos")
	public ResponseEntity<Curso> saveCurso(@RequestBody Curso curso) {
		log.info("salvando novo curso {} na porta {}", curso, getHostPort());
		try {
			return new ResponseEntity<Curso>(repository.save(curso), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Curso>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@ApiOperation(value = "Excluir curso", notes = "Excluir um curso oferecido na Educorp, por id", nickname = "Excluir curso")
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Curso excluído"),
			@ApiResponse(code = 500, message = "Erro ao excluir curso") })
	@DeleteMapping("/v6/cursos/{id}")
	public ResponseEntity<Curso> deleteCurso(@PathVariable Integer id) {
		log.info("excluindo curso {} na porta {}", id, getHostPort());
		try {
			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return new ResponseEntity<Curso>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "Alterar curso", notes = "Alterar os dados de um curso, por id", nickname = "Alterar curso")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Curso alterado", response = Curso.class),
			@ApiResponse(code = 404, message = "Curso não encontrado") })
	@PutMapping("/v6/cursos/{id}")
	public ResponseEntity<Curso> saveCurso(@PathVariable Integer id, @RequestBody Curso curso) {
		log.info("atualizando curso {} na porta {}", id, getHostPort());

		Optional<Curso> cursoEntity = repository.findById(id);

		if (cursoEntity.isPresent()) {
			Curso _curso = cursoEntity.get();
			_curso.setCodigo(curso.getCodigo());
			_curso.setDescricao(curso.getDescricao());
			return new ResponseEntity<Curso>(repository.save(curso), HttpStatus.OK);
		} else {
			return new ResponseEntity<Curso>(HttpStatus.NOT_FOUND);
		}
	}

}
