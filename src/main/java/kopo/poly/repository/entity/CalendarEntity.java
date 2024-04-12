package kopo.poly.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CALENDAR_INFO")
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Cacheable

public class CalendarEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_seq")
    private Long calendarSeq;

    @NonNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NonNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NonNull
    @Column(name = "start", nullable = false)
    private String start;

    @NonNull
    @Column(name = "end", nullable = false)
    private String end;

    @NonNull
    @Column(name = "description", length = 2000, nullable = false)
    private String description;
}