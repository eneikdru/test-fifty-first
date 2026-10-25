import unittest
import datetime
import zoneinfo
from schedule.master_schedule import MasterSchedule, TBILISI_TZ

class TestMasterSchedule(unittest.TestCase):
    def setUp(self):
        self.schedule = MasterSchedule()
        # Set up working hours for Monday (0)
        self.schedule.set_working_hours(0, datetime.time(9, 0), datetime.time(18, 0))
        # Set up lunch break for Monday
        self.schedule.set_lunch_break(0, datetime.time(13, 0), datetime.time(14, 0))

        # Set up working hours for Tuesday (1) without lunch break
        self.schedule.set_working_hours(1, datetime.time(10, 0), datetime.time(16, 0))

        # Add a holiday
        self.holiday_date = datetime.date(2023, 1, 1) # Sunday
        self.schedule.add_holiday(self.holiday_date)

        # Another holiday on a working day
        self.holiday_working_day = datetime.date(2023, 1, 2) # Monday
        self.schedule.add_holiday(self.holiday_working_day)

    def test_set_invalid_working_hours(self):
        # Invalid day
        with self.assertRaises(ValueError):
            self.schedule.set_working_hours(7, datetime.time(9, 0), datetime.time(18, 0))
        # Start time after end time
        with self.assertRaises(ValueError):
            self.schedule.set_working_hours(0, datetime.time(18, 0), datetime.time(9, 0))

    def test_set_invalid_lunch_break(self):
        # Invalid day
        with self.assertRaises(ValueError):
            self.schedule.set_lunch_break(-1, datetime.time(13, 0), datetime.time(14, 0))
        # Start time after end time
        with self.assertRaises(ValueError):
            self.schedule.set_lunch_break(0, datetime.time(14, 0), datetime.time(13, 0))

    def test_availability_windows_normal_day_with_lunch(self):
        # 2023-01-09 is a Monday, not a holiday
        target_date = datetime.date(2023, 1, 9)
        windows = self.schedule.get_availability_windows(target_date)

        expected = [
            (datetime.time(9, 0), datetime.time(13, 0)),
            (datetime.time(14, 0), datetime.time(18, 0))
        ]
        self.assertEqual(windows, expected)

    def test_availability_windows_normal_day_without_lunch(self):
        # 2023-01-10 is a Tuesday, not a holiday
        target_date = datetime.date(2023, 1, 10)
        windows = self.schedule.get_availability_windows(target_date)

        expected = [
            (datetime.time(10, 0), datetime.time(16, 0))
        ]
        self.assertEqual(windows, expected)

    def test_availability_windows_non_working_day(self):
        # 2023-01-11 is a Wednesday, not configured as a working day
        target_date = datetime.date(2023, 1, 11)
        windows = self.schedule.get_availability_windows(target_date)
        self.assertEqual(windows, [])

    def test_availability_windows_holiday(self):
        # 2023-01-02 is a Monday, but it's a holiday
        windows = self.schedule.get_availability_windows(self.holiday_working_day)
        self.assertEqual(windows, [])

    def test_is_available_now_during_work_hours(self):
        # 2023-01-09 10:00:00 (Monday) - Available
        current_dt = datetime.datetime(2023, 1, 9, 10, 0, tzinfo=TBILISI_TZ)
        self.assertTrue(self.schedule.is_available_now(current_dt))

    def test_is_available_now_during_lunch(self):
        # 2023-01-09 13:30:00 (Monday) - Lunch break
        current_dt = datetime.datetime(2023, 1, 9, 13, 30, tzinfo=TBILISI_TZ)
        self.assertFalse(self.schedule.is_available_now(current_dt))

    def test_is_available_now_before_work(self):
        # 2023-01-09 08:59:59 (Monday) - Before work
        current_dt = datetime.datetime(2023, 1, 9, 8, 59, 59, tzinfo=TBILISI_TZ)
        self.assertFalse(self.schedule.is_available_now(current_dt))

    def test_is_available_now_after_work(self):
        # 2023-01-09 18:00:00 (Monday) - End of work (exclusive)
        current_dt = datetime.datetime(2023, 1, 9, 18, 0, 0, tzinfo=TBILISI_TZ)
        self.assertFalse(self.schedule.is_available_now(current_dt))

    def test_is_available_now_on_holiday(self):
        # 2023-01-02 10:00:00 (Monday, holiday)
        current_dt = datetime.datetime(2023, 1, 2, 10, 0, 0, tzinfo=TBILISI_TZ)
        self.assertFalse(self.schedule.is_available_now(current_dt))

    def test_is_available_now_timezone_conversion(self):
        # User in UTC querying availability
        # Tbilisi is UTC+4. So UTC 06:00 is Tbilisi 10:00 (Available)
        utc_dt = datetime.datetime(2023, 1, 9, 6, 0, tzinfo=datetime.timezone.utc)
        self.assertTrue(self.schedule.is_available_now(utc_dt))

        # UTC 09:30 is Tbilisi 13:30 (Lunch break)
        utc_dt_lunch = datetime.datetime(2023, 1, 9, 9, 30, tzinfo=datetime.timezone.utc)
        self.assertFalse(self.schedule.is_available_now(utc_dt_lunch))

    def test_is_available_now_unaware_datetime(self):
        # Providing naive datetime should raise ValueError per Falsification Gates
        naive_dt = datetime.datetime(2023, 1, 9, 10, 0)
        with self.assertRaises(ValueError):
            self.schedule.is_available_now(naive_dt)

if __name__ == '__main__':
    unittest.main()
