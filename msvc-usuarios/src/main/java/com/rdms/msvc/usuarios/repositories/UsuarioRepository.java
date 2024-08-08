package com.rdms.msvc.usuarios.repositories;

import com.rdms.msvc.usuarios.models.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
}
