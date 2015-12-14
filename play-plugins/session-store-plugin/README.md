# Aerospike Session Store Plugin for Play Framework 2.4.x

This plugin implements a session-store for Play Framework using  [Aerospike](http://aerospike.com) database. This enables users to store their session
data into Aerospike. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOS.
The plugin provides option for using either one of two Transcoders, [Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization)
and [FasterXML-jackson databind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

The session store API supports atomic check and set operation on session data via user defined idempotent check and set operations, Aerospike generation check along with retries.

## How to Install

### Play version < 2.4.3

Add the following dependency in your application's build.sbt

```
libraryDependencies ++= Seq(
  "com.aerospike" % "aerospike-play-session-store_2.11" % "1.0"
)
```

### Play >= 2.4.3

Requires Java 8 JDK.

Add the following dependency in your application's build.sbt

```
libraryDependencies ++= Seq(
  "com.aerospike" % "aerospike-play-session-store_2.11" % "1.2"
)
```

### Configurations

Following is the description for configuration settings.

* ```play.sessionstore.aerospike.hosts```: Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings. [This field is mandatory]
* ```play.sessionstore.aerospike.username```: Specify aerospike username. [This field is optional]
* ```play.sessionstore.aerospike.password```: Specify your aerospike password. [This field is optional]
* ```play.sessionstore.aerospike.namespace```: Specify aerospike namespace to be used.[This field is mandatory]
* ```play.sessionstore.aerospike.set```: Specify aerospike set name to be used for storing session data.
	It is set to "test" by default.[Optional parameter]
* ```play.sessionstore.aerospike.sessionIdProviderFQCN```: SessionIDProvider gives unique value to each session.
	y default, we use DefaultSessionIDProvider to assign unique value(ID) to a session which is a random string of length 10.
	You can supply your own implementation for providing sessionID (sessionIDProvider) in this field or use the default SessionID provider
	(DefaultSessionIDProvider).[Optional parameter]
* ```play.sessionstore.aerospike.sessionMaxAge``` : Specify the maximum session age until it expires(in seconds). A session expires if time elapsed equals to max age.
Expiration values:
    - -1: Never expire session
	- Greater than 0 : Actual expiration in seconds.
    By default, it is set to 1000 seconds. [Optional parameter]
* ```play.sessionstore.aerospike.transcoderFQCN``` : Specify the transcoder to be used for
	serializing and deserializing POJOs. Default implementation uses Fast Transcoder[Optional parameter]
* ```play.sessionstore.aerospike.checkAndSetMaxTries``` : Specify number of retries when
	a checkAndSetOperation fails. [Optional parameter]

### Example configuration
Add the following to your application's conf/application.conf

```
play.sessionstore.aerospike{
	hosts = [
				{
					name = "127.0.0.1"
					port = 3000
				}
				{
					name = "10.0.0.1"
					port = 3000
				}
			]
	namespace = "test"
}
```
### Modify your application's Global.java file as given below.
It is used to create a session when it does not exist.

```
import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

import com.aerospike.session.SessionStore;

public class Global extends GlobalSettings {
    private SessionStore store;

    /*
     * (non-Javadoc)
     *
     * @see play.GlobalSettings#onRequest(play.mvc.Http.Request,
     * java.lang.reflect.Method)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Action onRequest(Request request, Method actionMethod) {

        return new Action.Simple() {
            @Override
            public Promise<Result> call(Context ctx) throws Throwable {
                if (!store.exists()) {
                    store.create();
                }
                return delegate.call(ctx);
            }
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see play.GlobalSettings#onStart(play.Application)
     */
    @Override
    public void onStart(Application application) {
        store = application.injector().instanceOf(SessionStore.class);
        super.beforeStart(application);
    }

}
```

### Operations:
Using any operation will mark the current session is in use and advances the session expiration by sessionMaxAge.

* void create():
	Create a session if it does not exists.
* boolean exists():
	Indicates whether there is an active session.
* void put(String key, Object value):
	Add a key-value pair associated with the current session. If a key-value pair is already present,
	it will be overwritten.
* void putAll(Map<String, Object> map):
	Add a map of key-value pairs associated with the current session. If any key-value pair already exists
	the value will be overwritten.
* Object get(String key):
	Read the value for a key associated with current session.
* Map<String, Object> getAll():
	Read all the key-value pairs associated with the current session.
* void touch():
	Touch current session which will reset the session expiry.
* void destroy():
	Destroy all the data in the current session.
* void checkAndSet(CheckAndSetOperation operation):
	This operation allows to read all key-value pairs associated with the current session
	and invoke checkAndSetOperation atomically. checkAndSetOperation is an interface which
	allows user to plug-in his own atomic read and write operation. Please refer
	[AddNewCart](examples/sample-sessionapp/app/controllers/AddNewCart.java) and [DeleteItem](examples/sample-sessionapp/app/controllers/DeleteItem.java) classes in example sample-sessionapp which are
	are implementation of CheckAndSetOperation. The operation fails after checkAndSetMaxTries failed attempts to update atomically.

## Examples
Code example are available [here](examples).

### Session read and update

The following is a small snippet for storing a POJO ShoppingItem instance to the user's session using aerospike session store plugin and retrieving it back.

```
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

public class ShoppingCartController extends Controller {

	private final SessionStore store;

	@Inject
	public ShoppingCartController(SessionStore store) {
		super();
		this.store = store;
	}

	public Result index() {
		return ok(home.render(Form.form(ShoppingItem.class)));
	}

	public Result saveItem() throws SessionStoreException, SessionNotFound {
		Form<ShoppingItem> form = Form.form(ShoppingItem.class).bindFromRequest();
		if (form.hasErrors()) {
			return badRequest(home.render(form));
		} else {
			ShoppingItem data = form.get();
			store.put("cart", data);
			return redirect(routes.Application.index());
		}
    }

    public Result getItem() throws SessionStoreException, SessionNotFound {
	    return ok(Json.toJson(store.get("cart")));
    }
}
```

### Atomic check and set
The check and set operation allows developers to implement atomic updates to complex data types like POJOs, lists and maps stored in the session store.

This requires one to implement an idempotent CheckAndSet class that perform the update to the session data.
The input to the execute method of this class is a map having all keys and values in the user's session.
The ouput of the execute method is again a map with modified session keys and values.

This allows atomic updates that could span multiple keys for the same user session.

The following example demonstrates adding a new shopping item atomically to a shopping cart list. For details see the [sample shopping cart application](examples/sample-sessionapp).

**Add check and set operation**

```
package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import com.aerospike.session.CheckAndSetOperation;

public class AddNewItem implements CheckAndSetOperation{
	private final ShoppingItem toAdd;

    public AddNewItem(ShoppingItem toAdd) {
        this.toAdd = toAdd;
    }

	public Map<String, Object> execute(Map<String, Object> currentValues) {
		if(!currentValues.containsKey("shopping-list")) {
			currentValues.put("shopping-list", new ArrayList<ShoppingItem>());
		}
		@SuppressWarnings("unchecked")
		List<ShoppingItem> mylist = (List<ShoppingItem>) currentValues.get("shopping-list");
		if (!mylist.contains(toAdd))
			mylist.add(toAdd);
		return currentValues;
	}


}
```

**Controller code**
```
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

public class ShoppingCartController extends Controller {

	private final SessionStore store;

	@Inject
	public ShoppingCartController(SessionStore store) {
		super();
		this.store = store;
	}

	public Result index() {

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
			store.checkAndSet(new AddNewItem(data));
			return ok(addnew.render(data));
		}
	}
}
```
