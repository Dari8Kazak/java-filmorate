//// Пример интеграционного тестирования с использованием TestRestTemplate
//// и проверки работы приложения целиком, включая REST-контроллер,
//// слой бизнес-логики и работу с базой данных.
//
//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.http.HttpStatus.CREATED;
//import static org.springframework.http.HttpStatus.OK;
//
//// Запускаем приложение с полноценным контекстом и сервером на случайном порту
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
//public class FilmoRateTests {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void testCreateAndGetUser() {
//        // Создаем новый объект пользователя.
//        // Обратите внимание, здесь нужно заполнить необходимые поля.
//        User newUser = new User();
//        newUser.setEmail("test@mail.com");
//        newUser.setLogin("testUser");
//        newUser.setName("Test User");
//        newUser.setBirthday(LocalDate.of(2000, 1, 1));
//
//        // Отправляем запрос на создание пользователя.
//        // Предполагается, что конечная точка для создания пользователя - POST /users.
//        ResponseEntity<User> createResponse = restTemplate.postForEntity("/users", newUser, User.class);
//
//        // Проверяем, что пользователь был создан и статус ответа соответствует CREATED.
//        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);
//        User createdUser = createResponse.getBody();
//        assertThat(createdUser).isNotNull();
//        assertThat(createdUser.getId()).isNotNull();
//
//        // Отправляем GET-запрос для получения только что созданного пользователя.
//        // Предполагается, что конечная точка для получения пользователя по id - GET /users/{id}.
//        ResponseEntity<User> getResponse = restTemplate.exchange(
//                "/users/" + createdUser.getId(), HttpMethod.GET, HttpEntity.EMPTY, User.class);
//        assertThat(getResponse.getStatusCode()).isEqualTo(OK);
//        User retrievedUser = getResponse.getBody();
//        assertThat(retrievedUser).isNotNull();
//
//        // Проверяем основные поля полученного пользователя.
//        assertThat(retrievedUser.getId()).isEqualTo(createdUser.getId());
//        assertThat(retrievedUser.getName()).isEqualTo("Test User");
//        assertThat(retrievedUser.getEmail()).isEqualTo("test@mail.com");
//    }
//}