package http

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import service.RoomService

import java.util.UUID

case class CreateRoomDto(name: String, createdBy: UUID)

case class AddMemberDto(userId: UUID, requesterId: UUID)

class RoomRoutes(roomService: RoomService):

  val routes = HttpRoutes.of[IO] {

    case req@POST -> Root / "rooms" =>
      for
        dto <- req.as[CreateRoomDto]
        result <- roomService.create(dto.name, dto.createdBy)
        resp <- Created(result)
      yield resp

    case GET -> Root / "rooms" / UUIDVar(id) =>
      roomService.findById(id).flatMap {
        case Right(room) => Ok(room)
        case Left(_) => NotFound("Комната не найдена")
      }

    case GET -> Root / "rooms" / UUIDVar(roomId) / "members" =>
      roomService.getMembers(roomId).flatMap(members => Ok(members))

    case req@POST -> Root / "rooms" / UUIDVar(roomId) / "members" =>
      for
        dto <- req.as[AddMemberDto]
        result <- roomService.addMember(roomId, dto.userId, dto.requesterId)
        resp <- result match
          case Right(_) => Ok("Участник добавлен")
          case Left(_) => Forbidden("Нет прав или уже участник")
      yield resp
  }