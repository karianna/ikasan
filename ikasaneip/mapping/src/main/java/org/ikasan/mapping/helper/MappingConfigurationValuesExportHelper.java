 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.mapping.helper;

 import org.ikasan.mapping.model.MappingConfiguration;
 import org.ikasan.mapping.model.MappingConfigurationValue;
 import org.ikasan.mapping.model.SourceConfigurationValue;

 import java.text.DateFormat;
 import java.util.*;

 /**
  * @author Ikasan Development Team
  *
  */
 public class MappingConfigurationValuesExportHelper
 {
     private static final String XML_TAG = "<?xml version=\"1.0\"?>";
     private static final String START_TAG = "<mappingConfigurationValues xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
             " xsi:noNamespaceSchemaLocation=\"{$schemaLocation}\">";
     private static final String START_TAG_WITHOUT_SCHEMA = "<mappingConfigurationValues>";
     private static final String END_TAG = "</mappingConfigurationValues>";
     private static final String MAPPING_CONFIGURATION_START_TAG = "<mappingConfigurationValue>";
     private static final String MAPPING_CONFIGURATION_END_TAG = "</mappingConfigurationValue>";
     private static final String SOURCE_CONFIGURATION_VALUES_START_TAG = "<sourceConfigurationValues>";
     private static final String SOURCE_CONFIGURATION_VALUES_END_TAG = "</sourceConfigurationValues>";
     private static final String SOURCE_CONFIGURATION_VALUE_START_TAG = "<sourceConfigurationValue>";
     private static final String SOURCE_CONFIGURATION_VALUE_END_TAG = "</sourceConfigurationValue>";
     private static final String TARGET_CONFIGURATION_VALUE_START_TAG = "<targetConfigurationValue>";
     private static final String TARGET_CONFIGURATION_VALUE_END_TAG = "</targetConfigurationValue>";
     private static final String EXPORT_DATE_TIME_START_TAG = "<exportDateTime>";
     private static final String EXPORT_DATE_TIME_END_TAG = "</exportDateTime>";

     /**
      * @param schemaLocation
      */
     public MappingConfigurationValuesExportHelper()
     {
         super();
     }

     /**
      * Helper method to create the XML export document.
      *
      * @param mappingConfiguration
      * @return
      */
     public String getMappingConfigurationExportXml(MappingConfiguration mappingConfiguration, boolean includeXmlTag,
             String schemaLocation)
     {
         StringBuffer exportString = new StringBuffer();

         if(includeXmlTag)
         {
             exportString.append(XML_TAG);
             String startTag = START_TAG;
             exportString.append(startTag.replace("{$schemaLocation}", schemaLocation));

             exportString.append(EXPORT_DATE_TIME_START_TAG);
             exportString.append(DateFormat.getDateTimeInstance
                 (DateFormat.LONG, DateFormat.LONG).format(new Date()));
             exportString.append(EXPORT_DATE_TIME_END_TAG);
         }
         else
         {
             exportString.append(START_TAG_WITHOUT_SCHEMA);
         }

         List<MappingConfigurationValue> mappingConfigurationValues
             = getMappingConfigurationValues(mappingConfiguration.getSourceConfigurationValues());

         for(MappingConfigurationValue mappingConfigurationValue: mappingConfigurationValues)
         {
             exportString.append(MAPPING_CONFIGURATION_START_TAG).append(SOURCE_CONFIGURATION_VALUES_START_TAG);

             for(SourceConfigurationValue value: mappingConfigurationValue.getSourceConfigurationValues())
             {
                 exportString.append(SOURCE_CONFIGURATION_VALUE_START_TAG).append(value.getSourceSystemValue())
                 .append(SOURCE_CONFIGURATION_VALUE_END_TAG);
             }

             exportString.append(SOURCE_CONFIGURATION_VALUES_END_TAG).append(TARGET_CONFIGURATION_VALUE_START_TAG)
             .append(mappingConfigurationValue.getTargetConfigurationValue().getTargetSystemValue())
             .append(TARGET_CONFIGURATION_VALUE_END_TAG).append(MAPPING_CONFIGURATION_END_TAG);
         }

         exportString.append(END_TAG);

         if(includeXmlTag)
         {
             return XmlFormatter.format(exportString.toString().trim());
         }
         else
         {
             return exportString.toString().trim();
         }
     }

     protected List<MappingConfigurationValue> getMappingConfigurationValues(Set<SourceConfigurationValue> sourceConfigurationValues)
     {
         HashMap<Long, MappingConfigurationValue> map = new HashMap<Long, MappingConfigurationValue>();

         ArrayList<MappingConfigurationValue> oneToOneMappingConfigurationValues = null;

         for(SourceConfigurationValue value: sourceConfigurationValues)
         {
             if(value.getSourceConfigGroupId() == null)
             {
                 if(oneToOneMappingConfigurationValues == null)
                 {
                     oneToOneMappingConfigurationValues = new ArrayList<MappingConfigurationValue>();
                 }
                 MappingConfigurationValue mappingConfigurationValue = new MappingConfigurationValue();
                 mappingConfigurationValue.addSourceConfigurationValue(value);
                 mappingConfigurationValue.setTargetConfigurationValue(value.getTargetConfigurationValue());
                 oneToOneMappingConfigurationValues.add(mappingConfigurationValue);
             }
             else
             {
                 MappingConfigurationValue mappingConfigurationValue = map.get(value.getSourceConfigGroupId());

                 if(mappingConfigurationValue == null)
                 {
                     mappingConfigurationValue = new MappingConfigurationValue();
                     mappingConfigurationValue.addSourceConfigurationValue(value);
                     mappingConfigurationValue.setTargetConfigurationValue(value.getTargetConfigurationValue());
                     map.put(value.getSourceConfigGroupId(), mappingConfigurationValue);
                 }
                 else
                 {
                     mappingConfigurationValue.addSourceConfigurationValue(value);
                 }
             }
         }

         if(oneToOneMappingConfigurationValues != null)
         {
             return oneToOneMappingConfigurationValues;
         }
         else
         {
             return new ArrayList<MappingConfigurationValue>(map.values());
         }
     }
 }
