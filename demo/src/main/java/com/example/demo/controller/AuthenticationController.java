package com.example.demo.controller;

import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.RegistroDTO;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UsuarioRepository usuarioRepository,
                                    PasswordEncoder passwordEncoder,
                                    TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?>  registrarUsuario(@RequestBody @Valid RegistroDTO registroDTO) {
        if (this.usuarioRepository.findByLogin(registroDTO.login()) != null){
            return ResponseEntity.badRequest().body("Esse usuário já existe");
        }

        String senhaCriptografada = this.passwordEncoder.encode(registroDTO.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(registroDTO.login());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setEmail(registroDTO.email());

        this.usuarioRepository.save(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var usuario = (Usuario) auth.getPrincipal();
            var token = this.tokenService.gerarToken(usuario);
            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erro: Credenciais inválidas.");
        }
    }
}
