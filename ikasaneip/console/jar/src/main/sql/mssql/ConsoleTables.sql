--
-- $Id:
-- $URL: 
--
-- ====================================================================
-- Ikasan Enterprise Integration Platform
-- 
-- Distributed under the Modified BSD License.
-- Copyright notice: The copyright for this software and a full listing 
-- of individual contributors are as shown in the packaged copyright.txt 
-- file. 
-- 
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without 
-- modification, are permitted provided that the following conditions are met:
--
--  - Redistributions of source code must retain the above copyright notice, 
--    this list of conditions and the following disclaimer.
--
--  - Redistributions in binary form must reproduce the above copyright notice, 
--    this list of conditions and the following disclaimer in the documentation 
--    and/or other materials provided with the distribution.
--
--  - Neither the name of the ORGANIZATION nor the names of its contributors may
--    be used to endorse or promote products derived from this software without 
--    specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
-- DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
-- USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-- ====================================================================
--
-- Author:  Ikasan Development Team
-- 

CREATE TABLE [Module]
(
    [Id]                [NUMERIC] (18) IDENTITY NOT NULL,
    [Name]              [VARCHAR] (255) NOT NULL UNIQUE,
    [Description]       [VARCHAR] (255) NOT NULL,
    [DesignDiagramURL]  [VARCHAR] (255) NULL
    CONSTRAINT [PK_Module_Id] PRIMARY KEY CLUSTERED
    (
        [Id] ASC
    )
)

CREATE TABLE [PointToPointFlowProfile]
(
    [Id]        [NUMERIC] IDENTITY NOT NULL,
    [Name]      [VARCHAR] (255) NOT NULL
    CONSTRAINT [PK_PointToPointFlowProfile_Id] PRIMARY KEY CLUSTERED
    (
        [Id] ASC
    )
)

-- No CONSTRAINT on the FromModuleId or ToModuleId as constraints enforce a Not NULL
-- and we want to allow NULLs
CREATE TABLE [PointToPointFlow]
(
    [Id]                        [NUMERIC] IDENTITY NOT NULL,
    [PointToPointFlowProfileId] [NUMERIC] NOT NULL,
    [FromModuleId]              [NUMERIC] NULL,
    [ToModuleId]                [NUMERIC] NULL,
    CONSTRAINT [PK_PointToPointFlow_Id] PRIMARY KEY CLUSTERED 
    (
        [Id] ASC
    ),
    CONSTRAINT [PTPP_ID_FK] FOREIGN KEY (PointToPointFlowProfileId) REFERENCES PointToPointFlowProfile(Id)
)
