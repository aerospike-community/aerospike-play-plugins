# Aerospike Cache Plugin for Play Framework 2.4.x

This plugin implements play's internal Caching interface using Aerospike. Provides Aerospike-based Cache API
for Play Framework. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOs. 
The plugin provides option to use one of two Transcoders,[Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) 
and [FasterXML-jackson databind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features) or write your own serializer
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

## How to install

### Play 2.4.x: 

Add the following dependency in your application's build.sbt 

```
libraryDependencies ++= Seq (
  "com.aerospike" % "aerospike-play-cache_2.11" % "1.0"
) 

```
## Configurations

The default cache module (EhCache) will be used for 
non-named cache. So first we need to disable it and enable only AerospikeCacheModule to use both named and non-named
cache. You can disable the Play's default implementation by adding following to conf/application.conf in your application

```
play.modules.disabled+="play.api.cache.EhCacheModule"
```

For binding the defaultCache, add following in your application's conf/application.conf

```
play.modules.cache.defaultCache=default
```
Play 2.4 introduced namedcache for the first time. This module also supports Play 2.4 NamedCaches. To add additional namespaces besides default cache, add following 
to conf/application.conf

```
play.modules.cache.bindCaches=["db-cache", "user-cache", "session-cache"]
```



Following is the description for configuration settings.
	
* ```play.cache.aerospike.hosts```: Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings. [This field is necessary]
* ```play.cache.aerospike.username```: Specify aerospike username. [This field is necessary]
* ```play.cache.aerospike.password```: Specify your aerospike password. [This field is necessary]
* ```play.cache.aerospike.namespace```: Specify aerospike namespace to be used.[This field is necessary]
* ```play.cache.aerospike.set```: Specify aerospike set name to be used for storing cache data.  [This field is necessary]
* ```play.cache.aerospike.transcoderFQCN``` : Specify the transcoder to be used for serializing and deserializing POJOs. Default implementation uses Fast Transcoder[Optional parameter]
* ```play.cache.aerospike.bin``` : By default, the cache is stored in a bin called "cachebin" in Aerospike. If you want to rename the bin (in Aerospike), say, to 'mybin', add the following line to conf/application.conf[optional]


### Example configuration
Add the following to your applications's conf/application.conf

```
play.cache.aerospike{

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
	namespace = "cache"
	set = "cache"
	bin = "mybin"
}
```

### Examples
Example play applications using the plugin can be found in the [examples folder](examples).

#### Java

Using default cache:

```
package controllers;

public class Application extends Controller {

	@Inject
	CacheApi cache;

	public Result index() {
		cache.set("key", data);
	}
```

Using NamedCache :

```
package controllers;

public class Application extends Controller {

	@Inject
	@NamedCache("session-cache") CacheApi sessionCache;

	public Result index() {
		sessionCache.set("key", data);
	}
```
Please refer [JavaCache](https://www.playframework.com/documentation/2.4.x/JavaCache) for details.
#### Scala

Using default cache :

```
package controllers

class Application @Inject() (cache: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {
def index = Action {
    cache.set("key", data)
  }
}
```

Using NamedCache :

```
package controllers

class Application @Inject() (cache: CacheApi, @NamedCache("session-cache") sessionCache: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {
def index = Action {
    sessionCache.set("key", data)
  }
}
```
Please refer [ScalaCache](https://www.playframework.com/documentation/2.4.x/ScalaCache) for details.

### Operations

#### To store a value which never expires from Cache

```
Cache.set(key,value)
```

For setting cache value which expire after sometime

value will expire after 100 seconds.

```
Cache.set(key,value,100)
```
	
#### To retrieve the value from the cache

Get the value from the key

```
myvalue = Cache.get(key)
```

#### To retrieve a value from the cache, or set it from a default function.

Retrieve a value from the cache, or set it from a default Callable function for indefinite period

```
Cache.getOrElse(key, Callable<T> block)
```

Retrieve a value from the cache, or set it from a default Callable function for 20 seconds

```
Cache.getOrElse(key, Callable<T> block,20)
```

#### To remove the value from cache

```
Cache.remove("key")
```
