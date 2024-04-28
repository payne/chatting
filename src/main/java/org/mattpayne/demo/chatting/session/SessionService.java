package org.mattpayne.demo.chatting.session;

import org.mattpayne.demo.chatting.chat.Chat;
import org.mattpayne.demo.chatting.chat.ChatRepository;
import org.mattpayne.demo.chatting.model.SimplePage;
import org.mattpayne.demo.chatting.person.Person;
import org.mattpayne.demo.chatting.person.PersonRepository;
import org.mattpayne.demo.chatting.util.NotFoundException;
import org.mattpayne.demo.chatting.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final PersonRepository personRepository;
    private final ChatRepository chatRepository;

    public SessionService(final SessionRepository sessionRepository,
            final PersonRepository personRepository, final ChatRepository chatRepository) {
        this.sessionRepository = sessionRepository;
        this.personRepository = personRepository;
        this.chatRepository = chatRepository;
    }

    public SimplePage<SessionDTO> findAll(final String filter, final Pageable pageable) {
        Page<Session> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = sessionRepository.findAllById(longFilter, pageable);
        } else {
            page = sessionRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(session -> mapToDTO(session, new SessionDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public SessionDTO get(final Long id) {
        return sessionRepository.findById(id)
                .map(session -> mapToDTO(session, new SessionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SessionDTO sessionDTO) {
        final Session session = new Session();
        mapToEntity(sessionDTO, session);
        return sessionRepository.save(session).getId();
    }

    public void update(final Long id, final SessionDTO sessionDTO) {
        final Session session = sessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sessionDTO, session);
        sessionRepository.save(session);
    }

    public void delete(final Long id) {
        sessionRepository.deleteById(id);
    }

    private SessionDTO mapToDTO(final Session session, final SessionDTO sessionDTO) {
        sessionDTO.setId(session.getId());
        sessionDTO.setStartedAt(session.getStartedAt());
        sessionDTO.setPerson(session.getPerson() == null ? null : session.getPerson().getId());
        return sessionDTO;
    }

    private Session mapToEntity(final SessionDTO sessionDTO, final Session session) {
        session.setStartedAt(sessionDTO.getStartedAt());
        final Person person = sessionDTO.getPerson() == null ? null : personRepository.findById(sessionDTO.getPerson())
                .orElseThrow(() -> new NotFoundException("person not found"));
        session.setPerson(person);
        return session;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Session session = sessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Chat sessionChat = chatRepository.findFirstBySession(session);
        if (sessionChat != null) {
            referencedWarning.setKey("session.chat.session.referenced");
            referencedWarning.addParam(sessionChat.getId());
            return referencedWarning;
        }
        return null;
    }

}
