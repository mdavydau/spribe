package com.mdavydau.spribe.job;

import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Profile("local")
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingPaymentTimeoutJob {

    private final BookingRepository bookingRepository;

    @Scheduled(initialDelayString = "${timeout.job.initialDelay}", fixedRateString = "${timeout.job.fixedRate}", timeUnit = TimeUnit.MINUTES)
    public void startPaymentTimeoutJob() {
        log.info("Starting booking payment timeout job");
        bookingRepository.findAllByStatusAndCreatedIsBefore(BookingStatus.PENDING,
                        LocalDateTime.now().minusMinutes(15))
                .forEach(b -> {
                    log.info("Booking {} status has been updated from {} to  {}", b.getId(), b.getStatus(), BookingStatus.PAYMENT_TIMEOUT);
                    b.setStatus(BookingStatus.PAYMENT_TIMEOUT);
                    bookingRepository.save(b);
                });
    }
}
