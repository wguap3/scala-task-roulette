package config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

case class DbConfig(
                   url: String,
                   user: String,
                   password: String,
                   driver: String
                   )derives ConfigReader

case class ServerConfig(
                       host: String,
                       port: Int
                       )derives ConfigReader

case class AppConfig(
                    db: DbConfig,
                    server: ServerConfig
                    )derives ConfigReader