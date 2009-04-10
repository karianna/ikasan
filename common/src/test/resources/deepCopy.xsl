<?xml version="1.0" encoding="UTF-8"?>
<!-- 
# //
# //
# // $Id: deepCopy.xsl 16606 2009-04-09 08:07:33Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/test/resources/deepCopy.xsl $
# // 
# // ====================================================================
# // Ikasan Enterprise Integration Platform
# // Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
# // by the @authors tag. See the copyright.txt in the distribution for a
# // full listing of individual contributors.
# //
# // This is free software; you can redistribute it and/or modify it
# // under the terms of the GNU Lesser General Public License as
# // published by the Free Software Foundation; either version 2.1 of
# // the License, or (at your option) any later version.
# //
# // This software is distributed in the hope that it will be useful,
# // but WITHOUT ANY WARRANTY; without even the implied warranty of
# // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# // Lesser General Public License for more details.
# //
# // You should have received a copy of the GNU Lesser General Public
# // License along with this software; if not, write to the 
# // Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
# // or see the FSF site: http://www.fsfeurope.org/.
# // ====================================================================
# //
# // Author:  Ikasan Development Team
# // 
-->

<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <!-- Set output options -->
  <xsl:output method="xml" encoding="UTF-8"/>

  <!--
   Straight copy of the document
   -->
  <xsl:template match="/">

      <!-- copy all structures under the root -->
      <xsl:apply-templates/>

  </xsl:template>

  <!-- default match for straight copies -->
  <xsl:template match="node() | @*"> 
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>