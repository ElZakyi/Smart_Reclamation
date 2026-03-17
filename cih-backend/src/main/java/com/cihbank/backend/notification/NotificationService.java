package com.cihbank.backend.notification;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.userteam.UserTeam;
import com.cihbank.backend.userteam.UserTeamRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserTeamRepository userTeamRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository,
                               UserTeamRepository userTeamRepository,
                               JavaMailSender mailSender) {

        this.notificationRepository = notificationRepository;
        this.userTeamRepository = userTeamRepository;
        this.mailSender = mailSender;
    }
    public void notifyTeam(Team team, Reclamation rec){
        List<UserTeam> member = userTeamRepository.findByTeam_IdTeam(team.getIdTeam());
        for(UserTeam ut : member){
            User user = ut.getUser();
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setSubject("Nouvelle réclamation assignée ");
            notification.setBody(
                    "Réclamation " + rec.getReference() + " assignée à votre équipe.\n\nTitre : " + rec.getTitle() + "\n\nDescription : " + rec.getDescription()
            );
            notification.setSentAt(LocalDateTime.now());
            try{
                sendEmail(user.getEmail(),notification.getSubject(),notification.getBody());
                notification.setNotificationStatus(NotificationStatus.SENT);
            }catch(Exception e){
                notification.setNotificationStatus(NotificationStatus.FAILED);
            }
            notificationRepository.save(notification);
        }
    }
    public void notifyUser(User user, String subject, String body, NotificationChannel channel){

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setSentAt(LocalDateTime.now());
        notification.setNotificationChannel(channel);

        try {

            if(channel == NotificationChannel.EMAIL){
                sendEmail(user.getEmail(), subject, body);
            }

            // plus tard tu peux ajouter :
            // if(channel == NotificationChannel.SMS){ ... }
            // if(channel == NotificationChannel.IN_APP){ ... }

            notification.setNotificationStatus(NotificationStatus.SENT);

        } catch (Exception e){

            notification.setNotificationStatus(NotificationStatus.FAILED);

        }

        notificationRepository.save(notification);
    }
    public void sendEmail(String to, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
