package service

import cats.effect.IO
import domain.*
import repository.roomMember.RoomMemberRepository
import repository.task.TaskRepository

import java.time.Instant
import java.util.UUID

class TaskService(
                   taskRepo: TaskRepository,
                   roomMemberRepo: RoomMemberRepository
                 ):

  def create(
              title: String,
              description: Option[String],
              difficulty: Difficulty,
              category: Option[String],
              roomId: UUID,
              createdBy: UUID
            ): IO[Either[AppError, Task]] =
    roomMemberRepo.findByRoomAndUser(roomId, createdBy).flatMap {
      case Some(member) if member.role == Owner =>
        val task = Task(
          id = UUID.randomUUID(),
          title = title,
          description = description,
          difficulty = difficulty,
          category = category,
          roomId = roomId,
          createdBy = createdBy,
          createdAt = Instant.now(),
          isActive = true
        )
        taskRepo.create(task).map(_ => Right(task))
      case _ => IO.pure(Left(AccessDenied))
    }

  def findById(id: UUID): IO[Either[AppError, Task]] =
    taskRepo.findById(id).map {
      case Some(task) => Right(task)
      case None => Left(TaskNotFound(id))
    }

  def findByRoom(roomId: UUID): IO[List[Task]] =
    taskRepo.findActiveByRoom(roomId)

  def archive(id: UUID, requesterId: UUID): IO[Either[AppError, Unit]] =
    taskRepo.findById(id).flatMap {
      case None => IO.pure(Left(TaskNotFound(id)))
      case Some(task) =>
        roomMemberRepo.findByRoomAndUser(task.roomId, requesterId).flatMap {
          case Some(member) if member.role == Owner =>
            taskRepo.archive(id).map(_ => Right(()))
          case _ => IO.pure(Left(AccessDenied))
        }
    }