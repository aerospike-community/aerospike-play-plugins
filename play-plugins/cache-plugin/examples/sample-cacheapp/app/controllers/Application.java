/*
 * Copyright 2008-2015 Aerospike, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Request;
import views.html.*;

@Slf4j
public class Application extends Controller {

	@Inject
	CacheApi cache;

	@Inject
	@NamedCache("session-cache") CacheApi sessionCache;

	public Result index() {
		return ok(home.render(Form.form(ShoppingItem.class)));
	}

	public Result addToCache() {
		String[] postAction = request().body().asFormUrlEncoded().get("action");
		if (postAction == null || postAction.length == 0) {
			return badRequest("You must provide a valid action");
		} else {
			String action = postAction[0];
			if ("default cache".equals(action)) {
				return addToDefaultCache();
			} else if ("session cache".equals(action)) {
				return addToSessionCache();
			} else {
				return badRequest("This action is not allowed");
			}
		}
	}

	public Result addToDefaultCache() {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			cache.set("key", data);
			return redirect(routes.Application.index());
		}
	}

	public Result addToSessionCache() {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class)
				.bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			sessionCache.set("key", data);
			return redirect(routes.Application.index());
		}
	}

	public Result list() {
		ShoppingItem cacheCart = new ShoppingItem();
		ShoppingItem sessionCacheCart = new ShoppingItem();
		if (cache.get("key")!=null){
			cacheCart = cache.get("key");
		}
		if (sessionCache.get("key")!=null){
			sessionCacheCart = sessionCache.get("key");
		}
		return ok(list.render(cacheCart.toString(),sessionCacheCart.toString()));
	}

}
