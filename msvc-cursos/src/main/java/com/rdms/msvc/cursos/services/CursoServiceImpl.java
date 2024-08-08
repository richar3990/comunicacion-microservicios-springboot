package com.rdms.msvc.cursos.services;

import com.rdms.msvc.cursos.clients.UsuarioClienteRest;
import com.rdms.msvc.cursos.models.Usuario;
import com.rdms.msvc.cursos.models.entity.Curso;
import com.rdms.msvc.cursos.models.entity.CursoUsuario;
import com.rdms.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements  CursoService{
    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClienteRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
    repository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);
        if (c.isPresent()) {
            Usuario usuarioMsvc= client.deltalle(usuario.getId());
            Curso curso = c.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);
        if (c.isPresent()) {
            Usuario usuarioNuevoMsvc= client.crear(usuario);
            Curso curso = c.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevoMsvc.getId());
            curso.addCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuarioNuevoMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);
        if (c.isPresent()) {
            Usuario usuarioMsvc= client.deltalle(usuario.getId());
            Curso curso = c.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());
            curso.removeCursoUsuario(cursoUsuario);
            repository.save(curso);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> c = repository.findById(id);
        if (c.isPresent()) {
            Curso curso = c.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream().map(CursoUsuario::getUsuarioId).toList();
                List<Usuario> usuariosList = client.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuariosList);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }
}
