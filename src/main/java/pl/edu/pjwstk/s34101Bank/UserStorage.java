package pl.edu.pjwstk.s34101Bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UserStorage {
  private List<User> users = new ArrayList<>();

  public Optional<User> insert(String fullName, BigDecimal balance) {
    var id = String.format("%s-%d", fullName, users.size());

    if (this.findById(id).isPresent()) {
      return Optional.empty();
    }

    var user = new User(id, fullName, balance);

    users.add(user);

    return Optional.of(user);
  }

  public Optional<User> update(String id, Consumer<User> consumer) {
    var user = users.stream().filter(u -> u.getId() == id).findFirst();

    user.ifPresent(consumer::accept);

    return user;
  }

  public Optional<User> findById(String id) {
    return users.stream().filter(u -> u.getId() == id).findFirst();
  }
}
