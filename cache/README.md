# Aerospike Cache plugin

This plugin implements Caching interface using Aerospike. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOs. 
You can use either one of two Transcoders,[Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) 
and [FasterXML-jackson dataind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features) or write your own serializer
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

### Installation:

Gradle:

In your gradle project, add following to your dependencies:

```
compile 'com.aerospike:aerospike-cache:O.9'
```
Maven : 

In your maven project, add the following dependency:

```
<dependency>
	<groupId>com.aerospike</groupId>
	<artifactId>aerospike-cache</artifactId>
	<version>0.9</version>
</dependency>

```

SBT Project :

In your Play project, add the following dependency

```
libraryDependencies += "com.aerospike" % "aerospike-cache" % "0.9"
```

## Configurations

Following is the description for configuration settings.
	
* ```hosts```: Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings. 
* ```username```: Specify aerospike username. 
* ```password```: Specify your aerospike password. 
* ```namespace```: Specify aerospike namespace to be used.
* ```set```: Specify aerospike set name to be used for storing cache data.  
* ```transcoderFQCN``` : Specify the transcoder to be used for serializing and deserializing POJOs. 
* ```bin`` : Specify the aerospike-bin name to be used for storing cache data. If you want to rename the bin (in Aerospike),
 say, to 'mybin', add the following line to conf/application.conf


### Example configuration
You can specify the name of configuration file to be used using configReader's getConfiguration method. 
By default, configuration is read from /aerospike-cache/src/test/resources/aerospike-cache.cfg

```
{
  "hosts"	:[{"name":"127.0.0.1","port":3000}],
  "username" : "Aerospike",
  "password" : "password",
  "namespace": "test",
  "set":"users",
  "bin":"mybin",
  "transcoderFQCN":"com.aerospike.transcoder.fst.FstTranscoder"
 }
```

### Code examples are provided in examples folder.

#### Java

```
import com.aerospike.cache.AerospikeCache

public class MyCache {

	@Inject
	CacheApi cache;

	public Result index() {
		cache.set("key", data);
	}
```
#### Scala

```
import com.aerospike.cache.AerospikeCache

class Application @Inject() (cache: CacheApi, val messagesApi: MessagesApi) extends Controller with I18nSupport {
def index = Action {
    cache.set("key", data)
  }
}
```

### Operations

#### To store a value which expires from Cache after 100 seconds, use expiration -1.

```
Cache.set(key,value,100)
```
	
#### To retrieve the value from the cache 

```
myvalue = Cache.get(key)
```

#### Retrieve a value from the cache, or set it from a default Callable function for some period,say, 20 seconds.

```
Cache.getOrElse(key, Callable<T> block,20)
```

#### To remove the value from cache

```
Cache.remove("key")
```
