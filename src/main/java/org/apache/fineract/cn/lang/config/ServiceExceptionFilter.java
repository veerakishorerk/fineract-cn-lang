/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.lang.config;

import org.apache.fineract.cn.lang.ServiceError;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

final class ServiceExceptionFilter extends OncePerRequestFilter {

  ServiceExceptionFilter() {
    super();
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (final NestedServletException ex) {
      if (ServiceException.class.isAssignableFrom(ex.getCause().getClass())) {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored") final ServiceException serviceException = ServiceException.class.cast(ex.getCause());
        final ServiceError serviceError = serviceException.serviceError();
        logger.info("Responding with a service error " + serviceError);
        response.sendError(serviceError.getCode(), serviceError.getMessage());
      } else {
        logger.info("Unexpected exception caught " + ex);
        throw ex;
      }
    }
  }
}
