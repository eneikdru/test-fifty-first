from dataclasses import dataclass
from datetime import datetime, timedelta
import zoneinfo
from typing import List, Optional

TBILISI_TZ = zoneinfo.ZoneInfo("Asia/Tbilisi")

@dataclass
class TimeSlot:
    start: datetime
    end: datetime

    def __post_init__(self):
        if self.start.tzinfo is None or self.end.tzinfo is None:
            raise ValueError("TimeSlot requires timezone-aware datetimes")

        if self.start.tzinfo != TBILISI_TZ:
            self.start = self.start.astimezone(TBILISI_TZ)
        if self.end.tzinfo != TBILISI_TZ:
            self.end = self.end.astimezone(TBILISI_TZ)

        if self.start >= self.end:
            raise ValueError("Start time must be before end time")

    def overlaps_with(self, other: 'TimeSlot') -> bool:
        """
        Strict interval collision math: max(start1, start2) < min(end1, end2)
        Modeling half-open intervals [start, end)
        """
        max_start = max(self.start, other.start)
        min_end = min(self.end, other.end)
        return max_start < min_end

    def contains(self, other: 'TimeSlot') -> bool:
        """Checks if this timeslot completely contains another timeslot"""
        return self.start <= other.start and self.end >= other.end

import threading

class MasterSchedule:
    def __init__(self, working_hours: TimeSlot):
        self.working_hours = working_hours
        self.breaks: List[TimeSlot] = []
        self._lock = threading.Lock()

    def add_break(self, break_slot: TimeSlot) -> None:
        """
        Adds a break slot after strictly validating:
        1. Break is within working hours.
        2. Break does not overlap with existing breaks.
        """
        with self._lock:
            if not self.working_hours.contains(break_slot):
                raise ValueError("Break must be entirely within working hours")

            for existing_break in self.breaks:
                if existing_break.overlaps_with(break_slot):
                    raise ValueError("Breaks cannot overlap")

            self.breaks.append(break_slot)
            # Optional: keep breaks sorted by start time
            self.breaks.sort(key=lambda b: b.start)
