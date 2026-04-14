package service

import cats.effect.IO
import domain.{AppError, User, UserNotFound}
import repository.user.UserRepository

import java.time.Instant
import java.util.UUID

class UserService(userRepo: UserRepository):

  def create(name: String): IO[User] =
    val user = User(
      id = UUID.randomUUID(),
      name = name,
      createdAt = Instant.now()
    )
    userRepo.create(user) *> IO.pure(user)

  def findById (id: UUID): IO[Either[AppError, User]] =
  userRepo.findById(id).map {
    case Some(user) => Right(user)
    case None => Left(UserNotFound(id)) 
  }

  def findAll: IO[List[User]] =
    userRepo.findAll


