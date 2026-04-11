package config

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

object Database:

  def migrate(config: DbConfig): IO[Unit] =
    IO {
      Flyway
        .configure()
        .dataSource(
          config.url,
          config.user,
          config.password
        )
        .locations("classpath:db/migration")
        .load()
        .migrate()
    }.void

  def transactor(config: DbConfig): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = config.driver,
      url             = config.url,
      user            = config.user,
      pass            = config.password,
      connectEC       = ExecutionContext.global
    )
