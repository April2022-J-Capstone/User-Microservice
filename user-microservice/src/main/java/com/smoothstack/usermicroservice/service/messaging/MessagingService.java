package com.smoothstack.usermicroservice.service.messaging;

import com.smoothstack.usermicroservice.exceptions.SendMsgFailureException;

import org.springframework.stereotype.Service;

@Service
public interface MessagingService {
    void sendEmail(String email, String subject, String htmlBody) throws SendMsgFailureException;
    void sendSMS(String phone, String message) throws SendMsgFailureException;
}
