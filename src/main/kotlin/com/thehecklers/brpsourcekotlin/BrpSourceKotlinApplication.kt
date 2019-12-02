package com.thehecklers.brpsourcekotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.support.MessageBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*
import java.util.function.Supplier
import kotlin.random.Random

@SpringBootApplication
class BrpSourceKotlinApplication

fun main(args: Array<String>) {
    runApplication<BrpSourceKotlinApplication>(*args)
}

/*
@Configuration
class GateAgent(private val generator: PassengerGenerator) {
    @Bean
    fun checkIn() = {
        Flux.interval(Duration.ofSeconds(1))
            .onBackpressureDrop()
            .map { generator.generate() }
    }
}
*/

@Configuration
class GateAgent(private val generator: PassengerGenerator) {
    @Bean
//    MH: This doesn't work...yet ;)
//    fun checkIn(): () -> Flux<Passenger> {
//        return {
    fun checkIn(): Supplier<Flux<Passenger>> = Supplier {
        Flux.interval(Duration.ofSeconds(1))
            .onBackpressureDrop()
            .map { generator.generate() }
    }

}

/*
MH: This works too (toggle settings in application.properties too)
@EnableBinding(Source::class)
@EnableScheduling
class GateAgent(private val source: Source, private val generator: PassengerGenerator) {
    @Scheduled(fixedRate = 1000)
    fun sendPassenger() =
        source.output().send(MessageBuilder.withPayload(generator.generate()).build())
}
*/

@Component
class PassengerGenerator() {
    val names = listOf("Ay", "Bea", "Cee", "Dee")
    val rnd = Random

    fun generate() = Passenger(
        UUID.randomUUID().toString(),
        names[rnd.nextInt(names.size)]
    )
}

data class Passenger(val id: String, val name: String)