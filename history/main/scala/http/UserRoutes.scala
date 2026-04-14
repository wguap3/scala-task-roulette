package http

import cats.effect.IO
import io.circe.generic.auto.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import service.UserService

import java.time.Instant
import java.util.UUID

given Encoder[UUID] = Encoder.encodeString.contramap(_.toString)
given Decoder[UUID] = Decoder.decodeString.emap(s =>
  scala.util.Try(UUID.fromString(s)).toEither.left.map(_.getMessage)
)
given Encoder[Instant] = Encoder.encodeString.contramap(_.toString)
given Decoder[Instant] = Decoder.decodeString.emap(s =>
  scala.util.Try(Instant.parse(s)).toEither.left.map(_.getMessage)
)

case class CreateUserDto(name: String)

class UserRoutes(userService: UserService):

  val routes = HttpRoutes.of[IO] {

    case req@POST -> Root / "users" =>
      req.as[CreateUserDto].attempt.flatMap {
        case Left(err) =>
          IO.println(s"Ошибка декодинга: $err") *>
            BadRequest(s"Ошибка: ${err.getMessage}")
        case Right(dto) =>
          userService.create(dto.name).attempt.flatMap {
            case Left(err) =>
              IO.println(s"Ошибка сервиса: $err") *>
                InternalServerError(s"Ошибка: ${err.getMessage}")
            case Right(user) =>
              Created(user)
          }
      }

    case GET -> Root / "users" =>
      userService.findAll.flatMap(users => Ok(users))

    case GET -> Root / "users" / UUIDVar(id) =>
      userService.findById(id).flatMap {
        case Right(user) => Ok(user)
        case Left(_) => NotFound("Пользователь не найден")
      }
  }