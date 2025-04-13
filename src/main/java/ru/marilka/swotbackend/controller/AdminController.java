package ru.marilka.swotbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.marilka.swotbackend.model.entity.AppUser;
import ru.marilka.swotbackend.repository.AppUserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;

    public AdminController(AppUserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/users")
    public ResponseEntity<AppUser> createUser(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String rawPassword = (String) body.get("password");
        List<String> rolesList = (List<String>) body.get("roles");

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRoles(new HashSet<>(rolesList));

        return ResponseEntity.ok(userRepo.save(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
