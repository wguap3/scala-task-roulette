package http

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import service.RouletteService

import java.util.UUID

given QueryParamDecoder[UUID] =
  QueryParamDecoder[String].emap { str =>
    scala.util.Try(UUID.fromString(str))
      .toEither
      .left.map(e => ParseFailure(e.getMessage, e.getMessage))
  }

object UserIdParam extends QueryParamDecoderMatcher[UUID]("userId")

class RouletteRoutes(rouletteService: RouletteService):

  val routes = HttpRoutes.of[IO] {
    // GET /rooms/:roomId/spin?userId=...
    case GET -> Root / "rooms" / UUIDVar(roomId) / "spin" :? UserIdParam(userId) =>
      rouletteService.spin(roomId, userId).flatMap {
        case Right(task) => Ok(task)
        case Left(_) => NotFound("Нет доступных задач")
      }
  }
