package domain

import java.util.UUID
sealed trait AppError

case class UserNotFound(id:UUID) extends AppError
case class RoomNotFound(id:UUID) extends AppError
case class TaskNotFound(id:UUID) extends AppError
case object AccessDenied extends AppError
case object NoTaskAvailable extends AppError
case class AlreadyMember(roomId:UUID, userId:UUID) extends AppError