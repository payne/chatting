package org.mattpayne.demo.chatting.person;

import org.mattpayne.demo.chatting.model.SimplePage;
import org.mattpayne.demo.chatting.session.Session;
import org.mattpayne.demo.chatting.session.SessionRepository;
import org.mattpayne.demo.chatting.util.NotFoundException;
import org.mattpayne.demo.chatting.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final SessionRepository sessionRepository;

    public PersonService(final PersonRepository personRepository,
            final SessionRepository sessionRepository) {
        this.personRepository = personRepository;
        this.sessionRepository = sessionRepository;
    }

    public SimplePage<PersonDTO> findAll(final String filter, final Pageable pageable) {
        Page<Person> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = personRepository.findAllById(longFilter, pageable);
        } else {
            page = personRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(person -> mapToDTO(person, new PersonDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public PersonDTO get(final Long id) {
        return personRepository.findById(id)
                .map(person -> mapToDTO(person, new PersonDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PersonDTO personDTO) {
        final Person person = new Person();
        mapToEntity(personDTO, person);
        return personRepository.save(person).getId();
    }

    public void update(final Long id, final PersonDTO personDTO) {
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(personDTO, person);
        personRepository.save(person);
    }

    public void delete(final Long id) {
        personRepository.deleteById(id);
    }

    private PersonDTO mapToDTO(final Person person, final PersonDTO personDTO) {
        personDTO.setId(person.getId());
        personDTO.setEmail(person.getEmail());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        return personDTO;
    }

    private Person mapToEntity(final PersonDTO personDTO, final Person person) {
        person.setEmail(personDTO.getEmail());
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        return person;
    }

    public boolean emailExists(final String email) {
        return personRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Person person = personRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Session personSession = sessionRepository.findFirstByPerson(person);
        if (personSession != null) {
            referencedWarning.setKey("person.session.person.referenced");
            referencedWarning.addParam(personSession.getId());
            return referencedWarning;
        }
        return null;
    }

}
