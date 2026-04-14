package service

import domain.{Member, Room}


import cats.effect.IO
import domain.{Room, RoomMember, Owner, AppError, RoomNotFound, AccessDenied, AlreadyMember}
import repository.room.RoomRepository
import repository.roomMember.RoomMemberRepository
import java.util.UUID
import java.time.Instant

class RoomService(
                   roomRepo: RoomRepository,
                   roomMemberRepo: RoomMemberRepository
                 ):

  def create(name: String, createdBy: UUID): IO[Room] =
    val room = Room(
      id        = UUID.randomUUID(),
      name      = name,
      createdBy = createdBy,
      createdAt = Instant.now()
    )
    val owner = RoomMember(
      roomId   = room.id,
      userId   = createdBy,
      role     = Owner,
      joinedAt = Instant.now()
    )
    roomRepo.create(room) *>
      roomMemberRepo.add(owner) *>
      IO.pure(room)

  def findById(id: UUID): IO[Either[AppError, Room]] =
    roomRepo.findById(id).map {
      case Some(room) => Right(room)
      case None       => Left(RoomNotFound(id))
    }

  def addMember(roomId: UUID, userId: UUID, requesterId: UUID): IO[Either[AppError, Unit]] =
    roomMemberRepo.findByRoomAndUser(roomId, requesterId).flatMap {
      case Some(member) if member.role == Owner =>
        roomMemberRepo.findByRoomAndUser(roomId, userId).flatMap {
          case Some(_) => IO.pure(Left(AlreadyMember(roomId, userId)))
          case None =>
            val newMember = RoomMember(
              roomId   = roomId,
              userId   = userId,
              role     = Member,
              joinedAt = Instant.now()
            )
            roomMemberRepo.add(newMember).map(_ => Right(()))
        }
      case _ => IO.pure(Left(AccessDenied))
    }

  def getMembers(roomId: UUID): IO[List[RoomMember]] =
    roomMemberRepo.findByRoom(roomId)