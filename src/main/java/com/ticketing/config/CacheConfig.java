package com.ticketing.config;

//package com.ticketing.config;
//For Ehcache (a more robust local cache): 

//You'd also need an ehcache.xml file in src/main/resources
//Example ehcache.xml:
/*
<config
 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
 xmlns='http://www.ehcache.org/v3'
 xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.10.xsd">

 <cache-template name="default">
     <expiry>
         <ttl unit="seconds">3600</ttl>
     </expiry>
     <heap unit="entries">1000</heap>
 </cache-template>

 <cache alias="attachments" uses-template="default"/>
 <cache alias="tickets" uses-template="default"/>
 <cache alias="users" uses-template="default"/>
</config>
*/



//package com.ticketing.config;
//
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.jsr107.EhcacheCachingProvider;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.jcache.JCacheCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.cache.Caching;
//import javax.cache.spi.CachingProvider;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//@Configuration
//public class CacheConfig {
//
// @Bean
// public CacheManager cacheManager() throws URISyntaxException {
//     // Use EhcacheCachingProvider for JCache (JSR-107) integration
//     CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
//     javax.cache.CacheManager ehcacheManager = cachingProvider.getCacheManager(
//         new URI("classpath:/ehcache.xml"), // Path to your ehcache.xml
//         getClass().getClassLoader()
//     );
//     return new JCacheCacheManager(ehcacheManager);
// }
//}


import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching; // Don't forget this!

@Configuration
@EnableCaching // Make sure caching is enabled for the application context
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Define all the cache names used across your services
        // Spring will create a ConcurrentMapCache for each name specified here.
        return new ConcurrentMapCacheManager(
            "departments",
            "projects",
            "userDetails",
            "users",
            "allUsers",
            "usersByDepartmentAndProject",
            "roles", 
            "rolesByUser" 
        );
    }
}

