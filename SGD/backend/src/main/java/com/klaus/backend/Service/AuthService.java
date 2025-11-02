package com.klaus.backend.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.klaus.backend.DTO.Mapper.AuthMapper;
import com.klaus.backend.DTO.request.AuthRequestDTO;
import com.klaus.backend.DTO.response.AuthResponseDTO;
import com.klaus.backend.Exception.LoginException;
import com.klaus.backend.Exception.ResourceAlreadyExistsException;
import com.klaus.backend.Model.User;
import com.klaus.backend.Repository.UserRepository;
import com.klaus.backend.Security.CookieUtil;
import com.klaus.backend.Security.JwtUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper mapper;

    public AuthResponseDTO login(AuthRequestDTO req, HttpServletResponse res) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.username(),
                            req.password()));

            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            String token = jwtUtil.generateToken(username);

            cookieUtil.createCookie(token, res);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return mapper.toDTO(user);

        } catch (AuthenticationException e) {
            throw new LoginException("Credenciais inválidas. Verifique seu usuário e senha.", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro interno durante a autenticação.", e);
        }
    }

    public void logout(HttpServletResponse res) {
        SecurityContextHolder.clearContext();
        cookieUtil.invalidateSession(res);
    }

    @Transactional
    public AuthResponseDTO register(AuthRequestDTO req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ResourceAlreadyExistsException("O nome de usuário já está em uso");
        }

        User user = mapper.toEntity(req);
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));

        return mapper.toDTO(userRepository.save(user));
    }

    public boolean getMe(HttpServletRequest request) {
        try {
            String token = cookieUtil.extractTokenFromCookie(request);
            if (token == null || token.isBlank())
                return false;

            String username = jwtUtil.getUserFromToken(token);
            return userRepository.existsByUsername(username);

        } catch (JwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
