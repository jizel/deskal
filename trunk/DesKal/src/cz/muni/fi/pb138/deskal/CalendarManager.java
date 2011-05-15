package cz.muni.fi.pb138.deskal;

import java.util.List;

public interface CalendarManager {

    public List<Day> getDaysInMonthWithTag(int year, int month);

    public void addFilter(Filter filter);

    public void removeFilter(Filter filter);

    public List<String> getAllTags();
}
