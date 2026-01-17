package pl.edu.pjwstk.s34101Bank;

import java.math.BigDecimal;

public class Transfer {
  User user;
  BigDecimal amount;
  TransferStatus status;

  public Transfer(User user, BigDecimal amount, TransferStatus status) {
    this.user = user;
    this.amount = amount;
    this.status = status;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public void setStatus(TransferStatus status) {
    this.status = status;
  }
}
