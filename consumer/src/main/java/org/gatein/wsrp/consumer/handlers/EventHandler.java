/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.wsrp.consumer.handlers;

import org.gatein.common.util.ParameterValidation;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.InstanceContext;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.wsrp.WSRPTypeFactory;
import org.gatein.wsrp.WSRPUtils;
import org.gatein.wsrp.consumer.InvocationHandler;
import org.gatein.wsrp.consumer.WSRPConsumerImpl;
import org.oasis.wsrp.v2.Event;
import org.oasis.wsrp.v2.EventParams;
import org.oasis.wsrp.v2.Extension;
import org.oasis.wsrp.v2.HandleEvents;
import org.oasis.wsrp.v2.HandleEventsFailed;
import org.oasis.wsrp.v2.HandleEventsResponse;
import org.oasis.wsrp.v2.PortletContext;
import org.oasis.wsrp.v2.RuntimeContext;
import org.oasis.wsrp.v2.UpdateResponse;
import org.oasis.wsrp.v2.UserContext;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class EventHandler extends NavigationalStateUpdatingHandler
{
   public EventHandler(WSRPConsumerImpl consumer)
   {
      super(consumer);
   }

   @Override
   protected void updateUserContext(Object request, UserContext userContext)
   {
      getHandleEvents(request).setUserContext(userContext);
   }

   @Override
   protected void updateRegistrationContext(Object request) throws PortletInvokerException
   {
      getHandleEvents(request).setRegistrationContext(consumer.getRegistrationContext());
   }

   @Override
   protected RuntimeContext getRuntimeContextFrom(Object request)
   {
      return getHandleEvents(request).getRuntimeContext();
   }

   @Override
   protected Object performRequest(Object request) throws Exception
   {
      HandleEvents eventRequest = getHandleEvents(request);

      if (InvocationHandler.debug)
      {
         InvocationHandler.log.debug("handleEvents on '" + eventRequest.getPortletContext().getPortletHandle() + "'");
      }

      Holder<List<HandleEventsFailed>> failedEvents = new Holder<List<HandleEventsFailed>>();
      Holder<UpdateResponse> updateResponse = new Holder<UpdateResponse>();
      consumer.getMarkupService().handleEvents(eventRequest.getRegistrationContext(), eventRequest.getPortletContext(),
         eventRequest.getRuntimeContext(), eventRequest.getUserContext(), eventRequest.getMarkupParams(),
         eventRequest.getEventParams(), updateResponse, failedEvents,
         new Holder<List<Extension>>());

      HandleEventsResponse response = WSRPTypeFactory.createHandleEventsReponse();
      response.setUpdateResponse(updateResponse.value);
      if (ParameterValidation.existsAndIsNotEmpty(failedEvents.value))
      {
         response.getFailedEvents().addAll(failedEvents.value);
      }
      return response;
   }

   @Override
   protected Object prepareRequest(InvocationHandler.RequestPrecursor requestPrecursor, PortletInvocation invocation)
   {
      if (!(invocation instanceof EventInvocation))
      {
         throw new IllegalArgumentException("EventHandler can only handle EventInvocations!");
      }

      EventInvocation eventInvocation = (EventInvocation)invocation;

      PortletContext portletContext = requestPrecursor.getPortletContext();
      if (InvocationHandler.debug)
      {
         InvocationHandler.log.debug("Consumer about to attempt action on portlet '" + portletContext.getPortletHandle() + "'");
      }

      // access mode
      InstanceContext instanceContext = invocation.getInstanceContext();
      ParameterValidation.throwIllegalArgExceptionIfNull(instanceContext, "instance context");
      AccessMode accessMode = instanceContext.getAccessMode();
      ParameterValidation.throwIllegalArgExceptionIfNull(accessMode, "access mode");
      if (InvocationHandler.debug)
      {
         InvocationHandler.log.debug("Portlet is requesting " + accessMode + " access mode");
      }

      // events
      QName name = eventInvocation.getName();
      Serializable payload = eventInvocation.getPayload();
      Event event = WSRPTypeFactory.createEvent(name, payload);
      EventParams eventParams = WSRPTypeFactory.createEventParams(Collections.singletonList(event), WSRPUtils.getStateChangeFromAccessMode(accessMode));

      return WSRPTypeFactory.createHandleEvents(portletContext, requestPrecursor.getRuntimeContext(),
         requestPrecursor.getMarkupParams(), eventParams);
   }

   @Override
   protected PortletInvocationResponse processResponse(Object response, PortletInvocation invocation, InvocationHandler.RequestPrecursor requestPrecursor) throws PortletInvokerException
   {
      HandleEventsResponse handleEventsResponse = (HandleEventsResponse)response;

      List<HandleEventsFailed> failed = handleEventsResponse.getFailedEvents();
      if (ParameterValidation.existsAndIsNotEmpty(failed))
      {
         return new ErrorResponse("Couldn't process events: " + failed);
      }

      return processUpdateResponse(invocation, requestPrecursor, handleEventsResponse.getUpdateResponse());
   }

   private HandleEvents getHandleEvents(Object request)
   {
      if (request instanceof HandleEvents)
      {
         return (HandleEvents)request;
      }

      throw new IllegalArgumentException("EventHandler: request is not a HandleEvents request!");
   }
}