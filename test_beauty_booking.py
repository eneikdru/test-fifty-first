import unittest
import threading
from datetime import datetime, timedelta
from beauty_booking import (
    Master,
    Appointment,
    AppointmentStatus,
    InvalidPhoneError,
    InvalidTransitionError,
    DoubleBookingError
)

class TestBeautyBooking(unittest.TestCase):

    def test_georgian_phone_validation_valid(self):
        # Valid rigid format: +995 5XX XX XX XX
        valid_phones = [
            "+995 555 12 34 56",
            "+995 599 00 11 22"
        ]
        start = datetime(2025, 1, 1, 10, 0)
        end = datetime(2025, 1, 1, 11, 0)

        for phone in valid_phones:
            try:
                appt = Appointment(start, end, phone)
                self.assertEqual(appt.client_phone, phone)
            except Exception as e:
                self.fail(f"Valid phone {phone} raised an exception: {e}")

    def test_georgian_phone_validation_invalid(self):
        invalid_phones = [
            "+995555123456",        # No spaces
            "+995 555 123456",      # Missing some spaces
            "+995 455 12 34 56",    # Starts with 4 instead of 5
            "+1 555 12 34 56",      # Wrong country code
            "+995 555 12 34 567",   # Too long
            "995 555 12 34 56"      # Missing +
        ]
        start = datetime(2025, 1, 1, 10, 0)
        end = datetime(2025, 1, 1, 11, 0)

        for phone in invalid_phones:
            with self.assertRaises(InvalidPhoneError):
                Appointment(start, end, phone)

    def test_negative_deontic_gate_cancellation(self):
        start = datetime(2025, 1, 1, 10, 0)
        end = datetime(2025, 1, 1, 11, 0)
        appt = Appointment(start, end, "+995 555 12 34 56")

        # Valid transition
        appt.complete()
        self.assertEqual(appt.status, AppointmentStatus.COMPLETED)

        # Invalid transition: COMPLETED -> CANCELLED
        with self.assertRaises(InvalidTransitionError):
            appt.cancel()

        # Invalid transition: COMPLETED -> COMPLETED
        with self.assertRaises(InvalidTransitionError):
            appt.complete()

        # Let's test PENDING -> CANCELLED -> COMPLETED
        appt2 = Appointment(start, end, "+995 555 12 34 56")
        appt2.cancel()
        self.assertEqual(appt2.status, AppointmentStatus.CANCELLED)

        with self.assertRaises(InvalidTransitionError):
            appt2.complete()

        with self.assertRaises(InvalidTransitionError):
            appt2.cancel()

    def test_half_open_interval_overlap_math(self):
        master = Master(turnaround_buffer_minutes=15)

        # Booking 1: 10:00 - 11:00
        # Blocked time: 10:00 - 11:15
        start1 = datetime(2025, 1, 1, 10, 0)
        end1 = datetime(2025, 1, 1, 11, 0)
        master.book_appointment(start1, end1, "+995 555 11 11 11")

        # Overlap scenarios

        # 1. Exact same time -> Collision
        with self.assertRaises(DoubleBookingError):
            master.book_appointment(start1, end1, "+995 555 22 22 22")

        # 2. Inside the time -> Collision
        with self.assertRaises(DoubleBookingError):
            master.book_appointment(datetime(2025, 1, 1, 10, 30), datetime(2025, 1, 1, 10, 45), "+995 555 33 33 33")

        # 3. Inside the buffer -> Collision
        # Buffer ends at 11:15. This starts at 11:05 and ends at 11:20
        with self.assertRaises(DoubleBookingError):
            master.book_appointment(datetime(2025, 1, 1, 11, 5), datetime(2025, 1, 1, 11, 20), "+995 555 44 44 44")

        # 4. Touching the buffer boundary (exact end of buffer) -> No Collision
        # Buffer ends at 11:15. This starts at 11:15.
        # max(10:00, 11:15) = 11:15. min(11:15, 12:15) = 11:15. 11:15 < 11:15 is False.
        try:
            master.book_appointment(datetime(2025, 1, 1, 11, 15), datetime(2025, 1, 1, 12, 0), "+995 555 55 55 55")
        except DoubleBookingError:
            self.fail("Should not raise DoubleBookingError when starting exactly at the end of previous buffer.")

        # 5. Touching the start boundary -> Collision
        # Ends exactly when the first one starts, but the new one has a 15-min buffer!
        # New appointment: 09:00 - 10:00. Buffer ends at 10:15.
        # max(10:00, 09:00) = 10:00. min(11:15, 10:15) = 10:15. 10:00 < 10:15 is True. Collision!
        with self.assertRaises(DoubleBookingError):
            master.book_appointment(datetime(2025, 1, 1, 9, 0), datetime(2025, 1, 1, 10, 0), "+995 555 66 66 66")

        # 6. Ends before start boundary, with buffer -> No Collision
        # New appointment: 08:45 - 09:45. Buffer ends at 10:00.
        # max(10:00, 08:45) = 10:00. min(11:15, 10:00) = 10:00. 10:00 < 10:00 is False.
        try:
            master.book_appointment(datetime(2025, 1, 1, 8, 45), datetime(2025, 1, 1, 9, 45), "+995 555 77 77 77")
        except DoubleBookingError:
            self.fail("Should not raise DoubleBookingError when previous buffer ends exactly at the next appointment start.")

    def test_concurrency_atomicity(self):
        master = Master(turnaround_buffer_minutes=15)
        start = datetime(2025, 1, 1, 10, 0)
        end = datetime(2025, 1, 1, 11, 0)

        exceptions = []
        successes = []

        def book():
            try:
                appt = master.book_appointment(start, end, "+995 555 99 99 99")
                successes.append(appt)
            except Exception as e:
                exceptions.append(e)

        threads = []
        for _ in range(10):
            t = threading.Thread(target=book)
            threads.append(t)

        for t in threads:
            t.start()

        for t in threads:
            t.join()

        self.assertEqual(len(successes), 1, "Only one booking should succeed.")
        self.assertEqual(len(exceptions), 9, "9 bookings should fail due to DoubleBookingError.")
        for e in exceptions:
            self.assertIsInstance(e, DoubleBookingError)

    def test_overlap_with_cancelled_appointment(self):
        master = Master(turnaround_buffer_minutes=15)
        start = datetime(2025, 1, 1, 10, 0)
        end = datetime(2025, 1, 1, 11, 0)

        appt1 = master.book_appointment(start, end, "+995 555 11 11 11")
        appt1.cancel()

        # Should be able to book same slot since previous is cancelled
        try:
            master.book_appointment(start, end, "+995 555 22 22 22")
        except DoubleBookingError:
            self.fail("Should be able to book over a cancelled appointment.")

if __name__ == '__main__':
    unittest.main()
