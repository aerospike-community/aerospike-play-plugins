# Aerospike Session Store plugin for Play Framework 2.4.x

This plugin implements a session-store for Play Framework. This enables users to store their session 
data into Aerospike. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOS. 
The plugin provides option for using either one of two Transcoders,[Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) 
and [FasterXML-jackson dataind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

## How to Install

### Play 2.4.x:

Add the following dependency in your application's build.sbt 

```
libraryDependencies ++= Seq(
  "com.aerospike" % "aerospike-play-session-store_2.11" % "1.0"
)
```

### Configurations

* Following is the description for configuration settings.
	
	* Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings ```play.sessionstore.aerospike.hosts```[This field is necessary]
	
	* ```play.cache.aerospike.username```: Specify aerospike username. [This field is necessary]
	  
	* ```play.cache.aerospike.password```: Specify your aerospike password. [This field is necessary]

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

* Add configuration settings for Aerospike in your applications conf/application.conf file.
It must be in HOCON format (given below)

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
	username = "Aerospike"
	password = "password"
	namespace = "test"
	
}

* Modify your application's Global.java file as given below.
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

### Operations: Using any operation will mark the 
	current session is in use and reset the session expiry if set.

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
	and invoke checkAndSetOperation atomically. checkAndSetOperation is an iterface which 
	allows user to plug-in his own atomic read and write operation. Please check 
	AddNewCartItem and DeleteItem classes in example sample-sessionapp which are
	are implementation of CheckAndSetOperation. 
