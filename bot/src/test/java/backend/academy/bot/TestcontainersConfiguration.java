package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import static org.mockito.ArgumentMatchers.any;

// isolated from the "scrapper" module's containers!
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

//    @Bean
//    @RestartScope
//    @ServiceConnection(name = "redis")
//    GenericContainer<?> redisContainer() {
//        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
//    }
//
//    @Bean
//    @RestartScope
//    @ServiceConnection
//    KafkaContainer kafkaContainer() {
//        return new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);
//    }

}
