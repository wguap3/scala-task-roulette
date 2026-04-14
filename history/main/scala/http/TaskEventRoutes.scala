package http

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import service.TaskEventService
import domain.{EventStatus, Completed, Skipped, InProgress}
import java.util.UUID

case class CreateEventDto(
                           taskId: UUID,
                           userId: UUID,
                           roomId: UUID,
                           status: String,
                           comment: Option[String]
                         )

class TaskEventRoutes(taskEventService: TaskEventService):

  val routes = HttpRoutes.of[IO] {

    case req @ POST -> Root / "events" =>
      for
        dto <- req.as[CreateEventDto]
        status = dto.status match
          case "completed"  => Completed
          case "skipped"    => Skipped
          case _            => InProgress
        result <- taskEventService.addEvent(
          taskId  = dto.taskId,
          userId  = dto.userId,
          roomId  = dto.roomId,
          status  = status,
          comment = dto.comment
        )
        resp <- result match
          case Right(event) => Created(event)
          case Left(_)      => NotFound("Задача не найдена")
      yield resp


    case GET -> Root / "tasks" / UUIDVar(taskId) / "events" =>
      taskEventService.findByTask(taskId).flatMap(events => Ok(events))

    case GET -> Root / "users" / UUIDVar(userId) / "events" =>
      taskEventService.findByUser(userId).flatMap(events => Ok(events))

    case GET -> Root / "rooms" / UUIDVar(roomId) / "events" =>
      taskEventService.findByRoom(roomId).flatMap(events => Ok(events))
  }