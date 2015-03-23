# Export to iCal format #

XSL Stylesheet for transformation calendar to iCalendar format.

_Code isn't well readable, because of newline characters, which makes unnecessary empty lines in iCal file_

```

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" media-type="text/calendar"/>
<xsl:template match="/calendar">BEGIN:VCALENDAR
<xsl:for-each select="event">BEGIN:VEVENT
DTSTART:<xsl:value-of select="substring(translate(dateSince,'-',''),1,8)"/>T<xsl:value-of select="translate(timeFrom,':','')"/><xsl:value-of select="substring(dateSince,11,1)"/>
DTEND:<xsl:value-of select="substring(translate(dateTo,'-',''),1,8)"/>T<xsl:value-of select="translate(timeTo,':','')"/><xsl:value-of select="substring(dateTo,11,1)"/>
SUMMARY:<xsl:value-of select="title" />
<xsl:if test="place != ''">
LOCATION:<xsl:value-of select="place" />


Unknown end tag for &lt;/if&gt;


<xsl:if test="note != ''">
DESCRIPTION:<xsl:value-of select="note" />


Unknown end tag for &lt;/if&gt;


<xsl:if test="tag">
CATEGORIES:<xsl:value-of select="tag/@tagref" />


Unknown end tag for &lt;/if&gt;


END:VEVENT


Unknown end tag for &lt;/for-each&gt;

END:VCALENDAR

Unknown end tag for &lt;/template&gt;




Unknown end tag for &lt;/stylesheet&gt;


```