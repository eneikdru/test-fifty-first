import datetime
import zoneinfo
from typing import List, Tuple, Optional

# Constants for Tbilisi timezone
TBILISI_TZ = zoneinfo.ZoneInfo("Asia/Tbilisi")

class MasterSchedule:
    def __init__(self):
        # Format: (start_time, end_time) as datetime.time objects
        # Using dict for day of week (0=Monday, 6=Sunday) -> (start_time, end_time)
        self.working_hours = {}
        # Format: (start_time, end_time) as datetime.time objects
        self.lunch_breaks = {}
        # Set of dates (datetime.date)
        self.holidays = set()

    def set_working_hours(self, day_of_week: int, start_time: datetime.time, end_time: datetime.time):
        """Set working hours for a specific day of the week (0=Monday, 6=Sunday)."""
        if not (0 <= day_of_week <= 6):
            raise ValueError("Day of week must be between 0 (Monday) and 6 (Sunday)")
        if start_time >= end_time:
            raise ValueError("Start time must be before end time")
        self.working_hours[day_of_week] = (start_time, end_time)

    def set_lunch_break(self, day_of_week: int, start_time: datetime.time, end_time: datetime.time):
        """Set lunch break for a specific day of the week (0=Monday, 6=Sunday)."""
        if not (0 <= day_of_week <= 6):
            raise ValueError("Day of week must be between 0 (Monday) and 6 (Sunday)")
        if start_time >= end_time:
            raise ValueError("Start time must be before end time")
        self.lunch_breaks[day_of_week] = (start_time, end_time)

    def add_holiday(self, date: datetime.date):
        """Add a holiday date."""
        self.holidays.add(date)

    def get_availability_windows(self, target_date: datetime.date) -> List[Tuple[datetime.time, datetime.time]]:
        """
        Get availability windows for a given date.
        Returns a list of (start, end) time tuples representing available periods.
        Takes into account working hours, lunch breaks, and holidays.
        """
        if target_date in self.holidays:
            return []

        day_of_week = target_date.weekday()
        if day_of_week not in self.working_hours:
            return []

        work_start, work_end = self.working_hours[day_of_week]

        if day_of_week not in self.lunch_breaks:
            return [(work_start, work_end)]

        lunch_start, lunch_end = self.lunch_breaks[day_of_week]

        # Check if lunch break overlaps with working hours
        if lunch_start >= work_end or lunch_end <= work_start:
            return [(work_start, work_end)]

        windows = []
        # Before lunch
        if work_start < lunch_start:
            windows.append((work_start, lunch_start))

        # After lunch
        if lunch_end < work_end:
            windows.append((lunch_end, work_end))

        return windows

    def is_available_now(self, current_datetime: Optional[datetime.datetime] = None) -> bool:
        """
        Check if the master is currently available.
        Uses UTC+4 (Tbilisi) timezone.
        current_datetime can be injected for testing, but should be tz-aware.
        """
        if current_datetime is None:
            current_datetime = datetime.datetime.now(tz=TBILISI_TZ)
        else:
            # Ensure the provided datetime is correctly localized to Tbilisi timezone
            if current_datetime.tzinfo is None:
                raise ValueError("Injected current_datetime must be timezone aware")
            current_datetime = current_datetime.astimezone(TBILISI_TZ)

        target_date = current_datetime.date()
        target_time = current_datetime.time()

        windows = self.get_availability_windows(target_date)
        for start, end in windows:
            if start <= target_time < end:
                return True

        return False
