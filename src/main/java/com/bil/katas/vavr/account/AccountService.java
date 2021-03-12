package com.bil.katas.vavr.account;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@AllArgsConstructor
public class AccountService {

    private final UserService userService;
    private final TwitterService twitterService;
    private final BusinessLogger businessLogger;

    private Try<TwitterRegistrationContext> createContext(UUID id) {
        return Try.of(() ->
                this.userService.findById(id))
                .map(TwitterRegistrationContext::new);
    }

    private Try<TwitterRegistrationContext> registerOnTwitter(TwitterRegistrationContext context) {
        return Try.of(() ->
                this.twitterService.register(context.getEmail(), context.getName()))
                .map(context::setAccountId);
    }

    private Try<TwitterRegistrationContext> authenticateOnTwitter(TwitterRegistrationContext context) {
        return Try.of(() ->
                this.twitterService.authenticate(context.getEmail(), context.getPassword()))
                .map(context::setTweeterToken);
    }

    private Try<TwitterRegistrationContext> tweet(TwitterRegistrationContext context) {
        return Try.of(() ->
                this.twitterService.tweet(context.getTweeterToken(), "Hello I am " + context.getName()))
                .map(context::setTweetUrl);
    }

    private void updateUser(TwitterRegistrationContext context) {
        Try.run(() ->
                this.userService.updateTwitterAccountId(context.getId(), context.getAccountId()));
    }

    public Option<String> register(UUID id) {
        return createContext(id)
                .flatMap(this::registerOnTwitter)
                .flatMap(this::authenticateOnTwitter)
                .flatMap(this::tweet)
                .andThen(this::updateUser)
                .andThen(context -> this.businessLogger.logSuccessRegister(id))
                .onFailure(exception -> this.businessLogger.logFailureRegister(id, exception))
                .map(TwitterRegistrationContext::getTweetUrl)
                .toOption();
    }
}

@Data
@Accessors(chain = true)
class TwitterRegistrationContext {
    private UUID id;
    private String email, name, password;
    private String accountId, tweeterToken,  tweetUrl;

    public TwitterRegistrationContext(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
    }
}
