# Prompts

1)
Создай Use Case Diagram в PlantUML для проекта Task Tracker.
Архитектура состоит из двух независимых микросервисов:
User Service и Task Service.

Актор один:
User.

Основные функции:
Создать пользователя.
Получить пользователя по ID.
Создать задачу.
Получить список задач пользователя.
Делегировать задачу другому пользователю.

Раздели функции по package микросервисов.
Будущие возможности выдели светло-серым цветом через stereotype future:
Authorization, Roles, Comments, Attachments, Deadlines, Notifications, Task priority.
Используй left to right direction, добавь title.
Верни только PlantUML.

2)
Создай Sequence Diagram в PlantUML для делегирования задачи.

Участники:
Client
Task Service
User Service
Task Database
User Database

Основной запрос:
POST /tasks/{taskId}/delegate?assigneeId=X

Task Service должен сначала найти задачу в своей базе.
После этого он проверяет пользователя HTTP-запросом:
GET /users/{assigneeId}
в User Service.

Покажи alt/else для сценариев:
1. assigneeId некорректный - 400 Bad Request.
2. Задача не найдена - 404 Not Found.
3. Пользователь найден - Task Service обновляет assigneeId и возвращает 200 OK.
4. Пользователь не найден - 404 Not Found.
5. User Service недоступен или вернул 5xx - 503 Service Unavailable.

Важно: Task Service не обращается напрямую к User Database.
Используй activation/deactivation, добавь title.
Верни только PlantUML.

3)
Создай ER Diagram в PlantUML для Task Tracker.
Покажи две независимые базы данных:

User Service Database:
User
id BIGINT PK
first_name VARCHAR(100)
last_name VARCHAR(100)
email VARCHAR(255) UNIQUE

Task Service Database:
Task
id BIGINT PK
title VARCHAR(255)
status ENUM
creator_id BIGINT
assignee_id BIGINT

ENUM status:
TODO
IN_PROGRESS
DONE

creator_id и assignee_id являются только идентификаторами пользователей.
Никаких foreign key между Task и User быть не должно.
Добавь title.
Верни только PlantUML.
