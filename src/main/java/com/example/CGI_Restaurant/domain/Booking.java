package com.example.CGI_Restaurant.domain;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Column(name = "guest_email", nullable = false)
    private String guestEmail;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "party_size", nullable = false)
    private int partySize;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status;

    @Column(name = "qr_token", nullable = false)
    private String qrToken;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "booking")
    private List<BookingPreference> bookingPreferences = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    private List<BookingTable> bookingTables = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
