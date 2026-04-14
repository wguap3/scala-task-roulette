package service

import cats.effect.IO
import domain.{Task, TaskEvent, InProgress, Skipped, Completed, AppError, NoTaskAvailable}
import repository.task.TaskRepository
import repository.taskEvent.TaskEventRepository
import java.util.UUID
import java.time.Instant
import scala.util.Random

class RouletteService(
                       taskRepo: TaskRepository,
                       taskEventRepo: TaskEventRepository
                     ):

  def spin(roomId: UUID, userId: UUID): IO[Either[AppError, Task]] =
    for
      tasks  <- taskRepo.findActiveByRoom(roomId)
      events <- taskEventRepo.findByRoom(roomId)
    yield selectTask(tasks, events, userId)

  private def selectTask(
                          tasks: List[Task],
                          events: List[TaskEvent],
                          userId: UUID
                        ): Either[AppError, Task] =

    if tasks.isEmpty then
      Left(NoTaskAvailable)
    else
      val inProgressIds = events
        .filter(_.status == InProgress)
        .map(_.taskId)
        .toSet

      val completedByUser = events
        .filter(e => e.userId == userId && e.status == Completed)
        .map(_.taskId)
        .toSet

      val skippedByUser = events
        .filter(e => e.userId == userId && e.status == Skipped)
        .map(_.taskId)
        .toSet

      val available = tasks.filterNot(t => inProgressIds.contains(t.id))

      if available.isEmpty then
        Left(NoTaskAvailable)
      else
        val weighted = available.map { task =>
          val weight =
            if completedByUser.contains(task.id) then 1
            else if skippedByUser.contains(task.id) then 2
            else 5

          (task, weight)
        }

        Right(pickWeighted(weighted))

  private def pickWeighted(weighted: List[(Task, Int)]): Task =

    val expanded = weighted.flatMap { (task, weight) =>
      List.fill(weight)(task)
    }
    expanded(Random.nextInt(expanded.size))