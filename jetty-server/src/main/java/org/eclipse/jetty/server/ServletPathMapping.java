//
// ========================================================================
// Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under
// the terms of the Eclipse Public License 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0
//
// This Source Code may also be made available under the following
// Secondary Licenses when the conditions for such availability set
// forth in the Eclipse Public License, v. 2.0 are satisfied:
// the Apache License v2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.server;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;

import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;

public class ServletPathMapping implements HttpServletMapping
{
    private final MappingMatch _mappingMatch;
    private final String _matchValue;
    private final String _pattern;
    private final String _servletName;
    private final String _servletPath;
    private final String _pathInfo;

    public ServletPathMapping(PathSpec pathSpec, String servletName, String pathInContext)
    {
        _servletName = (servletName == null ? "" : servletName);
        _pattern = pathSpec == null ? null : pathSpec.getDeclaration();

        if (pathSpec instanceof ServletPathSpec && pathInContext != null)
        {
            switch (pathSpec.getGroup())
            {
                case ROOT:
                    _mappingMatch = MappingMatch.CONTEXT_ROOT;
                    _matchValue = "";
                    _servletPath = "";
                    _pathInfo = "/";
                    break;

                case DEFAULT:
                    _mappingMatch = MappingMatch.DEFAULT;
                    _matchValue = "";
                    _servletPath = pathInContext;
                    _pathInfo = null;
                    break;

                case EXACT:
                    _mappingMatch = MappingMatch.EXACT;
                    _matchValue = _pattern.substring(1);
                    _servletPath = _pattern;
                    _pathInfo = null;
                    break;

                case PREFIX_GLOB:
                    _mappingMatch = MappingMatch.PATH;
                    _matchValue = pathInContext.substring(1);
                    int split = _pattern.length() - 2;
                    _servletPath = pathSpec.getPrefix();
                    _pathInfo = pathInContext.substring(split);
                    break;

                case SUFFIX_GLOB:
                    _mappingMatch = MappingMatch.EXTENSION;
                    int dot = pathInContext.lastIndexOf('.');
                    _matchValue = pathInContext.substring(1, dot);
                    _servletPath = pathInContext;
                    _pathInfo = null;
                    break;

                case MIDDLE_GLOB:
                    _mappingMatch = null;
                    _matchValue = "";
                    _servletPath = pathInContext;
                    _pathInfo = null;
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
        else
        {
            _mappingMatch = null;
            _matchValue = "";
            _servletPath = pathInContext;
            _pathInfo = null;
        }
    }

    @Override
    public String getMatchValue()
    {
        return _matchValue;
    }

    @Override
    public String getPattern()
    {
        return _pattern;
    }

    @Override
    public String getServletName()
    {
        return _servletName;
    }

    @Override
    public MappingMatch getMappingMatch()
    {
        return _mappingMatch;
    }

    public String getServletPath()
    {
        return _servletPath;
    }

    public String getPathInfo()
    {
        return _pathInfo;
    }

    @Override
    public String toString()
    {
        return "ServletPathMapping{" +
            "matchValue=" + _matchValue +
            ", pattern=" + _pattern +
            ", servletName=" + _servletName +
            ", mappingMatch=" + _mappingMatch +
            ", servletPath=" + _servletPath +
            ", pathInfo=" + _pathInfo +
            "}";
    }
}
