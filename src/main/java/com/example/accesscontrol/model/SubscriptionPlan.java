package com.example.accesscontrol.model;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import java.time.Duration;

// Can be injected
public enum SubscriptionPlan {
    FREE {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(2, Refill.intervally(1, Duration.ofMinutes(1)));
        }
    },
    BASIC {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(40, Refill.intervally(40, Duration.ofHours(1)));
        }
    },
    PROFESSIONAL {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.classic(100, Refill.intervally(100, Duration.ofHours(1)));
        }
    };

    public abstract Bandwidth getLimit();
}
