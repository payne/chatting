package org.mattpayne.demo.chatting.session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.mattpayne.demo.chatting.model.SimplePage;
import org.mattpayne.demo.chatting.person.Person;
import org.mattpayne.demo.chatting.person.PersonRepository;
import org.mattpayne.demo.chatting.util.CustomCollectors;
import org.mattpayne.demo.chatting.util.ReferencedException;
import org.mattpayne.demo.chatting.util.ReferencedWarning;
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
@RequestMapping(value = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class SessionResource {

    private final SessionService sessionService;
    private final PersonRepository personRepository;

    public SessionResource(final SessionService sessionService,
            final PersonRepository personRepository) {
        this.sessionService = sessionService;
        this.personRepository = personRepository;
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
    public ResponseEntity<SimplePage<SessionDTO>> getAllSessions(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(sessionService.findAll(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(sessionService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSession(@RequestBody @Valid final SessionDTO sessionDTO) {
        final Long createdId = sessionService.create(sessionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSession(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SessionDTO sessionDTO) {
        sessionService.update(id, sessionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSession(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = sessionService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/personValues")
    public ResponseEntity<Map<Long, String>> getPersonValues() {
        return ResponseEntity.ok(personRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Person::getId, Person::getEmail)));
    }

}
