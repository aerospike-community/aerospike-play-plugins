package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.data.Forms._
import play.api.libs.json._
import models._
import models.Task
import play.api.i18n._
import javax.inject.Inject
import play.cache.Cached
import play.api.cache.CacheApi
import play.cache.NamedCache

class Application @Inject() (cache: CacheApi, @NamedCache("session-cache") sessionCache: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val ShoppingItemForm = Form {
    mapping(
      "id" -> nonEmptyText,
      "productname" -> nonEmptyText,
      "Quantity" -> number)(ShoppingItem.apply)(ShoppingItem.unapply)
  }

  def setSessionCache = Action { implicit request =>
    ShoppingItemForm.bindFromRequest.fold(
      errors => BadRequest(views.html.home(errors)),
      mapping => {
        sessionCache.set("key", new ShoppingItem(mapping.id, mapping.productname, mapping.Quantity))
        Redirect(routes.Application.index)
      })
  }

  def index = Action {
    Ok(views.html.home(ShoppingItemForm))
  }

  def addToCache = Action { implicit request =>
    request.body.asFormUrlEncoded.get("action").headOption match {
      case Some("default cache") => {
        ShoppingItemForm.bindFromRequest.fold(
          errors => BadRequest(views.html.home(errors)),
          mapping => {
            cache.set("key", new ShoppingItem(mapping.id, mapping.productname, mapping.Quantity))
            Redirect(routes.Application.index)
          })
      }
      case Some("session cache") => ShoppingItemForm.bindFromRequest.fold(
        errors => BadRequest(views.html.home(errors)),
        mapping => {
          sessionCache.set("key", new ShoppingItem(mapping.id, mapping.productname, mapping.Quantity))
          Redirect(routes.Application.index)
        })
      case _ => BadRequest("This action is not allowed")

    }
  }


  def list = Action {
    Ok(views.html.list(cache.get("key").toString(), sessionCache.get("key").toString()))
  }
}
