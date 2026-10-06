package io.github.kirillbadin.tennisscoreboard.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMatchRequestDto (@NotBlank(message = "Имя игрока не может быть пустым")
                                     String firstPlayerName,

                                     @NotBlank(message = "Имя игрока не может быть пустым")
                                     String secondPlayerName){
}
