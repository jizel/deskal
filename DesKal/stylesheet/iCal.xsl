<?xml version="1.0" encoding="UTF-8"?>
<!--
    Document   : iCal.xsl
    Created on : 24. květen 2011, 15:50
    Author     : AdiC
    Description:
        Export XML calendar to iCal format
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" media-type="text/calendar"/>
<xsl:template match="/calendar">BEGIN:VCALENDAR
<xsl:for-each select="event">BEGIN:VEVENT
DTSTART:<xsl:value-of select="substring(translate(dateSince,'-',''),1,8)"/>T<xsl:value-of select="translate(timeFrom,':','')"/><xsl:value-of select="substring(dateSince,11,1)"/>
DTEND:<xsl:value-of select="substring(translate(dateTo,'-',''),1,8)"/>T<xsl:value-of select="translate(timeTo,':','')"/><xsl:value-of select="substring(dateTo,11,1)"/>
SUMMARY:<xsl:value-of select="title" />
<xsl:if test="place != ''">
LOCATION:<xsl:value-of select="place" />
</xsl:if>
<xsl:if test="note != ''">
DESCRIPTION:<xsl:value-of select="note" />
</xsl:if>
<xsl:if test="tag">
CATEGORIES:<xsl:value-of select="tag/@tagref" />
</xsl:if>
END:VEVENT
</xsl:for-each>END:VCALENDAR</xsl:template>
</xsl:stylesheet>
