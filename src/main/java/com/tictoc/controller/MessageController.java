package com.tictoc.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tictoc.auth.MyUser;
import com.tictoc.dto.MessageDTO;
import com.tictoc.repository.MessageRepository;
import com.tictoc.service.MessageService;

@RestController
public class MessageController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private MessageService messageService;
	@Autowired
	MessageRepository repository;

	@MessageMapping("/messages")
	public void getMessageByUserFrom(@Payload(required = false) String userName) {
		if (userName == null) {
			return;
		}
		simpMessagingTemplate.convertAndSendToUser(userName, "/queue/messages",
				messageService.getMessageByUserFrom(userName));
	}

	@MessageMapping("/messages.sendMessage")
	public void sendMessage(@Header String userName, @Payload String Content) {
		MessageDTO message = messageService.sendMessage(Content, userName);
		simpMessagingTemplate.convertAndSendToUser(userName, "/queue/message", message);
	}

	@MessageMapping("/messages.clear")
	public void clearMessage(@Payload String userName) {
		messageService.clear(userName);
		simpMessagingTemplate.convertAndSendToUser(userName, "/queue/messages",
				messageService.getMessageByUserFrom(userName));
	}

	@MessageMapping("/messages.chatroom")
	public void getSuggestedUser(@Payload String userNameTo, Principal principal) {
//		Principal cc = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(((MyUser)((UsernamePasswordAuthenticationToken)principal).getPrincipal()).getUser().getId());
//		simpMessagingTemplate.convertAndSendToUser(userNameTo, "/queue/chatroom", messageService.getChatRoom(userNameTo));
		
	}
	
	@GetMapping("/vv")
	public ResponseEntity<?> getSuggestedUsr() {

		return new ResponseEntity<>(repository.findMessageByUserFrom(3l, PageRequest.of(0, 1000, Sort.Direction.DESC, "createddate")), HttpStatus.OK);
	}
}