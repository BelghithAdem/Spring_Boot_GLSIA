package com.BeeOranized.BeeOranized.Dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDataDTO {
  private Long userId;
  private String firstName;
  private String lastName;
  private String email;

  public UserDataDTO(Long userId, String email, String lastName, String firstName) {
    this.userId = userId;
    this.email = email;
    this.lastName = lastName;
    this.firstName = firstName;
  }
  // Getters and setters
}
