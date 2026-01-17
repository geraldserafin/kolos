package pl.edu.pjwstk.s34101Bank;

import java.math.BigDecimal;

public class User {
  private String id;

  private String fullName;
  private BigDecimal balance;

  public User(String id, String fullName, BigDecimal balance) {
    this.id = id;
    this.fullName = fullName;
    this.balance = balance.setScale(2);
  }

  public String getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public void decreaseBalance(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }

  public void increaseBalance(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
