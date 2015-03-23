#### iCalendar ####
**iCalendar** is a computer file format which allows Internet users to send meeting requests and tasks to other Internet users, via email, or sharing files with an extension of .ics. Recipients of the iCalendar data file (with supporting software, such as an email client or calendar application) can respond to the sender easily or counter propose another meeting date/time.

iCalendar is used and supported by a large number of products, including Google Calendar, Apple iCal,GoDaddy Online Group Calendar,IBM Lotus Notes,Yahoo! Calendar and partially also by Microsoft Outlook. iCalendar is designed to be independent of the transport protocol. For example, certain events can be sent by traditional email or whole calendar files can be shared and edited by using a WebDav server, or SyncML. Simple web servers (using just the HTTP protocol) are often used to distribute iCalendar data about an event and to publish busy times of an individual. Publishers can embed iCalendar data in web pages using hCalendar, a 1:1 microformat representation of iCalendar in semantic (X)HTML.

**Technical specifications**

The top-level element in iCalendar is the Calendaring and Scheduling Core Object, a collection of calendar and scheduling information. Typically, this information will consist of a single iCalendar object. However, multiple iCalendar objects can be grouped together.
The first line must be "BEGIN:VCALENDAR", and the last line must be "END:VCALENDAR"; the contents between these lines is called the "icalbody".
The body of the iCalendar object (the icalbody) is made up of a list of calendar properties and one or more calendar components. The calendar properties apply to the entire calendar. The calendar components are several calendar properties which create a calendar schematic (design). For example, the calendar component can specify an event, a to-do list, a journal entry, time zone information, or free/busy time information, or an alarm. Empty lines are not allowed in some versions of usage (Google calendar).
Here is a simple example of an iCalendar object, "Bastille Day Party" event which occurs July 14, 1997
17:00 (UTC) through July 15, 1997 03:59:59 (UTC):

```

BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//hacksw/handcal//NONSGML v1.0//EN
BEGIN:VEVENT
UID:uid1@example.com
DTSTAMP:19970714T170000Z
ORGANIZER;CN=John Doe:MAILTO:john.doe@example.com
DTSTART:19970714T170000Z
DTEND:19970715T035959Z
SUMMARY:Bastille Day Party
END:VEVENT
END:VCALENDAR
```
There are many different types of components which can be used in iCalendar, as described below.

**Events (VEVENT)**
"VEVENT" describes an event, which has a scheduled amount of time on a calendar. Normally, when a user accepts the calendar event, this will cause that time to be considered busy. A VEVENT may include a VALARM which allows an alarm. Such events have a DTSTART which sets a starting time, and a DTEND which sets an ending time. If the calendar event is recurring, DTSTART sets up the start of the first event.
VEVENT also is used for calendar events without a specific time, such as anniversaries and daily reminders.If you need to send in a cancellation for an event the UID should be same as the original event and the component properties should be set to cancel Ex.
METHOD:CANCEL
STATUS:CANCELLED
For sending an UPDATE for an event the UID should match the original UID. the other component property to be set is:
SEQUENCE:<Num of Update>
i.e. for the first update
SEQUENCE:1
In Microsoft Outlook, the SUMMARY corresponds to the "Subject" entry in the "Appointment" form, and DESCRIPTION to the descriptive text below it. In addition, Outlook 2003 demands a UID and a DTSTAMP.
To-do (VTODO)
VTODO explains a to-do item, i.e., an action-item or assignment.
The following is an example of a to-do due on April 15, 1998. An audio alarm has been specified to remind the calendar user at noon, the day before the to-do is expected to be completed and repeat hourly, four additional times. The SEQUENCE element shows this to-do has been modified twice since it was initially created.

```

BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ABC Corporation//NONSGML My Product//EN
BEGIN:VTODO
DTSTAMP:19980130T134500Z
SEQUENCE:2
UID:uid4@host1.com
ORGANIZER:MAILTO:unclesam@us.gov
ATTENDEE;PARTSTAT=ACCEPTED:MAILTO:jqpublic@example.com
DUE:19980415T235959
STATUS:NEEDS-ACTION
SUMMARY:Submit Income Taxes
BEGIN:VALARM
ACTION:AUDIO
TRIGGER:19980403T120000
ATTACH;FMTTYPE=audio/basic:http://example.com/pub/audio-
files/ssbanner.aud
REPEAT:4
DURATION:PT1H
END:VALARM
END:VTODO
END:VCALENDAR
```

**Journal entry (VJOURNAL)**
VJOURNAL is a journal entry. They attach descriptive text to a particular calendar date, may be used to record a daily record of activities or accomplishments, or describe progress with a related to-do entry. A "VJOURNAL" calendar component does not take up time on a calendar, so it has no effect on free or busy time (just like TRANSPARENT entries). In practice, few programs support VJOURNAL entries, although examples exist: Plum Canary's Chirp software uses VTODO and VJOURNAL together. Also KOrganizer from the KDE desktop supports VJOURNAL.
The following is an example of a journal entry:

```

BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//ABC Corporation//NONSGML My Product//EN
BEGIN:VJOURNAL
DTSTAMP:19970324T120000Z
UID:uid5@host1.com
ORGANIZER:MAILTO:jsmith@example.com
STATUS:DRAFT
CLASS:PUBLIC
CATEGORIES:Project Report, XYZ, Weekly Meeting
DESCRIPTION:Project xyz Review Meeting Minutes\n
Agenda\n1. Review of project version 1.0 requirements.\n2.
Definition
of project processes.\n3. Review of project schedule.\n
Participants: John Smith, Jane Doe, Jim Dandy\n-It was
decided that the requirements need to be signed off by
product marketing.\n-Project processes were accepted.\n
-Project schedule needs to account for scheduled holidays
and employee vacation time. Check with HR for specific
dates.\n-New schedule will be distributed by Friday.\n-
Next weeks meeting is cancelled. No meeting until 3/23.
END:VJOURNAL
END:VCALENDAR
```

(Note: This example is literally taken from RFC 2445 with the correction of changing the word 'CATEGORY' to 'CATEGORIES', which is a mistake in the original RFC)

**Free/busy time (VFREEBUSY)**
VFREEBUSY is a request for free/busy time, is a response to a request, or is a published set of busy time.
The following is an example of published busy time information.:
```

BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//RDU Software//NONSGML HandCal//EN
BEGIN:VFREEBUSY
ORGANIZER:MAILTO:jsmith@example.com
DTSTART:19980313T141711Z
DTEND:19980410T141711Z
FREEBUSY:19980314T233000Z/19980315T003000Z
FREEBUSY:19980316T153000Z/19980316T163000Z
FREEBUSY:19980318T030000Z/19980318T040000Z
URL:http://www.example.com/calendar/busytime/jsmith.ifb
END:VFREEBUSY
END:VCALENDAR
```

**Other component types**
Other component types include VTIMEZONE (time zones) and VALARM (alarms). Some components can include other components (VALARM is often included in other components).