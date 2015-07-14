# Aerospike Plugin for Play Framework 2.4.x

This plugin implements play's internal Caching interface using Aerospike. Provides Aerospike-based Cache API
for Play Framework. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOS. 
The plugin provides option for using either one of two Transcoders,[Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) 
and [FasterXML-jackson dataind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features) 
for handling POJOs and complex datatypes. Please refer to this [documentation]

## How to install

* add play = 2.4.x:
to your dependencies in your applications build.sbt file

* Play 2.4 introduced namedcache for the first time. The default cache module (EhCache) will be used for all 
non-named cache. So first we need to disale it and enable only AerospikeCacheModule to use both named and non-named
cache. You can disable the Play's default implementation by adding following to conf/application.conf in your application

```
play.modules.disabled+="play.api.cache.EhCacheModule"
```

* This module also supports Play 2.4 NamedCaches. To add additional namespaces besides default cache, add following 
to conf/application.conf

```
play.cache.redis.bindCaches = ["db-cache", "user-cache", "session-cache"]
```

## Configurable


* Add configuration settings for Aerospike from your application's application.conf file.
It must be provided in HOCON format[required]

#### conf/application.conf


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
	namespace = "test"
	set = "users"
}


* By default, [Fast Transcoder] is used for serializing and deserializing. User can optionally use [Jackson Transcoder] by adding follwing additional line in conf/application.conf[optional]


#### Default-Fast Transcoder

```
transcoderFQCN = "com.aerospike.transcoder.fst.FstTranscoder"
```

#### Jackson Transcoder
```
play.cache.aerospike{
transcoderFQCN = "com.aerospike.transcoder.jackson.JacksonTranscoder"
}
```

* By default, the cache is stored in a bin called "cachebin" in Aerospike. If you want to rename the bin (in Aerospike), say, to 'mybin', add the following line to conf/application.conf[optional]

```
play.cache.aerospike{
	bin = "mybin"
}
```
### Code examples are provided in examples folder.

### For java, you can use play.cache.Cache object to store value in Cache. Please refer [JavaCache](https://www.playframework.com/documentation/2.4.x/JavaCache)

### For Scala, you can use play.cache.Cache object to store value in Cache. Please refer [ScalaCache](https://www.playframework.com/documentation/2.4.x/ScalaCache)

### Operations

* To store a value which never expires from Cache

```
Cache.set(key,value)
```

For setting cache value which expire after sometime

value will expire after 100 seconds

```
Cache.set(key,value,100)
```

* To retrieve the value from the cache

Get the value from the key

```
myvalue = Cache.get(key)
```

* To retrieve a value from the cache, or set it from a default function.

Retrieve a value from the cache, or set it from a default Callable function for indefinite period

```
Cache.getOrElse(key, Callable<T> block)
```

Retrieve a value from the cache, or set it from a default Callable function for 20 seconds

```
Cache.getOrElse(key, Callable<T> block,20)
```

* To remove the value from cache

```
Cache.remove("key")
```




* For using NamedCaches, please refer to sample examples in example/ folder
