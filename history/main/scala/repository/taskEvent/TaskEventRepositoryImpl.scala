package repository.taskEvent

import cats.effect.IO
import domain.*
import doobie.Meta
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

class TaskEventRepositoryImpl(xa: Transactor[IO]) extends TaskEventRepository:

  given Meta[EventStatus] = Meta[String].timap {
    case "completed" => Completed
    case "skipped" => Skipped
    case "inprogress" => InProgress
  } {
    case Completed => "completed"
    case Skipped => "skipped"
    case InProgress => "inprogress"
  }

  def create(event: TaskEvent): IO[Unit] =
    sql"""
      INSERT INTO task_events (id, task_id, user_id, room_id, status, comment, occurred_at)
      VALUES (${event.id}, ${event.taskId}, ${event.userId}, ${event.roomId},
              ${event.status}, ${event.comment}, ${event.occurredAt})
    """.update.run.transact(xa).void

  def findByTask(taskId: UUID): IO[List[TaskEvent]] =
    sql"""
      SELECT id, task_id, user_id, room_id, status, comment, occurred_at
      FROM task_events
      WHERE task_id = $taskId
    """.query[TaskEvent].to[List].transact(xa)

  def findByUser(userId: UUID): IO[List[TaskEvent]] =
    sql"""
      SELECT id, task_id, user_id, room_id, status, comment, occurred_at
      FROM task_events
      WHERE user_id = $userId
    """.query[TaskEvent].to[List].transact(xa)

  def findByRoom(roomId: UUID): IO[List[TaskEvent]] =
    sql"""
      SELECT id, task_id, user_id, room_id, status, comment, occurred_at
      FROM task_events
      WHERE room_id = $roomId
    """.query[TaskEvent].to[List].transact(xa)