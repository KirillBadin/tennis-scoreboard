package io.github.kirillbadin.tennisscoreboard.dto;

import jakarta.validation.constraints.NotBlank;

public record PointRequestDto (@NotBlank(message = "Имя игрока не может быть пустым") String name){
}
