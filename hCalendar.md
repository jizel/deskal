## hCalendar ##
**hCalendar** (short for HTML iCalendar) is a microformat standard for displaying a semantic (X)HTML representation of iCalendar-format calendar information about an event, on web pages, using HTML classes and rel attributes.
It allows parsing tools (for example other websites, or browser add-ons like Firefox's Operator extension) to extract the details of the event, and display them using some other website, index or search them, or to load them into a calendar or diary program, for instance. Multiple instances can be displayed as timelines.

Example:
```

The English Wikipedia was launched
on 15 January 2001 with a party from
2-4pm at
Jimmy Wales' house
(more information).
```

The HTML mark-up might be:
```

<p>
The English Wikipedia was launched
on 15 January 2001 with a party from
2-4pm at
Jimmy Wales' house
(<a href="http://en.wikipedia.org/wiki/History_of_Wikipedia">more information

Unknown end tag for &lt;/a&gt;

)


Unknown end tag for &lt;/p&gt;


```

hCalendar mark-up may be added using span HTML elements and the classes vevent, summary, dtstart (start date), dtend (end date), location and url:
```

<p class="vevent">
The <span class="summary">English Wikipedia was launched

Unknown end tag for &lt;/span&gt;


on 15 January 2001 with a party from
<abbr class="dtstart" title="2001-01-15T14:00:00+06:00">2

Unknown end tag for &lt;/abbr&gt;

-
<abbr class="dtend" title="2001-01-15T16:00:00+06:00">4

Unknown end tag for &lt;/abbr&gt;

pm at
<span class="location">Jimmy Wales' house

Unknown end tag for &lt;/span&gt;


(<a class="url" href="http://en.wikipedia.org/wiki/History_of_Wikipedia">more information

Unknown end tag for &lt;/a&gt;

)


Unknown end tag for &lt;/p&gt;


```

Note the use of the abbr element to contain the machine readable, ISO8601, date-time format for the start and end times.

Exclusive end-dates:
For whole-day dates, where no time is specified, the end-date must be recorded as exclusive (i.e. the day after the event ends). For example:
```

<abbr class="dtend" title="2001-02-01">31 January 2001

Unknown end tag for &lt;/abbr&gt;


```