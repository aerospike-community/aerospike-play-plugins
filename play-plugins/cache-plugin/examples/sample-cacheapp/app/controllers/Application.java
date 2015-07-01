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

import play.cache.CacheApi;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	@Inject CacheApi cache;
	
	
	
    public Result index() {
    	return ok(home.render(Form.form(ShoppingItem.class)));
    }
    public Result goHome(){
    	cache.remove("mycart");
    	return ok(home.render(Form.form(ShoppingItem.class)));
    }

    public Result addToCart(){
    	Form <ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if(form.hasErrors())
		{
			return badRequest(home.render(form));
		}
		else
		{
			ShoppingItem data = form.get();
			cache.set("mycart",data);
			return ok(addnew.render(data));
		}
    }
    
    public Result list(){
    	ShoppingItem checkcart = cache.get("mycart");
    	return ok(list.render(checkcart));
	}
    
}
