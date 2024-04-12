package kopo.poly.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NonNull;

public class MailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "to_mail")
    private Long toMail;

    @NonNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NonNull
    @Column(name = "contents", nullable = false)
    private String contents;
}
