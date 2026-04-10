package config

case class DbConfig(
                   url: String,
                   user: String,
                   password: String,
                   driver: String
                   )

case class ServerConfig(
                       host: String,
                       port: Int
                       )

case class AppConfig(
                    db: DbConfig,
                    server: ServerConfig
                    )