package com.rdms.msvc.cursos.controlers;

import com.rdms.msvc.cursos.models.entity.Curso;
import com.rdms.msvc.cursos.services.CursoService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rdms.msvc.cursos.models.Usuario;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        //Optional<Curso> cr = service.porId(id);
        Optional<Curso> cr = service.porIdConUsuarios(id);
        if (cr.isPresent()) {
            return ResponseEntity.ok(cr.get());
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody  Curso curso) {
        Curso cursoDB = service.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody Curso curso, @PathVariable Long id) {
        Optional<Curso> c = service.porId(id);
        if (c.isPresent()) {
            Curso cdb = c.get();
            cdb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cdb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> c = service.porId(id);
        if (c.isPresent()) {
            service.eliminar(c.get().getId());
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> u;
        try {
            u = service.asignarUsuario(usuario, cursoId);
            if (u.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(u.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "existe el usuario por " +
                    "el id o error en  la comunicación: " +e.getMessage()));
        }
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> u;
        try {
            u = service.crearUsuario(usuario, cursoId);
            if (u.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(u.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No se pudo crear usuario " +
                    " o error en la comunicación" +e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> u;
        try {
            u = service.eliminarUsuario(usuario, cursoId);
            if (u.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(u.get());
            }
            return ResponseEntity.notFound().build();
        } catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No se pudo eliminar usuario " +
                    " o error en la comunicación" +e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}
