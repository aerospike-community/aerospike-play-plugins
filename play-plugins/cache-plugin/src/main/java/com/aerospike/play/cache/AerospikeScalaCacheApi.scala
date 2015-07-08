/*
* Copyright (C) 2008-2015 Aerospike, Inc.
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
package com.aerospike.play.cache;
import com.aerospike.cache.AerospikeCache
import play.api.cache.CacheApi
import play.api.inject._
import javax.inject._
import scala.reflect.ClassTag
import scala.concurrent.duration.Duration
import com.google.inject.spi.RequireAtInjectOnConstructorsOption
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import lombok.extern.java.Log

@Slf4j
@Singleton
class AerospikeScalaCacheApi @Inject()(acache: AerospikeCache ,namespace: String) extends CacheApi{
   
  def get[T:ClassTag](key: String): Option[T] = {
     if(key.isEmpty()){
       None
     }
     else{
       val ct = implicitly[ClassTag[T]]
       val any = acache.get(namespace+key)
       Option(
          any match {
            case x if ct.runtimeClass.isInstance(x) => x.asInstanceOf[T]
            case x if ct == ClassTag.Nothing => x.asInstanceOf[T]
            case x => x.asInstanceOf[T]
          }
        )
     }
}
  def getOrElse[A:ClassTag](key:String, expiration: Duration = Duration.Inf)(orElse: => A):A = {
      get[A](key).getOrElse{
        val value = orElse
        set(key,value,expiration)
        value
      }
    
  } 
  
  def set(key: String, value: Any, expiration: Duration = Duration.Inf){
    if(!key.isEmpty()){
      val exp = if(expiration.isFinite()) expiration.toSeconds.toInt else -1;
      acache.set(namespace+key, value, exp)
      
    }
  }
    
  def remove(key:String){
    if(!key.isEmpty()){
      acache.remove(namespace+key)
    }
  }
  
}