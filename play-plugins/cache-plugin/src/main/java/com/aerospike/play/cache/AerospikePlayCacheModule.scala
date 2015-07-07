package com.aerospike.play.cache

import javax.inject.{Inject,Provider,Singleton}
import play.api.cache.{CacheApi,Cached,NamedCache}
import play.api.inject._
import play.api.{Configuration, Environment}
import play.cache.{CacheApi => JavaCacheApi, DefaultCacheApi => DefaultJavaCacheApi, NamedCacheImpl}
import com.aerospike.cache.AerospikeCacheConfig
import com.aerospike.cache.AerospikeClientModule

class JavaCacheApiProvider(bindkey: BindingKey[CacheApi]) extends Provider[JavaCacheApi] {
  @Inject private var injector: Injector = _
  lazy val get: JavaCacheApi = {
    new DefaultJavaCacheApi(injector.instanceOf(bindkey))
  }
}

class CachedProvider(bindkey: BindingKey[CacheApi]) extends Provider[Cached]{
  @Inject private var injector: Injector = _
  lazy val get: Cached = {
    new Cached(injector.instanceOf(bindkey))
  }
}

@Singleton
class AerospikePlayCacheModule extends Module{
  import scala.collection.JavaConversions._
  
  override def bindings(environment: Environment, configuration:Configuration): Seq[Binding[_]]={
    
    val defaultCacheName = configuration.underlying.getString("play.modules.cache.defaultCache")
    val bindCaches = configuration.underlying.getStringList("play.modules.cache.bindCaches").toSeq
  
    def named(name: String): NamedCache = {
      new NamedCacheImpl(name)  
    }
    

   def bindCache(name: String) = {
     val namedCache = named(name)
     val cacheApiKey = bind[CacheApi].qualifiedWith(namedCache)
     Seq(
       cacheApiKey.to(new AerospikeCacheApiProvider(name)),
       bind[JavaCacheApi].qualifiedWith(namedCache).to(new JavaCacheApiProvider(cacheApiKey)),
       bind[Cached].qualifiedWith(namedCache).to(new CachedProvider(cacheApiKey))
     )
   }
    
  Seq(
      bind[CacheApi].to(bind[CacheApi].qualifiedWith(named(defaultCacheName))),
      bind[JavaCacheApi].to[DefaultJavaCacheApi]
      )++ bindCache(defaultCacheName) ++ bindCaches.flatMap (bindCache)

  }
}