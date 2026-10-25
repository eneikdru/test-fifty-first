import threading
import re
from enum import Enum
from datetime import datetime, timedelta

class AppointmentStatus(Enum):
    PENDING = "PENDING"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

class InvalidPhoneError(ValueError):
    pass

class InvalidTransitionError(ValueError):
    pass

class DoubleBookingError(ValueError):
    pass

class Appointment:
    def __init__(self, start_time: datetime, end_time: datetime, client_phone: str):
        self.start_time = start_time
        self.end_time = end_time
        self._validate_phone(client_phone)
        self.client_phone = client_phone
        self.status = AppointmentStatus.PENDING

    def _validate_phone(self, phone: str):
        # Rigid Georgian mobile phone validation (+995 5XX XX XX XX)
        pattern = r"^\+995 5\d{2} \d{2} \d{2} \d{2}$"
        if not re.match(pattern, phone):
            raise InvalidPhoneError(f"Invalid Georgian phone number: {phone}")

    def complete(self):
        if self.status == AppointmentStatus.CANCELLED:
            raise InvalidTransitionError("Cannot complete a cancelled appointment.")
        if self.status == AppointmentStatus.COMPLETED:
            raise InvalidTransitionError("Appointment is already completed.")
        self.status = AppointmentStatus.COMPLETED

    def cancel(self):
        if self.status == AppointmentStatus.COMPLETED:
            raise InvalidTransitionError("Cannot cancel a completed appointment.")
        if self.status == AppointmentStatus.CANCELLED:
            raise InvalidTransitionError("Appointment is already cancelled.")
        self.status = AppointmentStatus.CANCELLED

class Master:
    def __init__(self, turnaround_buffer_minutes: int = 15):
        self.appointments = []
        self.lock = threading.Lock()
        self.buffer = timedelta(minutes=turnaround_buffer_minutes)

    def book_appointment(self, start_time: datetime, end_time: datetime, client_phone: str) -> Appointment:
        if start_time >= end_time:
            raise ValueError("End time must be after start time.")

        with self.lock:
            # Check overlap BEFORE creating Appointment to avoid unnecessary ID/object creation if we fail,
            # though doing it after is fine too. Phone validation might fail before lock, which is good.
            # We validate phone first by creating the object outside lock if we want, or just doing phone valid first.
            # Let's instantiate first to validate phone.
            new_appointment = Appointment(start_time, end_time, client_phone)

            # Strict half-open interval overlap math: collision occurs if max(start1, start2) < min(end1, end2)
            # incorporating master turnaround buffer.
            # We add buffer to the end_time to represent the total blocked time.
            start2 = new_appointment.start_time
            end2 = new_appointment.end_time + self.buffer

            for appt in self.appointments:
                if appt.status == AppointmentStatus.CANCELLED:
                    continue

                start1 = appt.start_time
                end1 = appt.end_time + self.buffer

                if max(start1, start2) < min(end1, end2):
                    raise DoubleBookingError("Appointment overlaps with an existing booking.")

            self.appointments.append(new_appointment)
            return new_appointment
