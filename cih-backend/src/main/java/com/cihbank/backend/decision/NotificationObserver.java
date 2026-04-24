package com.cihbank.backend.decision;

import com.cihbank.backend.decision.NotificationEvent;
import com.cihbank.backend.notification.NotificationChannel;
import com.cihbank.backend.notification.NotificationService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationObserver {

    private final NotificationService notificationService;
    private final ReclamationRepository reclamationRepository;

    public NotificationObserver(NotificationService notificationService,
                                ReclamationRepository reclamationRepository) {
        this.notificationService = notificationService;
        this.reclamationRepository = reclamationRepository;
    }

    @EventListener
    public void handleNotification(NotificationEvent event) {

        Reclamation rec = reclamationRepository
                .findById(event.getIdReclamation())
                .orElseThrow();

        notificationService.notifyUser(
                rec.getUser(),
                "Notification",
                event.getMessage(),
                NotificationChannel.EMAIL
        );
    }
}