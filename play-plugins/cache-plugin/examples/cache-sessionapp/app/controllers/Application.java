package controllers;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStore;
import com.aerospike.session.SessionStoreException;

import play.*;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	private final SessionStore store;
	private final CacheApi cache;

	private final CacheApi myCache;

	@Inject
	public Application(SessionStore store, CacheApi cache, @NamedCache("my-cache") CacheApi myCache) {
		super();
		this.store = store;
		this.cache = cache;
		this.myCache = myCache;
	}

	public Result index() {

		return ok(home.render(Form.form(ShoppingItem.class)));
	}

	public Result formAction() throws SessionStoreException, SessionNotFound {
		String[] postAction = request().body().asFormUrlEncoded().get("action");
		if (postAction == null || postAction.length == 0) {
			return badRequest("You must provide a valid action");
		} else {
			String action = postAction[0];
			if ("addstore".equals(action)) {
				return createItem();
			} else if ("deletestore".equals(action)) {
				return deleteItem();
			} else if ("defaultcache".equals(action)) {
				return addToDefaultCache();
			} else if ("mycache".equals(action)) {
				return addToMyCache();
			} else {
				return badRequest("This action is not allowed");
			}
		}
	}

	public Result createItem() throws SessionStoreException, SessionNotFound {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			store.checkAndSet(new AddNewCart(data));
			return redirect(routes.Application.index());
		}
	}

	public Result deleteItem() throws SessionStoreException, SessionNotFound {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			store.checkAndSet(new DeleteItem(data));
			return redirect(routes.Application.index());
		}
	}

	public Result addToDefaultCache() {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			cache.set("key", data);
			return redirect(routes.Application.index());
		}
	}

	public Result addToMyCache() {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			myCache.set("key", data);
			return redirect(routes.Application.index());
		}
	}

	@SuppressWarnings("unchecked")
	public Result list() throws SessionStoreException, SessionNotFound {
		List<ShoppingItem> checkcart = (List<ShoppingItem>) store.get("shopping-list");
		if (checkcart == null) {
			checkcart = Collections.emptyList();
		}
		ShoppingItem cacheCart = new ShoppingItem();
		ShoppingItem sessionCacheCart = new ShoppingItem();
		if (cache.get("key") != null) {
			cacheCart = cache.get("key");
		}
		if (myCache.get("key") != null) {
			sessionCacheCart = myCache.get("key");
		}
		return ok(list.render(checkcart, cacheCart.toString(), sessionCacheCart.toString()));

	}

}
