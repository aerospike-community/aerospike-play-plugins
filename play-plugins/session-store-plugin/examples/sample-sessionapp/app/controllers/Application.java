/*
 * Copyright (C) 2015 Aeroshift Authors
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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStore;
import com.aerospike.session.SessionStoreException;

import play.data.Form;
import play.mvc.*;
import views.html.*;

@RequiredArgsConstructor(onConstructor=@_(@Inject))
public class Application extends Controller {

	private final SessionStore store;
	
	public Result index() {
    	
    	return ok(home.render(Form.form(ShoppingItem.class)));
    }
	
	public Result gohome(){
		return ok(home.render(Form.form(ShoppingItem.class)));
	
	}
	
	public Result createItem() throws SessionStoreException, SessionNotFound
	{
		Form <ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if(form.hasErrors())
		{
			return badRequest(home.render(form));
		}
		else
		{
			ShoppingItem data = form.get();
			//store.checkAndSet(new AddToCartOperation(data));
			store.checkAndSet(new AddNewCart(data));
			return ok(addnew.render(data));
		}
	}
	@SuppressWarnings("unchecked")
	public Result list() throws SessionStoreException, SessionNotFound{
		//ShoppingCart checkcart = (ShoppingCart) store.get("shopping-cart");
		List<ShoppingItem> checkcart = (List<ShoppingItem>) store.get("shopping-list");
		if(checkcart == null ) {
			checkcart = Collections.emptyList();
		}
		return ok(list.render(checkcart));
		
	}
	
}
