<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="text"/>
    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>
<xsl:template match="//*[@class='vevent']">
    <xsl:apply-templates select=".//*[@class]"/>
---
</xsl:template>
<xsl:template match="*[@class='summary'][not(@title)]">;SUMMARY;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='description'][not(@title)]">;DESCRIPTION;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='dtstart'][not(@title)]">;DTSTART;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='dtend'][not(@title)]">;DTEND;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='location'][not(@title)]">;LOCATION;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='categories'][not(@title)]">;CATEGORIES;<xsl:value-of select="current()"/>
</xsl:template>
<xsl:template match="*[@class='summary'][@title]">;SUMMARY;<xsl:value-of select="./@title"/>
</xsl:template>
<xsl:template match="*[@class='description'][@title]">;DESCRIPTION;<xsl:value-of select="./@title"/>
</xsl:template>
<xsl:template match="*[@class='dtstart'][@title]">;DTSTART;<xsl:value-of select="./@title"/>
</xsl:template>
<xsl:template match="*[@class='dtend'][@title]">;DTEND;<xsl:value-of select="./@title"/>
</xsl:template>
<xsl:template match="*[@class='location'][@title]">;LOCATION;<xsl:value-of select="./@title"/>
</xsl:template>
<xsl:template match="*[@class='categories'][@title]">;CATEGORIES;<xsl:value-of select="./@title"/>
</xsl:template>
</xsl:stylesheet>