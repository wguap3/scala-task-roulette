package http

import cats.effect.IO
import domain.{Difficulty, Easy, Hard, Medium}
import io.circe.Encoder
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.io.*
import service.TaskService

import java.util.UUID

given Encoder[Difficulty] = Encoder.encodeString.contramap {
  case Easy => "easy"
  case Medium => "medium"
  case Hard => "hard"
}

case class CreateTaskDto(
                          title: String,
                          description: Option[String],
                          difficulty: String,
                          category: Option[String],
                          roomId: UUID,
                          createdBy: UUID
                        )

case class ArchiveTaskDto(requesterId: UUID)

class TaskRoutes(taskService: TaskService):

  val routes = HttpRoutes.of[IO] {

    case req@POST -> Root / "tasks" =>
      for
        dto <- req.as[CreateTaskDto]
        difficulty = dto.difficulty match
          case "easy" => Easy
          case "medium" => Medium
          case _ => Hard
        result <- taskService.create(
          title = dto.title,
          description = dto.description,
          difficulty = difficulty,
          category = dto.category,
          roomId = dto.roomId,
          createdBy = dto.createdBy
        )
        resp <- result match
          case Right(task) => Created(task)
          case Left(_) => Forbidden("Нет прав")
      yield resp

    case GET -> Root / "tasks" / UUIDVar(id) =>
      taskService.findById(id).flatMap {
        case Right(task) => Ok(task)
        case Left(_) => NotFound("Задача не найдена")
      }

    case GET -> Root / "rooms" / UUIDVar(roomId) / "tasks" =>
      taskService.findByRoom(roomId).flatMap(tasks => Ok(tasks))

    case req@DELETE -> Root / "tasks" / UUIDVar(id) =>
      for
        dto <- req.as[ArchiveTaskDto]
        result <- taskService.archive(id, dto.requesterId)
        resp <- result match
          case Right(_) => Ok("Задача архивирована")
          case Left(_) => Forbidden("Нет прав")
      yield resp
  }