import unittest
from datetime import datetime, timedelta
import zoneinfo
from master_schedule import TimeSlot, MasterSchedule, TBILISI_TZ

class TestMasterSchedule(unittest.TestCase):
    def setUp(self):
        # Helper datetimes
        self.today_8am = datetime(2023, 10, 25, 8, 0, tzinfo=TBILISI_TZ)
        self.today_9am = datetime(2023, 10, 25, 9, 0, tzinfo=TBILISI_TZ)
        self.today_10am = datetime(2023, 10, 25, 10, 0, tzinfo=TBILISI_TZ)
        self.today_11am = datetime(2023, 10, 25, 11, 0, tzinfo=TBILISI_TZ)
        self.today_12pm = datetime(2023, 10, 25, 12, 0, tzinfo=TBILISI_TZ)
        self.today_1pm = datetime(2023, 10, 25, 13, 0, tzinfo=TBILISI_TZ)
        self.today_5pm = datetime(2023, 10, 25, 17, 0, tzinfo=TBILISI_TZ)
        self.working_hours = TimeSlot(self.today_9am, self.today_5pm)

    def test_timezone_immutability(self):
        # Naive datetime should be rejected
        with self.assertRaises(ValueError):
            TimeSlot(datetime(2023, 10, 25, 9, 0), datetime(2023, 10, 25, 17, 0))

        # Cross timezone handling (UTC -> Tbilisi)
        utc_tz = zoneinfo.ZoneInfo("UTC")
        # 5 AM UTC is 9 AM in UTC+4 (Tbilisi standard time)
        utc_start = datetime(2023, 10, 25, 5, 0, tzinfo=utc_tz)
        utc_end = datetime(2023, 10, 25, 13, 0, tzinfo=utc_tz)

        slot = TimeSlot(utc_start, utc_end)
        self.assertEqual(slot.start.tzinfo, TBILISI_TZ)
        self.assertEqual(slot.start.hour, 9)
        self.assertEqual(slot.end.tzinfo, TBILISI_TZ)
        self.assertEqual(slot.end.hour, 17)

    def test_interval_math_edge_cases(self):
        slot1 = TimeSlot(self.today_9am, self.today_11am)

        # Abutting slots (should not overlap)
        slot2_abut_after = TimeSlot(self.today_11am, self.today_1pm)
        slot2_abut_before = TimeSlot(self.today_8am, self.today_9am)
        self.assertFalse(slot1.overlaps_with(slot2_abut_after))
        self.assertFalse(slot1.overlaps_with(slot2_abut_before))

        # Complete overlaps
        slot3_exact = TimeSlot(self.today_9am, self.today_11am)
        slot4_contained = TimeSlot(self.today_10am, datetime(2023, 10, 25, 10, 30, tzinfo=TBILISI_TZ))
        self.assertTrue(slot1.overlaps_with(slot3_exact))
        self.assertTrue(slot1.overlaps_with(slot4_contained))
        self.assertTrue(slot1.contains(slot4_contained))

        # Partial overlaps
        slot5_partial_after = TimeSlot(self.today_10am, self.today_12pm)
        slot6_partial_before = TimeSlot(self.today_8am, self.today_10am)
        self.assertTrue(slot1.overlaps_with(slot5_partial_after))
        self.assertTrue(slot1.overlaps_with(slot6_partial_before))

        # Invalid slot (start >= end)
        with self.assertRaises(ValueError):
            TimeSlot(self.today_11am, self.today_9am)
        with self.assertRaises(ValueError):
            TimeSlot(self.today_9am, self.today_9am)

    def test_master_schedule_negative_gates(self):
        schedule = MasterSchedule(self.working_hours)

        # Break outside working hours (before)
        break_before = TimeSlot(self.today_8am, self.today_10am)
        with self.assertRaisesRegex(ValueError, "Break must be entirely within working hours"):
            schedule.add_break(break_before)

        # Break outside working hours (after)
        break_after = TimeSlot(self.today_1pm, datetime(2023, 10, 25, 18, 0, tzinfo=TBILISI_TZ))
        with self.assertRaisesRegex(ValueError, "Break must be entirely within working hours"):
            schedule.add_break(break_after)

        # Valid break
        break1 = TimeSlot(self.today_10am, self.today_11am)
        schedule.add_break(break1)
        self.assertEqual(len(schedule.breaks), 1)

        # Overlapping break (exact)
        break2_overlap = TimeSlot(self.today_10am, self.today_11am)
        with self.assertRaisesRegex(ValueError, "Breaks cannot overlap"):
            schedule.add_break(break2_overlap)

        # Overlapping break (partial)
        break3_overlap = TimeSlot(datetime(2023, 10, 25, 10, 30, tzinfo=TBILISI_TZ), self.today_12pm)
        with self.assertRaisesRegex(ValueError, "Breaks cannot overlap"):
            schedule.add_break(break3_overlap)

        # Valid second break
        break4 = TimeSlot(self.today_1pm, datetime(2023, 10, 25, 14, 0, tzinfo=TBILISI_TZ))
        schedule.add_break(break4)
        self.assertEqual(len(schedule.breaks), 2)

if __name__ == '__main__':
    unittest.main()
