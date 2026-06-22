package salon_queue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import salon_queue.config.JwtUtil;
import salon_queue.model.User;
import salon_queue.repository.SalonRepository;
import salon_queue.repository.UserRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, SalonRepository salonRepository,
                          BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.salonRepository = salonRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        String username = request.username().trim();
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Username already exists"));
        }

        User.Role role = parseRole(request.role());
        if (role == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "role must be CUSTOMER or OWNER"));
        }

        Long salonId = role == User.Role.OWNER ? request.salonId() : null;
        if (role == User.Role.OWNER && salonId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "salonId is required for OWNER users"));
        }
        if (salonId != null && !salonRepository.existsById(salonId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Salon not found"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setSalonId(salonId);

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", userDetails(savedUser)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        String username = request.username().trim();

        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(user -> {
                    String role = user.getRole().name();
                    String token = jwtUtil.generateToken(user.getUsername(), role);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("token", token);
                    response.put("role", role);
                    response.put("username", user.getUsername());
                    response.put("name", user.getUsername());
                    response.put("salonId", user.getSalonId());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid username or password")));
    }

    private Map<String, Object> userDetails(User user) {
        Map<String, Object> details = new HashMap<>();
        details.put("id", user.getId());
        details.put("username", user.getUsername());
        details.put("role", user.getRole().name());
        details.put("salonId", user.getSalonId());
        return details;
    }

    private User.Role parseRole(String role) {
        try {
            return User.Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public record RegisterRequest(String username, String password, String role, Long salonId) {
    }

    public record LoginRequest(String username, String password) {
    }
}
