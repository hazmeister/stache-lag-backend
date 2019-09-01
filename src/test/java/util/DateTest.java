package util;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTest {

    @Test
    public void compareDates() {
        Date date = new Date();
        Date date1 = DateHelper.addFourHours(date);
        assertThat(date1.after(date))
                .as("Four hours later comes after current time")
                .isTrue();
    }
}
