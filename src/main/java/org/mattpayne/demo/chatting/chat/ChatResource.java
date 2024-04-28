package org.mattpayne.demo.chatting.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.mattpayne.demo.chatting.model.SimplePage;
import org.mattpayne.demo.chatting.session.Session;
import org.mattpayne.demo.chatting.session.SessionRepository;
import org.mattpayne.demo.chatting.util.CustomCollectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatResource {

    private final ChatService chatService;
    private final SessionRepository sessionRepository;

    public ChatResource(final ChatService chatService, final SessionRepository sessionRepository) {
        this.chatService = chatService;
        this.sessionRepository = sessionRepository;
    }

    @Operation(
            parameters = {
                    @Parameter(
                            name = "page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "size",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = Integer.class)
                    ),
                    @Parameter(
                            name = "sort",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = String.class)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<SimplePage<ChatDTO>> getAllChats(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(chatService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> getChat(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(chatService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createChat(@RequestBody @Valid final ChatDTO chatDTO) {
        final Long createdId = chatService.create(chatDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChat(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ChatDTO chatDTO) {
        chatService.update(id, chatDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteChat(@PathVariable(name = "id") final Long id) {
        chatService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessionValues")
    public ResponseEntity<Map<Long, Long>> getSessionValues() {
        return ResponseEntity.ok(sessionRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Session::getId, Session::getId)));
    }

}
