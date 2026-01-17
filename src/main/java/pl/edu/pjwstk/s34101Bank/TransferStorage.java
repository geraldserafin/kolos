package pl.edu.pjwstk.s34101Bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

@Component
public class TransferStorage {
  private List<Transfer> transfers = new ArrayList<>();

  public Optional<Transfer> insert(Transfer transfer) {
    transfers.add(transfer);
    return Optional.of(transfer);
  }

  public List<Transfer> findAll(Predicate<Transfer> predicate) {
    return transfers.stream().filter(predicate).toList();
  }
}
