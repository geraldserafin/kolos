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

  private record TransferInfo(Transfer transfer, BigDecimal balanceAfterTransfer) {
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
    }

    if (user.getBalance().compareTo(amount) == -1) {
      transfer.setStatus(TransferStatus.DECLINED);
    }

    transferStorage.insert(transfer);

    // Previous check ensures that the user exist so we can safely use .get()
    var updatedUser = userStorage.update(user.getId(), u -> u.increaseBalance(amount)).get();

    return new TransferInfo(transfer, updatedUser.getBalance());
  }

  public TransferInfo transferElse(User user, BigDecimal amount) {
    return this.transfer(user, amount.negate());
  }

  public TransferInfo addFounds(User user, BigDecimal amount) {
    return this.transfer(user, amount);
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
