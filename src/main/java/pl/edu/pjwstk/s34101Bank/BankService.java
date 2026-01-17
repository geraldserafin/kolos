package pl.edu.pjwstk.s34101Bank;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
class BankService {
  private final TransferStorage transferStorage;
  private final UserStorage userStorage;

  public BankService(TransferStorage transferStorage, UserStorage userStorage) {
    this.transferStorage = transferStorage;
    this.userStorage = userStorage;
  }

  private record TransferInfo(Transfer transfer, Optional<BigDecimal> balanceAfterTransfer,
      Optional<String> message) {
  }

  private record UserInfo(User user, List<Transfer> transfers) {
  }

  public Optional<User> registerUser(String fullName, BigDecimal balance) {
    return userStorage.insert(fullName, balance);
  }

  private TransferInfo transfer(User user, BigDecimal amount) {
    var transfer = new Transfer(user, amount, TransferStatus.ACCEPTED);

    if (!userStorage.findById(user.getId()).isPresent()) {
      transfer.setStatus(TransferStatus.DECLINED);

      return new TransferInfo(transfer, Optional.empty(),
          Optional.of("User not found"));
    }

    if (user.getBalance().compareTo(amount) == -1) {
      transfer.setStatus(TransferStatus.DECLINED);

      return new TransferInfo(transfer, Optional.empty(),
          Optional.of("Insufficient funds"));
    }

    transferStorage.insert(transfer);

    // Previous check ensures that the user exist so we can safely use .get()
    var updatedUser = userStorage.update(user.getId(), u -> u.decreaseBalance(amount));

    return new TransferInfo(transfer, updatedUser.map(u -> u.getBalance()),
        Optional.empty());
  }

  private TransferInfo increaseBalance(User user, BigDecimal amount) {
    var transfer = new Transfer(user, amount, TransferStatus.ACCEPTED);

    if (!userStorage.findById(user.getId()).isPresent()) {
      transfer.setStatus(TransferStatus.DECLINED);

      return new TransferInfo(transfer, Optional.empty(),
          Optional.of("User not found"));
    }

    transferStorage.insert(transfer);

    var updatedUser = userStorage.update(user.getId(), u -> u.increaseBalance(amount));

    return new TransferInfo(transfer, updatedUser.map(u -> u.getBalance()),
        Optional.empty());
  }

  public Optional<UserInfo> getUserInfo(String id) {
    var user = userStorage.findById(id);

    if (user.isEmpty()) {
      return Optional.empty();
    }

    var transfers = transferStorage.findAll(t -> t.getUser().getId() == id);

    return Optional.of(new UserInfo(user.get(), transfers));
  }
}
