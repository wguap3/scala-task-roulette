package repository.room

import cats.effect.IO
import domain.Room
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

class RoomRepositoryImpl(xa: Transactor[IO]) extends RoomRepository:

  def create(room: Room): IO[Unit] =
    sql"""
      INSERT INTO rooms (id, name, created_by, created_at)
      VALUES (${room.id}, ${room.name}, ${room.createdBy}, ${room.createdAt})
    """.update.run.transact(xa).void

  def findById(id: UUID): IO[Option[Room]] =
    sql"""
      SELECT id, name, created_by, created_at
      FROM rooms
      WHERE id = $id
    """.query[Room].option.transact(xa)

  def findAll: IO[List[Room]] =
    sql"""
      SELECT id, name, created_by, created_at
      FROM rooms
    """.query[Room].to[List].transact(xa)

  def findByUser(userId: UUID): IO[List[Room]] =
    sql"""
      SELECT r.id, r.name, r.created_by, r.created_at
      FROM rooms r
      JOIN room_members rm ON r.id = rm.room_id
      WHERE rm.user_id = $userId
    """.query[Room].to[List].transact(xa)