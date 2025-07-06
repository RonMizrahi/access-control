package com.example.template.model;

import io.github.bucket4j.Bandwidth;
import java.time.Duration;

/**
 * Subscription plans enum defining rate limiting policies for different user tiers.
 * Each plan specifies capacity and refill rate using the modern Bucket4j API.
 * 
 * @since 1.0
 */
public enum SubscriptionPlan {
    
    /**
     * Free tier: 2 requests capacity, refills 1 request per minute
     */
    FREE {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(2)
                    .refillIntervally(1, Duration.ofMinutes(1))
                    .build();
        }
    },
    
    /**
     * Basic tier: 40 requests capacity, refills 40 requests per hour
     */
    BASIC {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(40)
                    .refillIntervally(40, Duration.ofHours(1))
                    .build();
        }
    },
    
    /**
     * Professional tier: 100 requests capacity, refills 100 requests per hour
     */
    PROFESSIONAL {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(100)
                    .refillIntervally(100, Duration.ofHours(1))
                    .build();
        }
    };

    /**
     * Gets the rate limiting bandwidth configuration for this subscription plan.
     * 
     * @return Bandwidth configuration defining capacity and refill rate
     */
    public abstract Bandwidth getLimit();
}
