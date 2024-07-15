package video.cache

import java.time.Duration

object Manager {

  import com.github.benmanes.caffeine.cache.{Caffeine, Cache}

  private val maxCacheSize = 100
  private val maxCacheAge = 120
  
  object CacheManager {
    
    private val cache: Cache[String, Any] = Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofMinutes(maxCacheAge))
      .maximumSize(maxCacheSize)
      .build[String, Any]()

    def get[T](key: String): Option[T] = Option(cache.getIfPresent(key).asInstanceOf[T])

    def put[T](key: String, value: T): Unit = {
      cache.put(key, value)
    }

    def invalidate = cache.invalidateAll
  }
}
