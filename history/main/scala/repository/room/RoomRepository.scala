package repository.room

import cats.effect.IO
import domain.Room

import java.util.UUID

trait RoomRepository:
  def create(room:Room): IO[Unit]
  def findById(id: UUID): IO[Option[Room]]
  def findAll: IO[List[Room]]
  def findByUser(userId: UUID): IO[List[Room]]
