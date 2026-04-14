package repository.roomMember

import cats.effect.IO
import domain.RoomMember

import java.util.UUID

trait RoomMemberRepository:
  def add(member: RoomMember): IO[Unit]
  def findByRoom(roomId: UUID): IO[List[RoomMember]]
  def findByRoomAndUser(roomId: UUID, userId: UUID): IO[Option[RoomMember]]
  def remove(roomId: UUID, userId: UUID): IO[Unit]