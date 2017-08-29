package com.young.planhelper.widget.calendar.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blaz Solar on 24/02/14.
 */
public class Week extends RangeUnit {

    @NotNull
    private final List<Day> mDays = new ArrayList<Day>(7);

    public Week(@NotNull LocalDate date, @NotNull LocalDate today, @Nullable LocalDate minDate,
                @Nullable LocalDate maxDate) {
        super(
                date.withDayOfWeek(1),
                date.withDayOfWeek(7),
                "'week' w",
                today,
                minDate,
                maxDate
        );

        build();
    }

    @Override
    public boolean hasNext() {
        LocalDate maxDate = getMaxDate();
        if (maxDate == null) {
            return true;
        } else {
            return maxDate.isAfter(mDays.get(6).getDate());
        }
    }

    @Override
    public boolean hasPrev() {
        LocalDate minDate = getMinDate();
        if (minDate == null) {
            return true;
        } else {
            return minDate.isBefore(mDays.get(0).getDate());
        }
    }

    @Override
    public boolean next() {
        if (hasNext()) {
            setFrom(getFrom().plusWeeks(1));
            setTo(getTo().plusWeeks(1));
            build();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean prev() {
        if (hasPrev()) {
            setFrom(getFrom().minusWeeks(1));
            setTo(getTo().minusWeeks(1));
            build();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deselect(@NotNull LocalDate date) {
        if (getFrom().compareTo(date) <= 0 &&
                getTo().compareTo(date) >= 0) {
            setSelected(false);

            for (Day day : mDays) {
                day.setSelected(false);
            }
        }
    }

    @Override
    public boolean select(@NotNull LocalDate date) {
        if (getFrom().compareTo(date) <= 0 &&
                getTo().compareTo(date) >= 0) {
            setSelected(true);

            for (Day day : mDays) {
                day.setSelected(day.getDate().isEqual(date));
            }
            return true;
        } else {
            return false;
        }
    }

    @NotNull
    public List<Day> getDays() {
        return mDays;
    }

    @Override
    public void build() {

        mDays.clear();

        LocalDate date = getFrom();
        for(; date.compareTo(getTo()) <= 0; date = date.plusDays(1)) {
            Day day = new Day(date, date.equals(getToday()));
            day.setEnabled(isDayEnabled(date));
            mDays.add(day);
        }

    }

    private boolean isDayEnabled(@NotNull LocalDate date) {

        LocalDate minDate = getMinDate();
        if (minDate != null && date.isBefore(minDate)) {
            return false;
        }

        LocalDate maxDate = getMaxDate();
        if (maxDate != null && date.isAfter(maxDate)) {
            return false;
        }

        return true;
    }

    @Nullable
    @Override
    LocalDate getFirstDateOfCurrentMonth(@NotNull LocalDate currentMonth) {

        int year = currentMonth.getYear();
        int month = currentMonth.getMonthOfYear();

        LocalDate date = getFrom();
        for(; date.compareTo(getTo()) <= 0; date = date.plusDays(1)) {
            int fromYear = date.getYear();
            int fromMonth = date.getMonthOfYear();

            if (year == fromYear && month == fromMonth) {
                return date;
            }
        }

        return null;
    }
}
