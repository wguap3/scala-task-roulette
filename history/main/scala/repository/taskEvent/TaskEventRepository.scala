package repository.taskEvent

import cats.effect.IO
import domain.TaskEvent

import java.util.UUID

trait TaskEventRepository:
  def create(event: TaskEvent): IO[Unit]

  def findByTask(taskId: UUID): IO[List[TaskEvent]]

  def findByUser(userId: UUID): IO[List[TaskEvent]]

  def findByRoom(roomId: UUID): IO[List[TaskEvent]]