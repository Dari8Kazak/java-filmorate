# java-filmorate
Template repository for Filmorate project.

## Схема базы данных изображения
![Image](https://github.com/user-attachments/assets/0ccf0c8f-a651-45d3-acdc-64ff7f82d592)
 Схема базы данных команды

<details>
    <summary>
    Чтобы воссоздать ее в https://dbdiagram.io/ откройте список команд находящийся ниже.
    </summary> 

```sql

Table users {

  id long pk

  name String

  login String

  email String

  birthday LocalDate

  friends Long

}

 Table films {

  id Long pk

  name String

  description String

  releaseDate LocalData

  duration Long

  likes Long 

  genres Genre

  mpa RatingMPA

} 

Table filmgenre {

  film_id Long

  genre_id Long

}

Table genres {

  id Long pk

  name String pk

}

Table film_MPA {

  MPA_id Long

  MPA_name Long

}

  Table  rating_MPA {

  id Long

  name String

}

Table likes {

  film_id Long pk

  user_id Long

}

Table friendShip {

  friend_id Long

  user_id Long

}

  

Ref: "films"."id" < "likes"."film_id"

  

Ref: "users"."id" < "likes"."user_id"

  

Ref: "films"."id" < "filmgenre"."film_id"

  

Ref: "genres"."id" < "filmgenre"."genre_id"

  

Ref: "users"."id" < "friendShip"."friend_id"

  

Ref: "users"."id" < "friendShip"."user_id"

  




Ref: "films"."mpa" < "film_MPA"."MPA_id"

Ref: "film_MPA"."MPA_name" < "rating_MPA"."id"
```
