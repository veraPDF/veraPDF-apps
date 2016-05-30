<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:vera="http://www.verapdf.org/ValidationProfile">

    <xsl:output method="text"/>

    <xsl:variable name="summ" select="sum(vera:report/vera:validationReport/vera:details/vera:rule/@failedChecks)"/>

    <xsl:template match="/">
        <xsl:value-of select="$summ"/>
        <xsl:if test="$summ &gt; 5">Not Bad</xsl:if>
    </xsl:template>

</xsl:stylesheet>