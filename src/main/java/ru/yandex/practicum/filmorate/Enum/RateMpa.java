package ru.yandex.practicum.filmorate.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum RateMpa {
    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC17(5, "NC-17");

    private final int id;
    @Getter
    private final String name;

    @JsonCreator
    public static RateMpa forValues(@JsonProperty("id") int id) {
        for (RateMpa rating : RateMpa.values()) {
            if (rating.id == id) {
                return rating;
            }
        }
        return null;
    }
}