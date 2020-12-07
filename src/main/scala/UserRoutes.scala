import UserRegistry._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout

import scala.concurrent.Future

//#import-json-formats
//#user-routes-class
class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getUsers(): Future[AddressBooks] =
    userRegistry.ask(GetAddresses)
  def getUser(name: String): Future[GetAddressBookResponse] =
    userRegistry.ask(GetAddressBook(name, _))
  def createUser(user: AddressBook): Future[ActionPerformed] =
    userRegistry.ask(CreateAddressBook(user, _))
  def deleteUser(name: String): Future[ActionPerformed] =
    userRegistry.ask(DeleteAddressBook(name, _))
  def updateUser(name: String, user: AddressBook): Future[ActionPerformed2] =
    userRegistry.ask(UpdateAddressBook(user, name, _))


  //#all-routes
  //#users-get-post
  //#users-get-delete
  val userRoutes: Route =
    pathPrefix("address") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              complete(getUsers())
            },
            post {
              entity(as[AddressBook]) { user =>
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#users-get-delete
        //#users-get-post
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              rejectEmptyResponse {
                onSuccess(getUser(name)) { response =>
                  complete(response.maybeUser)
                }
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              onSuccess(deleteUser(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            },
            put {
              rejectEmptyResponse{
                entity(as[AddressBook]) { user =>
                  onSuccess(updateUser(name, user)){ response =>
                    complete((StatusCodes.OK), response.description)
                  }
                }
              }
            }
          )
        })
      //#users-get-delete
    }
  //#all-routes
}
