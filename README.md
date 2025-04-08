# java-filmorate
Template repository for Filmorate project.

## Схема базы данных изображения
![Image] https://github.com/Dari8Kazak/java-filmorate/issues/5#issue-2979645122
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

}

  

Table likes {

  film_id Long pk

  user_id Long

}

  

Table films {

  id Long pk

  name String

  description String

  releaseDate LocalData

  duration Long

  rating Long

}


Table friendShip {

  friend_id Long

  user_id Long

}

  

Table filmgenre {

  film_id Long

  genre_id Long

}

  

Table genres {

  genre_id Long pk

  genre_name String pk

}

  

Table film_MPA {

  MPA_id Long

  MPA_name Long

}

  

Table  rating_MPA {

  film_id Long

  MPA_id Long

}

  
  
  
  

Ref: "films"."id" < "likes"."film_id"

  

Ref: "users"."id" < "likes"."user_id"

  

Ref: "films"."id" < "filmgenre"."film_id"

  

Ref: "genres"."genre_id" < "filmgenre"."genre_id"

  

Ref: "users"."id" < "friendShip"."friend_id"

  

Ref: "users"."id" < "friendShip"."user_id"

  

Ref: "films"."id" < "rating_MPA"."film_id"

Ref: "rating_MPA"."MPA_id" < "film_MPA"."MPA_id"

```
