# Aerospike Cache

This module implements caching interface using Aerospike. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOs. 
You can use either one of two Transcoders,[Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) 
and [FasterXML-jackson databind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features) or write your own serializer
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

### Installation:

Requires Java 8 JDK. Use 0.9.1 version for Java 7 JDK.

Gradle:

In your gradle project, add following to your dependencies:

```
compile 'com.aerospike:aerospike-cache:1.1'
```
Maven : 

In your maven project, add the following dependency:

```
<dependency>
	<groupId>com.aerospike</groupId>
	<artifactId>aerospike-cache</artifactId>
	<version>1.1</version>
</dependency>

```

Play framework :

Consider using the (cache plugin)[play-plugins/cache-plugin] for play projects.

## Configurations

Configuration is read from ```aerospike-cache.cfg``` in classpath. It must be in JSON format.

#### Sample Configuration
```
{
  "hosts"	:[{"name":"127.0.0.1","port":3000}],
  "username" : "aerospike",
  "password" : "password",
  "namespace": "test",
  "set":"users",
  "bin":"mybin",
  "transcoderFQCN":"com.aerospike.transcoder.fst.FstTranscoder"
 }
```


Following is the description for configuration parameters.
	
* ```hosts```: Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings. 
* ```username```: Specify aerospike username. 
* ```password```: Specify your aerospike password. 
* ```namespace```: Specify aerospike namespace to be used.
* ```set```: Specify aerospike set name to be used for storing cache data.  
* ```transcoderFQCN``` : Specify the transcoder to be used for serializing and deserializing POJOs. 
* ```bin``` : Specify the aerospike-bin name to be used for storing cache data. If you want to rename the bin (in Aerospike),
 say, to 'mybin', add the following line to conf/application.conf



### Code examples are provided in examples folder.

#### Java

```
import com.aerospike.cache.AerospikeCache

public class MyCache {

	@Inject
	AerospikeCache cache;

	public Result index() {
		cache.set("key", data);
	}
```

### Operations

#### To store a value which expires from Cache after 100 seconds, use expiration -1.

```
cache.set(key,value,100)
```
	
#### To retrieve the value from the cache 

```
myvalue = cache.get(key)
```

#### Retrieve a value from the cache, or set it from a default Callable function for some period,say, 20 seconds.

```
cache.getOrElse(key, Callable<T> block,20)
```

#### To remove the value from cache

```
cache.remove("key")
```

### Important gradle tasks

Publishing the artifact to local maven repository.

```
gradle release publishToMavenLocal
```

Build and run tests

```
gradle build
```
