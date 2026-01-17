package pl.edu.pjwstk.s34101Bank;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

  @Mock
  private UserStorage userStorage;

  @Mock
  private TransferStorage transferStorage;

  @InjectMocks
  private BankService bankService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("1", "Gerald Serafin", new BigDecimal("100.00"));
  }

  @Test
  void shouldRegisterUser() {
    when(userStorage.insert(anyString(), any(BigDecimal.class)))
        .thenReturn(Optional.of(user));

    var result = bankService.registerUser("Gerald", new BigDecimal("100.00"));

    assertTrue(result.isPresent());
    assertEquals(user, result.get());
  }

  @Test
  void shouldTransferFundsSuccessfully() {
    var amount = new BigDecimal("50.00");

    when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
    when(userStorage.update(eq(user.getId()), any(Consumer.class)))
        .thenAnswer(invocation -> {
          Consumer<User> consumer = invocation.getArgument(1);
          consumer.accept(user);
          return Optional.of(user);
        });

    var result = bankService.transfer(user, amount);

    assertEquals(TransferStatus.ACCEPTED, result.transfer().getStatus());
    assertTrue(result.balanceAfterTransfer().isPresent());
    assertEquals(0, result.balanceAfterTransfer().get().compareTo(
        new BigDecimal("50.00")));
  }

  @Test
  void shouldDeclineTransferWhenUserNotFound() {
    when(userStorage.findById(user.getId())).thenReturn(Optional.empty());

    var result = bankService.transfer(user, new BigDecimal("50.00"));

    assertEquals(TransferStatus.DECLINED, result.transfer().getStatus());
    assertTrue(result.message().isPresent());
    assertEquals("User not found", result.message().get());
  }

  @Test
  void shouldDeclineTransferWhenInsufficientFunds() {
    var amount = new BigDecimal("150.00");

    when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));

    var result = bankService.transfer(user, amount);

    assertEquals(TransferStatus.DECLINED, result.transfer().getStatus());
    assertTrue(result.message().isPresent());
    assertEquals("Insufficient funds", result.message().get());
  }

  @Test
  void shouldIncreaseBalanceSuccessfully() {
    var amount = new BigDecimal("50.00");

    when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
    when(userStorage.update(eq(user.getId()), any(Consumer.class)))
        .thenAnswer(invocation -> {
          Consumer<User> consumer = invocation.getArgument(1);
          consumer.accept(user);
          return Optional.of(user);
        });

    var result = bankService.increaseBalance(user, amount);

    assertEquals(TransferStatus.ACCEPTED, result.transfer().getStatus());
    assertTrue(result.balanceAfterTransfer().isPresent());
    assertEquals(0, result.balanceAfterTransfer().get().compareTo(
        new BigDecimal("150.00")));
  }

  @Test
  void shouldGetUserInfo() {
    when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
    when(transferStorage.findAll(any(Predicate.class))).thenReturn(List.of());

    var result = bankService.getUserInfo(user.getId());

    assertTrue(result.isPresent());
    assertEquals(user, result.get().user());
    assertTrue(result.get().transfers().isEmpty());
  }

  @Test
  void shouldReturnEmptyUserInfoWhenUserNotFound() {
    when(userStorage.findById(anyString())).thenReturn(Optional.empty());

    var result = bankService.getUserInfo("unknown");

    assertTrue(result.isEmpty());
  }
}
