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
import ru.marilka.swotbackend.model.request.UserRequest;
import ru.marilka.swotbackend.repository.AppUserRepository;
import ru.marilka.swotbackend.service.EmailService;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
 @RequiredArgsConstructor
public class AdminController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    @PostMapping("/users")
    public ResponseEntity<AppUser> createUser(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String rawPassword = (String) body.get("password");
        String role = (String) body.get("role");

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        user.setReg(false);

        return ResponseEntity.ok(userRepo.save(user));
    }

    @GetMapping("/users")
    /**
     * Получение всех пользователей (сразу забираем данные до перехода на вкладку).
     */
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        var principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        var appUser = userRepo.findByUsername(username).orElseThrow();

        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Пользователь с таким логином уже существует");
        }
        String generatedPassword = passwordGenerator();
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(generatedPassword));
        user.setRole(request.getRole());
        user.setFullName(request.getName());
        user.setCompanyId(appUser.getCompanyId());

        userRepo.save(user);
        // Отправка пароля на email
        emailService.sendPasswordToUser(user.getUsername(), generatedPassword);
        return ResponseEntity.ok("Пользователь создан");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepo.deleteById(id);
        return ResponseEntity.ok().body("Пользователь удалён");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        AppUser user = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

         user.setRole(request.getRole());

        userRepo.save(user);
        return ResponseEntity.ok("Пользователь обновлён");
    }

    private String passwordGenerator() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
