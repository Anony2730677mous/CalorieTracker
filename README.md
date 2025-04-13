# CalorieTracker - сервис для учета калорий, полученных пользователем после приемов пищи.

Техническое задание для проекта:
Разработать REST API сервис для отслеживания дневной нормы калорий учета съеденных блюд. 
Требования к сервису такие: 
1. Пользователи. Добавление пользователей с параметрами: ID, Имя, Email, Возраст, Вес, Рост, Цель(Похудение, Поддержание, Набор массы)
2. Блюда. Добавление блюд с параметрами: ID, Название, Количество калорий на порцию, Количество белков, жиров, углеводов в блюде на 100 грамм
3. Прием пищи. Пользователь может добавлять приемы пищи со списком блюд
4. Отчеты(это ендпойнты без формирования документа, можно в виде отправки файла JSON на фронтенд): отчет за день с суммой всех калорий и приемов пищи, отчет о проверке, уложился ли пользователь в свою дневную норму калорий, история приемов пищи по дням с выводом потребленных калорий.
5. На основе введенных пользователем данных автоматически рассчитать дневную норму калорий(можно использовать формулу Харриса-Бенедикта) Нефункциональные требования: использовать Spring Boot, Spring Data JPA, PostgresSQL, применить валидацию входных данных от пользователя(проверка роста, возраста, веса). Также необходимо написать unit тесты для основной логики, реализовать обработку ошибок(пользователь, блюдо, прием пищи не найдены).

Проект выполнен на основе микросервисной архитектуры с использованием следующих инструментов: Java 17, Spring Boot 3.3.3, Maven, Kafka 3.1.7, Zookeeper, Postgres 15, JUnit, Mockito, Feign Client. Все микросервисы имеют свои файлы настроек (application.yaml), где задаются параметры подключения к базе данных, Kafka, логирования и т.д. В файлах настроек указаны тестовые данные. Добавлен файл docker-compose.yml. В тестовых классах каждого микросервиса протестированы unit-тестами методы только сервисных классов.

Проект состоит из четырех модулей:
1. user-service – сервис для работы с пользователями (CRUD, валидация).
2. dishes-service – сервис для управления блюдами (CRUD, валидация, информация о блюдах, калорийность, белки, жиры, углеводы).
3. meal-service – сервис для работы с приемами пищи (CRUD, регистрация приемов пищи, связывание с пользователями и блюдами, обработка коллекции блюд, отправка событий в Kafka).
4. report-service – сервис для формирования отчетов. Здесь реализованы эндпоинты, возвращающие JSON-отчеты : суммарный отчет по дням, проверка, уложился ли пользователь в дневную норму, история приемов пищи с выводом потребленных калорий; получение событий из Kafka, расчет дневной нормы калорий на основе формулы Харриса-Бенедикта.
   
Структура проекта:
```
/calorie-tracker
├── users-service
│   ├── src/main/java/...
│   ├── src/test/java/...
│   └── pom.xml
├── dishes-service
│   ├── src/main/java/...
│   ├── src/test/java/...
│   └── pom.xml
├── meal-service
│   ├── src/main/java/...
│   ├── src/test/java/...
│   └── pom.xml
└── report-service
    ├── src/main/java/...
    ├── src/test/java/...
    └── pom.xml
└── pom.xml
```
Для удобства тестирования сервисов есть ссылки на коллекции запросов в postman
## Postman Collections

- **User Service:** [User Service](https://www.postman.com/your-username/workspace/your-workspace/collection/your-user-service-collection](https://lunar-astronaut-228999.postman.co/workspace/Team-Workspace~38144695-1e32-4100-88bb-de34b80eff1f/collection/43354093-1eee36e1-83af-441e-8d76-8a457af84a89?action=share&creator=43354093))
- **Dishes Service:** [Dishes Service](https://www.postman.com/your-username/workspace/your-workspace/collection/your-dishes-service-collection](https://lunar-astronaut-228999.postman.co/workspace/Team-Workspace~38144695-1e32-4100-88bb-de34b80eff1f/collection/43354093-c06965d1-2b06-4a2c-960c-09bab708218d?action=share&creator=43354093))
- **Meal Service:** [Meal Service](https://www.postman.com/your-username/workspace/your-workspace/collection/your-meal-service-collection](https://lunar-astronaut-228999.postman.co/workspace/Team-Workspace~38144695-1e32-4100-88bb-de34b80eff1f/collection/43354093-4b768669-cd7e-49cd-8932-5936a3fcff74?action=share&creator=43354093))
- **Report Service:** [Report Service](https://www.postman.com/your-username/workspace/your-workspace/collection/your-report-service-collection](https://lunar-astronaut-228999.postman.co/workspace/Team-Workspace~38144695-1e32-4100-88bb-de34b80eff1f/collection/43354093-c0145c6a-3ef4-4ab1-9959-2395a992938c?action=share&creator=43354093))


### Установка и запуск

1. **Клонирование репозитория:**
   ```bash
   git clone https://github.com/Anony2730677mous/CalorieTracker.git demo
   cd demo

    ```

2. **Сборка проекта:**
    ```sh
    mvn clean install
    ```

3. **Запуск микросервисов:**
   Перейдите в каталоги каждого микросервиса (например, `users-service`) и выполните:
    ```sh
    mvn spring-boot:run
    ```
4. **Запуск инфраструктуры проекта с помощью `docker-compose up -d` (см. ниже).:**

### Инфраструктура через Docker Compose

В корневой директории находится файл `docker-compose.yml`, который позволяет быстро развернуть Kafka, Zookeeper и PostgreSQL:
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: bitnami/kafka:latest
    container_name: kafka
    environment:
      ALLOW_PLAINTEXT_LISTENER: "yes"
      KAFKA_CFG_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CFG_LISTENERS: PLAINTEXT://0.0.0.0:9092
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    networks:
      - kafka-network

  postgres:
    image: postgres:15
    container_name: postgres
    environment:
      POSTGRES_USER: userok
      POSTGRES_PASSWORD: p@ssw0rd
      POSTGRES_DB: pogreb
    ports:
      - "5400:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

networks:
  kafka-network:
    driver: bridge

volumes:
  postgres-data:
```
