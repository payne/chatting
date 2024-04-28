package org.mattpayne.demo.chatting.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatDTO {

    private Long id;

    @NotNull
    private String message;

    @NotNull
    private String response;

    @NotNull
    private Long session;

}
