package io.github.kirillbadin.tennisscoreboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMatchRequestDto (@NotBlank(message = "Имя игрока не может быть пустым")
                                     @Size(max = 50, message = "Имя игрока не может быть длиннее 50 символов")
                                     String firstPlayerName,

                                     @NotBlank(message = "Имя игрока не может быть пустым")
                                     @Size(max = 50, message = "Имя игрока не может быть длиннее 50 символов")
                                     String secondPlayerName){
}
