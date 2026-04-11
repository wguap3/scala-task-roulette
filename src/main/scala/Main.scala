import cats.effect.{IO, IOApp}
import config.{AppConfig, Database}
import pureconfig.ConfigSource
import pureconfig.generic.derivation.default.*

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    val config = ConfigSource.default.loadOrThrow[AppConfig]

    Database.transactor(config.db).use { xa =>
      IO.println("Task Roulette запускается...") *>
        IO.println("Загружаем конфиг...") *>
        IO.println("Запускаем миграции БД...") *>
        Database.migrate(config.db) *>
        IO.println("Миграции применены!") *>
        IO.println(s"Сервер запущен на порту ${config.server.port}")
    }