package com.aerospike.play.cache

import play.api.Configuration
import play.api.inject.{BindingKey, Injector}
import javax.inject.{Inject,Singleton, Provider}
import com.aerospike.cache._

@Singleton
class AerospikeCacheApiProvider(namespace: String) extends Provider[AerospikeScalaCacheApi] {
 	@Inject private var injector: Injector = _
 	
 	lazy val get: AerospikeScalaCacheApi = {
 		 new AerospikeScalaCacheApi(injector.instanceOf[AerospikeCache], namespace);
 	}
}