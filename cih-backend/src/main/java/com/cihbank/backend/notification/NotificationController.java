package com.cihbank.backend.notification;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/user/{idUser}")
    public List<Notification> getUserNotifications(@PathVariable Integer idUser){

        return notificationRepository.findByUserIdUser(idUser);

    }
}