package org.mattpayne.demo.chatting.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PersonRepository extends JpaRepository<Person, Long> {

    Page<Person> findAllById(Long id, Pageable pageable);

    boolean existsByEmailIgnoreCase(String email);

}
