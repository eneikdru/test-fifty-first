import re
from datetime import datetime
import uuid

class InvalidPhoneError(ValueError):
    pass

class ConflictError(ValueError):
    pass

class AppointmentNotFoundError(ValueError):
    pass

def validate_phone(phone: str) -> bool:
    """
    Validates Georgian phone numbers.
    Format: +995 followed by exactly 9 digits.
    """
    pattern = r"^\+995\d{9}$"
    if re.match(pattern, phone):
        return True
    return False

class Appointment:
    def __init__(self, master: str, client: str, phone: str, start_time: datetime, end_time: datetime):
        self.id = str(uuid.uuid4())
        self.master = master
        self.client = client
        self.phone = phone
        self.start_time = start_time
        self.end_time = end_time

class BookingService:
    def __init__(self):
        self.appointments = []

    def check_conflicts(self, master: str, start_time: datetime, end_time: datetime) -> bool:
        """
        Checks if there's an overlapping appointment for the given master.
        Returns True if a conflict exists, False otherwise.
        """
        for apt in self.appointments:
            if apt.master == master:
                # Conflict occurs if the new appointment overlaps with an existing one
                if start_time < apt.end_time and end_time > apt.start_time:
                    return True
        return False

    def book_appointment(self, master: str, client: str, phone: str, start_time: datetime, end_time: datetime) -> Appointment:
        if not validate_phone(phone):
            raise InvalidPhoneError("Invalid Georgian phone number format.")

        if start_time >= end_time:
            raise ValueError("Start time must be before end time.")

        if self.check_conflicts(master, start_time, end_time):
            raise ConflictError(f"Master {master} is already booked for the requested time.")

        new_appointment = Appointment(master, client, phone, start_time, end_time)
        self.appointments.append(new_appointment)
        return new_appointment

    def cancel_appointment(self, appointment_id: str) -> bool:
        for i, apt in enumerate(self.appointments):
            if apt.id == appointment_id:
                del self.appointments[i]
                return True
        raise AppointmentNotFoundError(f"Appointment with ID {appointment_id} not found.")
