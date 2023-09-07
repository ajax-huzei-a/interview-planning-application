package intellistart.interviewplanning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * Configuration Jedis connection to Redis.
 */
@Configuration
public class JedisConfig {

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private Integer port;

  @Bean
  public JedisPooled getJedisConnection() {
    return new JedisPooled(host, port);
  }
}
