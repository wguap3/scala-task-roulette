import cats.effect.{IO, IOApp}
import cats.syntax.semigroupk.*
import com.comcast.ip4s.*
import config.{AppConfig, Database}
import http.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import pureconfig.ConfigSource
import pureconfig.generic.derivation.default.*
import repository.room.RoomRepositoryImpl
import repository.roomMember.RoomMemberRepositoryImpl
import repository.task.TaskRepositoryImpl
import repository.taskEvent.TaskEventRepositoryImpl
import repository.user.UserRepositoryImpl
import service.*

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    val config = ConfigSource.default.loadOrThrow[AppConfig]

    Database.transactor(config.db).use { xa =>
      for
        _ <- Database.migrate(config.db)
        _ <- IO.println("Миграции применены!")

        userRepo = UserRepositoryImpl(xa)
        roomRepo = RoomRepositoryImpl(xa)
        roomMemberRepo = RoomMemberRepositoryImpl(xa)
        taskRepo = TaskRepositoryImpl(xa)
        taskEventRepo = TaskEventRepositoryImpl(xa)

        userService = UserService(userRepo)
        roomService = RoomService(roomRepo, roomMemberRepo)
        taskService = TaskService(taskRepo, roomMemberRepo)
        taskEventService = TaskEventService(taskEventRepo, taskRepo)
        rouletteService = RouletteService(taskRepo, taskEventRepo)

        userRoutes = UserRoutes(userService).routes
        roomRoutes = RoomRoutes(roomService).routes
        taskRoutes = TaskRoutes(taskService).routes
        rouletteRoutes = RouletteRoutes(rouletteService).routes
        taskEventRoutes = TaskEventRoutes(taskEventService).routes

        allRoutes = userRoutes <+> roomRoutes <+> taskRoutes <+> rouletteRoutes <+> taskEventRoutes

        corsApp = CORS.policy
          .withAllowOriginAll
          .withAllowMethodsAll
          .withAllowHeadersAll
          .apply(allRoutes)

        httpApp = Router("/" -> corsApp).orNotFound

        _ <- IO.println(s"Сервер запущен на порту ${config.server.port}")
        _ <- EmberServerBuilder
          .default[IO]
          .withHost(Host.fromString(config.server.host).get)
          .withPort(Port.fromInt(config.server.port).get)
          .withHttpApp(httpApp)
          .build
          .useForever
      yield ()
    }