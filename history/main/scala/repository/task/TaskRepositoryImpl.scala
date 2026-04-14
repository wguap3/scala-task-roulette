package repository.task

import cats.effect.IO
import domain.*
import doobie.Meta
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

class TaskRepositoryImpl(xa: Transactor[IO]) extends TaskRepository:

  given Meta[Difficulty] = Meta[String].timap {
    case "easy"   => Easy
    case "medium" => Medium
    case "hard"   => Hard
  } {
    case Easy   => "easy"
    case Medium => "medium"
    case Hard   => "hard"
  }

  def create(task: Task): IO[Unit] =
    sql"""
      INSERT INTO tasks (id, title, description, difficulty, category, room_id, created_by, created_at, is_active)
      VALUES (${task.id}, ${task.title}, ${task.description}, ${task.difficulty},
              ${task.category}, ${task.roomId}, ${task.createdBy}, ${task.createdAt}, ${task.isActive})
    """.update.run.transact(xa).void

  def findById(id: UUID): IO[Option[Task]] =
    sql"""
      SELECT id, title, description, difficulty, category, room_id, created_by, created_at, is_active
      FROM tasks
      WHERE id = $id
    """.query[Task].option.transact(xa)

  def findByRoom(roomId: UUID): IO[List[Task]] =
    sql"""
      SELECT id, title, description, difficulty, category, room_id, created_by, created_at, is_active
      FROM tasks
      WHERE room_id = $roomId
    """.query[Task].to[List].transact(xa)

  def findActiveByRoom(roomId: UUID): IO[List[Task]] =
    sql"""
      SELECT id, title, description, difficulty, category, room_id, created_by, created_at, is_active
      FROM tasks
      WHERE room_id = $roomId AND is_active = true
    """.query[Task].to[List].transact(xa)

  def archive(id: UUID): IO[Unit] =
    sql"""
      UPDATE tasks SET is_active = false WHERE id = $id
    """.update.run.transact(xa).void