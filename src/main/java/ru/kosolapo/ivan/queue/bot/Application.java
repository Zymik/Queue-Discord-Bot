package ru.kosolapo.ivan.queue.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
       /* DiscordClient client = DiscordClient.create("");
        AddToQueue t = new AddToQueue();
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // ReadyEvent example

            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                            }))
                    .then();

            // MessageCreateEvent example
            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().startsWith("!!add")) {
                    return t.add(event);
                }
				return Mono.empty();
            }).then();

            // combine them!
            return printOnLogin.and(handlePingCommand);
        });

        login.block();*/
        SpringApplication.run(Application.class, args);
    }

}
