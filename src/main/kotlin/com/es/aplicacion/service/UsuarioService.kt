package com.es.aplicacion.service

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.BadRequestException
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Usuario
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun loadUserByUsername(username: String?): UserDetails {
        val usuario: Usuario = usuarioRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username no existente")
            }

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO) : UsuarioDTO? {
        val usuarioExist = usuarioInsertadoDTO.let { usuarioRepository.findByUsername(it.username) }

        val usuario = usuarioInsertadoDTO.rol?.let {
            Usuario(
                null,
                usuarioInsertadoDTO.username,
                passwordEncoder.encode(usuarioInsertadoDTO.password),
                usuarioInsertadoDTO.email,
                it,
                usuarioInsertadoDTO.direccion
            )
        }

        if (usuarioInsertadoDTO.password != usuarioInsertadoDTO.passwordRepeat) {
            throw BadRequestException("La contraseña no coincide")
        }

        if (usuarioExist.isPresent){
            throw BadRequestException("Usuario ${usuarioInsertadoDTO.username} ya existe")
        } else {
            if (usuario != null) {
                usuarioRepository.save(usuario)
                return UsuarioDTO(usuario.username, usuario.email, usuario.roles)
            }
        }
        return null
    }
}