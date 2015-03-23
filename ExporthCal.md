# Export to hCal format #

XSL Stylesheet for transformation calendar to hCalendar format.



```

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml" media-type="text/calendar"/>
<xsl:template match="/calendar">
<xsl:text disable-output-escaping="yes">
<![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">]]>


Unknown end tag for &lt;/text&gt;


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>hCalendar export

Unknown end tag for &lt;/title&gt;




Unknown end tag for &lt;/head&gt;


<body>
<xsl:for-each select="event">
<p class="vevent">
<abbr class="dtstart">
<xsl:value-of select="substring(translate(dateSince,'-',''),1,8)"/>T<xsl:value-of select="translate(timeFrom,':','')"/>
<xsl:value-of select="substring(dateSince,11,1)"/>


Unknown end tag for &lt;/abbr&gt;


<abbr class="dtend">
<xsl:value-of select="substring(translate(dateTo,'-',''),1,8)"/>T<xsl:value-of select="translate(timeTo,':','')"/>
<xsl:value-of select="substring(dateTo,11,1)"/>


Unknown end tag for &lt;/abbr&gt;


<span class="summary"><xsl:value-of select="title" />

Unknown end tag for &lt;/span&gt;


<xsl:if test="place != ''">
<abbr class="location"><xsl:value-of select="place" />

Unknown end tag for &lt;/abbr&gt;




Unknown end tag for &lt;/if&gt;


<xsl:if test="note != ''">
<abbr class="description"><xsl:value-of select="note" />

Unknown end tag for &lt;/abbr&gt;




Unknown end tag for &lt;/if&gt;


<xsl:if test="tag">
<abbr class="category"><xsl:value-of select="tag/@tagref" />

Unknown end tag for &lt;/abbr&gt;




Unknown end tag for &lt;/if&gt;




Unknown end tag for &lt;/p&gt;




Unknown end tag for &lt;/for-each&gt;




Unknown end tag for &lt;/body&gt;




Unknown end tag for &lt;/html&gt;




Unknown end tag for &lt;/template&gt;




Unknown end tag for &lt;/stylesheet&gt;


```