package repository.task

import cats.effect.IO
import domain.Task

import java.util.UUID

trait TaskRepository:
  def create(task: Task): IO[Unit]

  def findById(id: UUID): IO[Option[Task]]

  def findByRoom(roomId: UUID): IO[List[Task]]

  def findActiveByRoom(roomId: UUID): IO[List[Task]]

  def archive(id: UUID): IO[Unit]