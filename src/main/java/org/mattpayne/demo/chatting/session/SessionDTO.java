package org.mattpayne.demo.chatting.session;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SessionDTO {

    private Long id;

    @NotNull
    private LocalDateTime startedAt;

    @NotNull
    private Long person;

}
