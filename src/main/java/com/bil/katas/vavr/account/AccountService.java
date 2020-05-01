package com.bil.katas.vavr.account;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class AccountService {

    private final UserService userService;
    private final TwitterService twitterService;
    private final BusinessLogger businessLogger;

    public String register(UUID id) {
        return createContext(id)
                .flatMap(this::registerOnTwitter)
                .flatMap(this::authenticateOnTwitter)
                .flatMap(this::tweet)
                .andThen(this::updateTwitterAccountId)
                .andThen(() -> this.businessLogger.logSuccessRegister(id))
                .onFailure(e -> this.businessLogger.logFailureRegister(id, e))
                .map(RegistrationContext::getTweetUrl)
                .getOrNull();
    }

    private Try<RegistrationContext> createContext(UUID id) {
        return getUser(id).map(RegistrationContext::new);
    }

    private Try<RegistrationContext> registerOnTwitter(RegistrationContext context) {
        return Try.of(() -> this.twitterService.register(context.getEmail(), context.getName())).map(context::setAccountId);
    }

    private Try<RegistrationContext> authenticateOnTwitter(RegistrationContext context) {
        return Try.of(() -> this.twitterService.authenticate(context.getEmail(), context.getPassword())).map(context::setTwitterToken);
    }

    private Try<RegistrationContext> tweet(RegistrationContext context) {
        return Try.of(() -> this.twitterService.tweet(context.getTwitterToken(), "Hello I am " + context.getName())).map(context::setTweetUrl);
    }

    private void updateTwitterAccountId(RegistrationContext context) {
        Try.run(() -> this.userService.updateTwitterAccountId(context.getId(), context.getAccountId()));
    }

    private Try<User> getUser(UUID id) {
        return Try.of(() -> this.userService.findById(id));
    }

}