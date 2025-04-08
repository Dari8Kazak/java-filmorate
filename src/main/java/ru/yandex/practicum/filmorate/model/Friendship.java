package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.Enum.FriendshipStatus;

@Getter
public class Friendship {
    private Long friendId;
    @Setter
    private FriendshipStatus status;

    public Friendship(Long friendId, FriendshipStatus status) {
        this.friendId = friendId;
        this.status = status;
    }

}
