package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.util.JwtUtil;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = encoder.encode(request.get("password"));
        encoder.encode(request.get("password"));
        System.out.println(encoder.encode(request.get("1111")));

        if (userRepo.findByUsername(username).isPresent()) {
            return Map.of("error", "User already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(Set.of("USER"));
        userRepo.save(user);

        return Map.of("token", jwt.generateToken(username));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        System.out.println(encoder.encode("1111"));
        String username = request.get("username");
        String password = request.get("password");
        System.out.println();
        Optional<AppUser> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        AppUser user = userOptional.get();

        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
        }

        String token = jwt.generateToken(user.getUsername());
        user.setToken(token);                  // ← сохранить токен
        userRepo.save(user);                  // ← сохранить в базу

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "roles", user.getRoles()
        ));
    }


    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile() {
        return ResponseEntity.ok("{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"companyName\": \"Роги и Ноги\",\n" +
                "  \"roles\": [\"ADMIN\"]\n" +
                "}");
    }

    @GetMapping("/refresh")
    public ResponseEntity<Object> getRefresh() {
        return ResponseEntity.ok("{\n" +
                "  \"token\": \"access-token\",\n" +
                "  \"refreshToken\": \"refresh-token\"\n" +
                "}\n");
    }
}

