<?xml version="1.0" encoding="Windows-1250"?>
<!--
    Document   : iCal.xsl
    Created on : 24. květen 2011, 15:50
    Author     : AdiC
    Description:
        Export XML calendar to hCal format
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!--
    output method xml, because when method is html, parser insert incorrect tag <META ... > without endind /, so entire document is incorrect
    -->
<xsl:output method="xml" version="1.0" encoding="Windows-1250" media-type="text/calendar" />
<xsl:template match="/calendar">
    <span class="vcalendar">    
    <xsl:for-each select="event">
        <div class="vevent">
            <abbr class="dtstart"><xsl:value-of select="translate(dateSince,'Z','T')"/><xsl:value-of select="timeFrom"/>+01:00</abbr>
            <abbr class="dtend"><xsl:value-of select="translate(dateTo,'Z','T')"/><xsl:value-of select="timeTo"/>+01:00</abbr>
            <span class="summary"><xsl:value-of select="title" /></span>
            <xsl:if test="place != ''">
                <abbr class="location"><xsl:value-of select="place" /></abbr>
            </xsl:if>
            <xsl:if test="note != ''">
                <abbr class="description"><xsl:value-of select="note" /></abbr>
            </xsl:if>
            <xsl:if test="tag">
                <abbr class="categories"><xsl:value-of select="tag/@tagref" /></abbr>
            </xsl:if>
        </div>
    </xsl:for-each>
    </span>
</xsl:template>
</xsl:stylesheet>
