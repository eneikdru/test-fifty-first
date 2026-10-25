import unittest
from datetime import datetime, timedelta
from beauty_booking import (
    validate_phone,
    BookingService,
    InvalidPhoneError,
    ConflictError,
    AppointmentNotFoundError
)

class TestBeautyBooking(unittest.TestCase):

    def setUp(self):
        self.service = BookingService()
        self.now = datetime(2023, 11, 1, 10, 0, 0)
        self.one_hour_later = self.now + timedelta(hours=1)
        self.two_hours_later = self.now + timedelta(hours=2)

    def test_validate_phone_valid(self):
        self.assertTrue(validate_phone("+995123456789"))
        self.assertTrue(validate_phone("+995987654321"))

    def test_validate_phone_invalid(self):
        self.assertFalse(validate_phone("995123456789")) # Missing +
        self.assertFalse(validate_phone("+99512345678")) # Too short
        self.assertFalse(validate_phone("+9951234567890")) # Too long
        self.assertFalse(validate_phone("+123123456789")) # Wrong country code
        self.assertFalse(validate_phone("+99512345678a")) # Contains letters

    def test_book_appointment_success(self):
        appointment = self.service.book_appointment(
            master="Anna",
            client="Maria",
            phone="+995555112233",
            start_time=self.now,
            end_time=self.one_hour_later
        )
        self.assertIsNotNone(appointment.id)
        self.assertEqual(appointment.master, "Anna")
        self.assertEqual(appointment.client, "Maria")
        self.assertEqual(appointment.phone, "+995555112233")
        self.assertEqual(appointment.start_time, self.now)
        self.assertEqual(appointment.end_time, self.one_hour_later)
        self.assertEqual(len(self.service.appointments), 1)

    def test_book_appointment_invalid_phone(self):
        with self.assertRaises(InvalidPhoneError):
            self.service.book_appointment(
                master="Anna",
                client="Maria",
                phone="12345",
                start_time=self.now,
                end_time=self.one_hour_later
            )
        self.assertEqual(len(self.service.appointments), 0)

    def test_book_appointment_invalid_time(self):
        with self.assertRaises(ValueError):
            self.service.book_appointment(
                master="Anna",
                client="Maria",
                phone="+995555112233",
                start_time=self.one_hour_later,
                end_time=self.now
            )
        self.assertEqual(len(self.service.appointments), 0)

    def test_book_appointment_conflict_same_time(self):
        self.service.book_appointment(
            master="Anna",
            client="Maria",
            phone="+995555112233",
            start_time=self.now,
            end_time=self.one_hour_later
        )
        with self.assertRaises(ConflictError):
            self.service.book_appointment(
                master="Anna",
                client="Sophie",
                phone="+995555445566",
                start_time=self.now,
                end_time=self.one_hour_later
            )
        self.assertEqual(len(self.service.appointments), 1)

    def test_book_appointment_conflict_overlapping(self):
        self.service.book_appointment(
            master="Anna",
            client="Maria",
            phone="+995555112233",
            start_time=self.now,
            end_time=self.two_hours_later
        )

        # Starts during another appointment
        with self.assertRaises(ConflictError):
            self.service.book_appointment(
                master="Anna",
                client="Sophie",
                phone="+995555445566",
                start_time=self.one_hour_later,
                end_time=self.two_hours_later + timedelta(hours=1)
            )

        # Ends during another appointment
        with self.assertRaises(ConflictError):
            self.service.book_appointment(
                master="Anna",
                client="Sophie",
                phone="+995555445566",
                start_time=self.now - timedelta(hours=1),
                end_time=self.one_hour_later
            )

        # Completely contained within another appointment
        with self.assertRaises(ConflictError):
            self.service.book_appointment(
                master="Anna",
                client="Sophie",
                phone="+995555445566",
                start_time=self.now + timedelta(minutes=30),
                end_time=self.one_hour_later
            )

        self.assertEqual(len(self.service.appointments), 1)

    def test_book_multiple_masters_concurrently(self):
        self.service.book_appointment(
            master="Anna",
            client="Maria",
            phone="+995555112233",
            start_time=self.now,
            end_time=self.one_hour_later
        )
        # Should succeed because it's a different master
        self.service.book_appointment(
            master="Bella",
            client="Sophie",
            phone="+995555445566",
            start_time=self.now,
            end_time=self.one_hour_later
        )
        self.assertEqual(len(self.service.appointments), 2)

    def test_cancel_appointment_success(self):
        appointment = self.service.book_appointment(
            master="Anna",
            client="Maria",
            phone="+995555112233",
            start_time=self.now,
            end_time=self.one_hour_later
        )
        self.assertEqual(len(self.service.appointments), 1)

        result = self.service.cancel_appointment(appointment.id)
        self.assertTrue(result)
        self.assertEqual(len(self.service.appointments), 0)

    def test_cancel_appointment_not_found(self):
        with self.assertRaises(AppointmentNotFoundError):
            self.service.cancel_appointment("nonexistent-id")

if __name__ == '__main__':
    unittest.main()
