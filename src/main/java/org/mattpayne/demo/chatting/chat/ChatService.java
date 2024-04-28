package org.mattpayne.demo.chatting.chat;

import org.mattpayne.demo.chatting.model.SimplePage;
import org.mattpayne.demo.chatting.session.Session;
import org.mattpayne.demo.chatting.session.SessionRepository;
import org.mattpayne.demo.chatting.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;

    public ChatService(final ChatRepository chatRepository,
            final SessionRepository sessionRepository) {
        this.chatRepository = chatRepository;
        this.sessionRepository = sessionRepository;
    }

    public SimplePage<ChatDTO> findAll(final String filter, final Pageable pageable) {
        Page<Chat> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = chatRepository.findAllById(longFilter, pageable);
        } else {
            page = chatRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ChatDTO get(final Long id) {
        return chatRepository.findById(id)
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ChatDTO chatDTO) {
        final Chat chat = new Chat();
        mapToEntity(chatDTO, chat);
        return chatRepository.save(chat).getId();
    }

    public void update(final Long id, final ChatDTO chatDTO) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(chatDTO, chat);
        chatRepository.save(chat);
    }

    public void delete(final Long id) {
        chatRepository.deleteById(id);
    }

    private ChatDTO mapToDTO(final Chat chat, final ChatDTO chatDTO) {
        chatDTO.setId(chat.getId());
        chatDTO.setMessage(chat.getMessage());
        chatDTO.setResponse(chat.getResponse());
        chatDTO.setSession(chat.getSession() == null ? null : chat.getSession().getId());
        return chatDTO;
    }

    private Chat mapToEntity(final ChatDTO chatDTO, final Chat chat) {
        chat.setMessage(chatDTO.getMessage());
        chat.setResponse(chatDTO.getResponse());
        final Session session = chatDTO.getSession() == null ? null : sessionRepository.findById(chatDTO.getSession())
                .orElseThrow(() -> new NotFoundException("session not found"));
        chat.setSession(session);
        return chat;
    }

}
