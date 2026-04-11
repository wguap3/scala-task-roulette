package domain

import java.util.UUID
import java.time.Instant

case class User(
               id: UUID,
               name: String,
               createdAt: Instant
               )

case class Room(
               id: UUID,
               name: String,
               createdBy: UUID,
               createdAt: Instant
               )

sealed trait RoomRole
case object Owner extends RoomRole
case object Member extends RoomRole

case class RoomMember(
                     roomId: UUID,
                     userId: UUID,
                     role: RoomRole,
                     joinedAt: Instant
                     )

sealed trait Difficulty
case object Easy extends Difficulty
case object Medium extends Difficulty
case object Hard extends Difficulty

case class Task(
               id: UUID,
               title: String,
               description: String,
               difficulty: Difficulty,
               category: Option[String],
               roomId: UUID,
               createdBy: UUID,
               createdAt: Instant,
               isActive: Boolean
               )

sealed trait EventStatus
case object Completed  extends EventStatus
case object Skipped    extends EventStatus
case object InProgress extends EventStatus

case class TaskEvent(
                      id: UUID,
                      taskId: UUID,
                      userId: UUID,
                      roomId: UUID,
                      status: EventStatus,
                      comment: Option[String],
                      occurredAt: Instant
                    )