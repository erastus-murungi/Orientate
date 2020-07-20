package com.erastus.orientate.utils.horizontalcalendar.utils;

import com.erastus.orientate.utils.horizontalcalendar.models.CalendarEvent;

import java.util.Calendar;
import java.util.List;


public interface CalendarEventsPredicate {

    /**
     * @param date the date where the events will be attached to.
     * @return a list of {@link CalendarEvent} related to this date.
     */
    List<CalendarEvent> events(Calendar date);
}
