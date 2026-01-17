package pl.edu.pjwstk.s34101Bank;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserStorageTest {
  private UserStorage userStorage;

  @BeforeEach
  void setUp() {
    userStorage = new UserStorage();
  }

  @Test
  void shouldInsertUserSuccessfully() {
    var fullName = "Gerald Serafin";
    var balance = new BigDecimal("100.00");

    var result = userStorage.insert(fullName, balance);

    assertTrue(result.isPresent());
    assertEquals(fullName, result.get().getFullName());
    assertEquals(balance.setScale(2), result.get().getBalance());
    assertNotNull(result.get().getId());
  }

  @Test
  void shouldFindUserById() {
    var savedUser = userStorage.insert("Gerald Serafin", BigDecimal.TEN).get();
    var id = savedUser.getId();

    var foundUser = userStorage.findById(id);

    assertTrue(foundUser.isPresent());
    assertEquals(savedUser, foundUser.get());
  }

  @Test
  void shouldReturnEmptyWhenUserNotFoundById() {
    var result = userStorage.findById("non-existent-id");
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldUpdateUser() {
    var savedUser = userStorage.insert("Gerald Serafin", new BigDecimal("50.00")).get();
    var id = savedUser.getId();

    var updatedResult = userStorage.update(id, user -> user.setFullName("Krzychu Serafin"));

    assertTrue(updatedResult.isPresent());
    assertEquals("Krzychu Serafin", updatedResult.get().getFullName());

    var foundUser = userStorage.findById(id);
    assertTrue(foundUser.isPresent());
    assertEquals("Krzychu Serafin", foundUser.get().getFullName());
  }

  @Test
  void shouldNotUpdateNonExistentUser() {
    var result = userStorage.update("eloelo320", user -> user.setFullName("ITN"));
    assertTrue(result.isEmpty());
  }
}
