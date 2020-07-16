package com.erastus.orientate.student.event;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.Iterator;


/**
 * an infinite iterator to return all the dates starting from a specified start date
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateStream implements Iterator<LocalDate> {
    private LocalDate startDate;

    public DateStream(LocalDate startDate) {
        this.startDate = startDate;
    }

    public DateStream() {
        this.startDate = LocalDate.now();
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public LocalDate next() {
        this.startDate = startDate.plusDays(1);  // number of days to add;
        return startDate;
    }


}
