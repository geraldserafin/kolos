package pl.edu.pjwstk.s34101Bank;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferStorageTest {

  private TransferStorage transferStorage;

  @BeforeEach
  void setUp() {
    transferStorage = new TransferStorage();
  }

  @Test
  void shouldInsertTransfer() {
    var user = new User("1", "Gerald", BigDecimal.TEN);
    var transfer = new Transfer(user, BigDecimal.ONE, TransferStatus.ACCEPTED);
    var result = transferStorage.insert(transfer);

    assertTrue(result.isPresent());
    assertEquals(transfer, result.get());
    assertTrue(transferStorage.findAll(t -> true).contains(transfer));
  }

  @Test
  void shouldFindAllTransfersMatchingPredicate() {
    var user1 = new User("1", "Gerald", BigDecimal.TEN);
    var user2 = new User("2", "Serafin", BigDecimal.TEN);

    var transfer1 = new Transfer(user1, BigDecimal.ONE, TransferStatus.ACCEPTED);
    var transfer2 = new Transfer(user2, BigDecimal.TWO, TransferStatus.DECLINED);

    transferStorage.insert(transfer1);
    transferStorage.insert(transfer2);

    var transfers = transferStorage.findAll(t -> t.getUser().getId().equals("1"));

    assertEquals(1, transfers.size());
    assertEquals(transfer1, transfers.get(0));
  }

  @Test
  void shouldReturnEmptyListIfNoMatches() {
    var user1 = new User("1", "Gerald", BigDecimal.TEN);
    var transfer1 = new Transfer(user1, BigDecimal.ONE, TransferStatus.ACCEPTED);

    transferStorage.insert(transfer1);

    var results = transferStorage.findAll(t -> t.getUser().getId().equals("999"));

    assertTrue(results.isEmpty());
  }
}
