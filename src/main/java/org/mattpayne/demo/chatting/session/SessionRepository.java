package org.mattpayne.demo.chatting.session;

import org.mattpayne.demo.chatting.person.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepository extends JpaRepository<Session, Long> {

    Page<Session> findAllById(Long id, Pageable pageable);

    Session findFirstByPerson(Person person);

}
