# Aerospike Session Store

This module implements a session-store for using Aerospike as a backing store. This enables users to store their session
data into Aerospike. Supported types include String, Int, Long, Boolean, BLOBs, List, Map and POJOS.
You can use either one of two Transcoders, [Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization)
and [FasterXML-jackson databind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)
for handling POJOs and complex datatypes. Please refer to [Aerospike-Transcoder](https://github.com/aerospike/aerospike-java-plugins/tree/master/transcoder) for more about serializers.

### Installation:

Requires Java 8 JDK. Use 0.9.1 version for Java 7 JDK.

Gradle:

In your gradle project, add following to your dependencies:

```
compile 'com.aerospike:aerospike-session-store:1.2'
```
Maven :

In your maven project, add the following dependency:

```
	<dependency>
		<groupId>com.aerospike</groupId>
		<artifactId>aerospike-session-store</artifactId>
		<version>1.2</version>
	</dependency>

```

Play framework :

Consider using the [session store plugin](../play-plugins/session-store-plugin) for play projects.

### Configurations

Configuration is read from ```aerospike-session-store.cfg``` in classpath. It must be in JSON format.

#### Sample configuration

```
{
  "hosts"	:[{"name":"127.0.0.1","port":3000}],
  "namespace": "test",
  "set":"users",
  "sessionMaxAge":"10000",
  "sessionIdProviderFQCN" : "com.aerospike.session.impl.DefaultSessionIDProvider",
  "checkAndSetMaxTries":"5"
 }
```

Following is the description for configuration parameters.

* ```hosts```: Specify list of aerospike endpoints/nodes for the cluster(host machines) to be used, with their
	 name and ports, using configuration settings.
* ```username```: Specify aerospike username. This parameter is optional.
* ```password```: Specify your aerospike password. The parameter is optional.
* ```namespace```: Specify aerospike namespace to be used.
* ```set```: Specify aerospike set name to be used for storing session data.
* ```sessionIdProviderFQCN```: SessionIDProvider gives unique value to each session.
	You can supply your own implementation for providing sessionID (sessionIDProvider) in this field.
* ```sessionMaxAge``` : Specify the maximum session age until it expires(in seconds). A session expires if time elapsed
equals to max age.Default is set to 1 second.
	- -1: Never expire session
	- Greater than 0 : Actual expiration in seconds.

* ```transcoderFQCN``` : Specify the transcoder to be used for
	serializing and deserializing POJOs.
* ```checkAndSetMaxTries``` : Specify number of retries when
	a checkAndSetOperation fails.

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
	AddNewCartItem and DeleteItem classes in example sample-sessionapp which are
	are implementation of CheckAndSetOperation.

### Important gradle tasks

Publishing the artifact to local maven repository.

```
gradle release publishToMavenLocal
```

Build and run tests

```
gradle build
```
