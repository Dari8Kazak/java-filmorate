# java-filmorate
Template repository for Filmorate project.

## Схема базы данных изображения
![Image](https://github.com/user-attachments/assets/0c0726a3-d4ba-428b-922d-12dd9f7f028e)

 Схема базы данных команды

<details>
    <summary>
    Чтобы воссоздать ее в https://dbdiagram.io/ откройте список команд находящийся ниже.
    </summary> 

```sql
Table users {

  id long

  name String

  login String

  email String

  birthday LocalDate

}

  

Table likes {

  film_id Long

  user_id Long

}

  

Table films {

  id Long

  name String

  description String

  releaseDate LocalData

  duration Long

  rate Long

}

  

Table birthday {

  data LocalDate

  user_id Long

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

  genre_id Long

  genre_name String

}

  

Table film_MPA {

  MPA_id Long

  MPA_name Long

}

  

Table MPA {

  film_id Long

  MPA_id Long

}

  
  
  
  

Ref: "films"."id" < "likes"."film_id"

  

Ref: "users"."id" < "likes"."user_id"

  

Ref: "films"."id" < "filmgenre"."film_id"

  

Ref: "genres"."genre_id" < "filmgenre"."genre_id"

  

Ref: "users"."id" < "friendShip"."friend_id"

  

Ref: "users"."id" < "friendShip"."user_id"

  

Ref: "films"."id" < "MPA"."film_id"

  

Ref: "MPA"."MPA_id" < "film_MPA"."MPA_id"

  

Ref: "users"."id" < "birthday"."user_id"
```


Примерно такую схему потребуется реализовать в конце модуля.
