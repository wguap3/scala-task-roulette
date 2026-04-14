package repository.roomMember

import cats.effect.IO
import domain.{Member, Owner, RoomMember, RoomRole}
import doobie.Meta
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

import java.util.UUID

class RoomMemberRepositoryImpl(xa: Transactor[IO]) extends RoomMemberRepository:

  given Meta[RoomRole] = Meta[String].timap {
    case "owner"  => Owner
    case "member" => Member
  } {
    case Owner  => "owner"
    case Member => "member"
  }

  def add(member: RoomMember): IO[Unit] =
    sql"""
      INSERT INTO room_members (room_id, user_id, role, joined_at)
      VALUES (${member.roomId}, ${member.userId}, ${member.role}, ${member.joinedAt})
    """.update.run.transact(xa).void

  def findByRoom(roomId: UUID): IO[List[RoomMember]] =
    sql"""
      SELECT room_id, user_id, role, joined_at
      FROM room_members
      WHERE room_id = $roomId
    """.query[RoomMember].to[List].transact(xa)

  def findByRoomAndUser(roomId: UUID, userId: UUID): IO[Option[RoomMember]] =
    sql"""
      SELECT room_id, user_id, role, joined_at
      FROM room_members
      WHERE room_id = $roomId AND user_id = $userId
    """.query[RoomMember].option.transact(xa)

  def remove(roomId: UUID, userId: UUID): IO[Unit] =
    sql"""
      DELETE FROM room_members
      WHERE room_id = $roomId AND user_id = $userId
    """.update.run.transact(xa).void