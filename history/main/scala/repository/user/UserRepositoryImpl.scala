package repository.user

import cats.effect.IO
import domain.User
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository:
  def create(user:User): IO[Unit] =
    sql"""
    INSERT INTO users (id,name,created_at)
    VALUES (${user.id}, ${user.name}, ${user.createdAt})
    """.update.run.transact(xa).void

  def findById(id:UUID): IO[Option[User]] =
    sql"""
    SELECT id, name, created_at
    FROM users
    WHERE id = $id
    """.query[User].option.transact(xa)

  def findAll: IO[List[User]] =
    sql"""
        SELECT * FROM users
        """.query[User].to[List].transact(xa)

