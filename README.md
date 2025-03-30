![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

## Запуск приложения

При запуске приложения в IntelliJ Idea нужно добавить 2 переменные окружения в конфигурацию запуска:
* app.telegram-bot - Токен телеграм бота. Добавляется в переменные окружения BotApplication
* app.github-token - Токен github api. Добавляется в переменные окружения ScrapperApplication

После этого нужно последовательно запустить BotApplication и ScrapperApplication. Выполнив эти действия,
приложение должно начать корректо работать.

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.

Для дополнительной справки: [HELP.md](./HELP.md)
