package us.navonod.us.users;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // Endpoint #1: GET /users
    @GetMapping("")
    public Iterable<User> getAll() {
        return this.repository.findAll();
    }

    // Endpoint #2: POST /users
    @PostMapping("")
    public User create(@RequestBody User user) {
        return this.repository.save(user);
    }

    // Endpoint #3: GET /users/{id}
    @GetMapping("/{id}")
    public User read(@PathVariable(value="id") Long id) {
        return this.repository.findById(id).orElse(new User());
    }

    // Endpoint #4: PATCH /users/{id}
    @PatchMapping("/{id}")
    public User update(
            @PathVariable(value="id") Long id,
            @RequestBody User newUser
    ) {
        Optional<User> oldUser = this.repository.findById(id);
        if (newUser.getEmail() != null) {
            oldUser.get().setEmail(newUser.getEmail());
        }
        if (newUser.getPassword() != null) {
            oldUser.get().setPassword(newUser.getPassword());
        }
        return this.repository.save(oldUser.get());
    }

    // Endpoint #5: DELETE /users/{id}
    @DeleteMapping("/{id}")
    public Count delete(@PathVariable(value="id") Long id) {
        Optional<User> user = this.repository.findById(id);
        if (user.isPresent()) {
            this.repository.delete(user.get());
        }
        Count count = new Count();
        List<User> users = (List<User>) getAll();

        count.setCount(users.size());
        return count;
    }

    // Endpoint #6: POST /users/authenticate
    @PostMapping("/authenticate")
    public Authenticated authenticate(@RequestBody User user) {
        Optional<User> auth = Optional.ofNullable(this.repository.findByEmail(user.getEmail()));
        Authenticated authUser = new Authenticated();
        if (auth.isPresent() && auth.get().getPassword().equals(user.getPassword())) {
            authUser.setAuthenticated(true);
            authUser.setUser(auth.get());
        } else {
            authUser.setAuthenticated(false);
        }
        return authUser;
    }
}
