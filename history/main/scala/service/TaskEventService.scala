package service

import cats.effect.IO
import domain.{TaskEvent, EventStatus, AppError, TaskNotFound}
import repository.taskEvent.TaskEventRepository
import repository.task.TaskRepository
import java.util.UUID
import java.time.Instant

class TaskEventService(
                        taskEventRepo: TaskEventRepository,
                        taskRepo: TaskRepository
                      ):

  def addEvent(
                taskId: UUID,
                userId: UUID,
                roomId: UUID,
                status: EventStatus,
                comment: Option[String]
              ): IO[Either[AppError, TaskEvent]] =
    taskRepo.findById(taskId).flatMap {
      case None => IO.pure(Left(TaskNotFound(taskId)))
      case Some(_) =>
        val event = TaskEvent(
          id          = UUID.randomUUID(),
          taskId      = taskId,
          userId      = userId,
          roomId      = roomId,
          status      = status,
          comment     = comment,
          occurredAt  = Instant.now()
        )
        taskEventRepo.create(event).map(_ => Right(event))
    }

  def findByTask(taskId: UUID): IO[List[TaskEvent]] =
    taskEventRepo.findByTask(taskId)

  def findByUser(userId: UUID): IO[List[TaskEvent]] =
    taskEventRepo.findByUser(userId)

  def findByRoom(roomId: UUID): IO[List[TaskEvent]] =
    taskEventRepo.findByRoom(roomId)