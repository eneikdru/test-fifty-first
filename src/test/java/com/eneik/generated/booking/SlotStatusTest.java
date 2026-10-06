package com.eneik.generated.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotStatusTest {

    @Test
    void testValidTransitions() {
        assertTrue(SlotStatus.FREE.canTransitionTo(SlotStatus.HELD));
        assertTrue(SlotStatus.FREE.canTransitionTo(SlotStatus.CANCELLED));

        assertTrue(SlotStatus.HELD.canTransitionTo(SlotStatus.BOOKED));
        assertTrue(SlotStatus.HELD.canTransitionTo(SlotStatus.FREE));
        assertTrue(SlotStatus.HELD.canTransitionTo(SlotStatus.CANCELLED));

        assertTrue(SlotStatus.BOOKED.canTransitionTo(SlotStatus.NO_SHOW));
        assertTrue(SlotStatus.BOOKED.canTransitionTo(SlotStatus.CANCELLED));
    }

    @Test
    void testInvalidTransitions() {
        assertFalse(SlotStatus.FREE.canTransitionTo(SlotStatus.BOOKED));
        assertFalse(SlotStatus.FREE.canTransitionTo(SlotStatus.NO_SHOW));

        assertFalse(SlotStatus.HELD.canTransitionTo(SlotStatus.NO_SHOW));

        assertFalse(SlotStatus.BOOKED.canTransitionTo(SlotStatus.FREE));
        assertFalse(SlotStatus.BOOKED.canTransitionTo(SlotStatus.HELD));

        assertFalse(SlotStatus.NO_SHOW.canTransitionTo(SlotStatus.FREE));
        assertFalse(SlotStatus.NO_SHOW.canTransitionTo(SlotStatus.BOOKED));

        assertFalse(SlotStatus.CANCELLED.canTransitionTo(SlotStatus.FREE));
        assertFalse(SlotStatus.CANCELLED.canTransitionTo(SlotStatus.HELD));
    }

    @Test
    void testSelfTransition() {
        assertTrue(SlotStatus.FREE.canTransitionTo(SlotStatus.FREE));
        assertTrue(SlotStatus.HELD.canTransitionTo(SlotStatus.HELD));
        assertTrue(SlotStatus.BOOKED.canTransitionTo(SlotStatus.BOOKED));
    }

    @Test
    void testNullTransition() {
        assertFalse(SlotStatus.FREE.canTransitionTo(null));
    }
}
