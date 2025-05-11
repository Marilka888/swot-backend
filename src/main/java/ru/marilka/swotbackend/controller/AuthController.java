package ru.marilka.swotbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.model.entity.CompanyEntity;
import ru.marilka.swotbackend.model.request.ChangePasswordRequest;
import ru.marilka.swotbackend.model.request.UserRequest;
import ru.marilka.swotbackend.model.response.UserResponse;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
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
        user.setRole("USER");
        userRepo.save(user);

        return Map.of("token", jwt.generateToken(username));
    }

    @PostMapping("/login")
    /**
     * Аутентификация.
     */
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Optional<AppUser> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        AppUser user = userOptional.get();

        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid password"));
        }

        String token = jwt.generateToken(user.getUsername());
        user.setToken(token);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole(),
                "firstLogin", !user.isReg()
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepo.findByUsername(principal.getUsername()).orElseThrow();

        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setReg(true); // снимаем флаг
        userRepo.save(user);

        return ResponseEntity.ok("");
    }


    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile() {
        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var username = principal.getUsername();
        var user = userRepo.findByUsername(username).orElseThrow();
        var company = companyRepository.findById(user.getCompanyId()).orElseThrow();
        var response = UserResponse.builder()
                .id(user.getId())
                .company(company.getName())
                .fullName(username)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Object> getRefresh() {
        return ResponseEntity.ok("{\n" +
                "  \"token\": \"access-token\",\n" +
                "  \"refreshToken\": \"refresh-token\"\n" +
                "}\n");
    }
}

