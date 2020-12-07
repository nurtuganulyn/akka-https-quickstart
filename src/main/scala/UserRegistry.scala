//#user-registry-actor
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable

//#user-case-classes
final case class AddressBook(name: String, age: Int, address: String)
final case class AddressBooks(users: immutable.Seq[AddressBook])
//#user-case-classes

object UserRegistry {

  // actor protocol
  sealed trait Command
  final case class GetAddresses(replyTo: ActorRef[AddressBooks]) extends Command
  final case class CreateAddressBook(user: AddressBook, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetAddressBook(name: String, replyTo: ActorRef[GetAddressBookResponse]) extends Command
  final case class DeleteAddressBook(name: String, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class UpdateAddressBook(user: AddressBook, name: String, replyTo: ActorRef[ActionPerformed2]) extends Command

  final case class GetAddressBookResponse(maybeUser: Option[AddressBook])
  final case class ActionPerformed(description: String)
  final case class ActionPerformed2(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(users: Set[AddressBook]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetAddresses(replyTo) =>
        replyTo ! AddressBooks(users.toSeq)
        Behaviors.same
      case CreateAddressBook(user, replyTo) =>
        replyTo ! ActionPerformed(s"User ${user.name} created.")
        registry(users + user)
      case GetAddressBook(name, replyTo) =>
        replyTo ! GetAddressBookResponse(users.find(_.name == name))
        Behaviors.same
      case DeleteAddressBook(name, replyTo) =>
        replyTo ! ActionPerformed(s"User $name deleted.")
        registry(users.filterNot(_.name == name))
      case UpdateAddressBook(user, name,  replyTo) =>
        replyTo ! ActionPerformed2(s"User $name has been updated")
        val temp_users = users.filterNot(_.name == name)
        registry(temp_users + user)
    }
}
//#user-registry-actor
