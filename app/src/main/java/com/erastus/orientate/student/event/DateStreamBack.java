package com.erastus.orientate.student.event;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.Iterator;

/**
 * Backwards version of DateStream
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateStreamBack implements Iterator<LocalDate> {

    private LocalDate startDate;

    public DateStreamBack(LocalDate startDate) {
        this.startDate = startDate.plusDays(1);
    }

    public DateStreamBack() {
        this.startDate = LocalDate.now().plusDays(1);
    }

    public void reset(LocalDate date) {
        this.startDate = date.plusDays(1);
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public LocalDate next() {
        this.startDate = startDate.minusDays(1);  // number of days to add;
        return startDate;
    }
}
