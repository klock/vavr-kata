package com.bil.katas.vavr.account;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class RegistrationContext {

    private final String email;
    private final UUID id;
    private String password;
    private String name;
    private String accountId;
    private String twitterToken;
    private String tweetUrl;

    public RegistrationContext(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        name = user.getName();
        password = user.getPassword();
    }

}
