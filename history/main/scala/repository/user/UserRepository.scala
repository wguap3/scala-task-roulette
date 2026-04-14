package repository.user

import cats.effect.IO
import domain.User

import java.util.UUID

trait UserRepository:
  def create(user: User): IO[Unit]

  def findById(id: UUID): IO[Option[User]]

  def findAll: IO[List[User]]
