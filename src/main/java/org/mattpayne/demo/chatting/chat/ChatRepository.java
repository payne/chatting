package org.mattpayne.demo.chatting.chat;

import org.mattpayne.demo.chatting.session.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findAllById(Long id, Pageable pageable);

    Chat findFirstBySession(Session session);

}
